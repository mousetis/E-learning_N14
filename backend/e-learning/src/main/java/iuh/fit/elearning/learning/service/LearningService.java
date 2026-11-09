package iuh.fit.elearning.learning.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.dto.response.CurriculumResponse;
import iuh.fit.elearning.course.dto.response.SectionResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.Lesson;
import iuh.fit.elearning.course.repository.LessonRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.learning.dto.response.EnrollmentResponse;
import iuh.fit.elearning.learning.dto.response.LearnCourseResponse;
import iuh.fit.elearning.learning.dto.response.ProgressResponse;
import iuh.fit.elearning.learning.dto.response.StreamResponse;
import iuh.fit.elearning.learning.entity.Certificate;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.LessonProgress;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.CertificateRepository;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.learning.repository.LessonProgressRepository;
import iuh.fit.elearning.storage.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** UC011 (học bài, tiến độ) và UC012 (khóa học của tôi). */
@Service
public class LearningService {

    /** Bài học được tính là hoàn thành khi xem đến 90% thời lượng (SDS 7.8.2). */
    private static final double COMPLETION_RATIO = 0.9;

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final CertificateRepository certificateRepository;
    private final CourseAccessService accessService;
    private final CourseService courseService;
    private final CertificateService certificateService;
    private final RefundService refundService;
    private final StorageService storageService;

    public LearningService(EnrollmentRepository enrollmentRepository, LessonProgressRepository progressRepository,
                           LessonRepository lessonRepository, CertificateRepository certificateRepository,
                           CourseAccessService accessService, CourseService courseService,
                           CertificateService certificateService, RefundService refundService,
                           StorageService storageService) {
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.lessonRepository = lessonRepository;
        this.certificateRepository = certificateRepository;
        this.accessService = accessService;
        this.courseService = courseService;
        this.certificateService = certificateService;
        this.refundService = refundService;
        this.storageService = storageService;
    }

    // ------------------------------------------------------------------ UC012: khóa học của tôi

    /** status = ALL (mặc định, không gồm đã hoàn tiền) | ONGOING | COMPLETED. */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> myEnrollments(UUID userId, String status) {
        String s = status == null || status.isBlank() ? "ALL" : status.trim().toUpperCase();
        List<Enrollment> enrollments = switch (s) {
            case "ALL" -> enrollmentRepository.findByUserIdAndStatusNotOrderByLastAccessedAtDesc(userId,
                    EnrollmentStatus.REFUNDED);
            case "ONGOING" -> enrollmentRepository.findByUserIdAndStatusOrderByLastAccessedAtDesc(userId,
                    EnrollmentStatus.ONGOING);
            case "COMPLETED" -> enrollmentRepository.findByUserIdAndStatusOrderByLastAccessedAtDesc(userId,
                    EnrollmentStatus.COMPLETED);
            default -> throw BusinessException.badRequest("status phải là ALL, ONGOING hoặc COMPLETED");
        };
        if (enrollments.isEmpty()) {
            return List.of();
        }
        List<Enrollment> sorted = enrollments.stream()
                .sorted(Comparator.comparing(
                        (Enrollment e) -> e.getLastAccessedAt() != null ? e.getLastAccessedAt() : e.getEnrolledAt())
                        .reversed())
                .toList();

        Map<UUID, CourseSummaryResponse> summaries = new HashMap<>();
        for (CourseSummaryResponse c : courseService.summarize(sorted.stream().map(Enrollment::getCourse).toList(), userId)) {
            summaries.put(c.id(), c);
        }
        Map<UUID, String> certificates = new HashMap<>();
        for (Certificate c : certificateRepository.findByEnrollmentUserIdOrderByIssuedAtDesc(userId)) {
            certificates.put(c.getEnrollment().getId(), c.getCertificateCode());
        }
        return sorted.stream().map(e -> new EnrollmentResponse(e.getId(), summaries.get(e.getCourse().getId()),
                e.getProgressPercentage(), e.getStatus(), e.getEnrolledAt(), e.getCompletedAt(), e.getLastAccessedAt(),
                certificates.get(e.getId()), refundService.ineligibleReason(e) == null,
                e.getEnrolledAt().plus(RefundService.REFUND_WINDOW))).toList();
    }

    // ------------------------------------------------------------------ UC011: học bài

    @Transactional(readOnly = true)
    public LearnCourseResponse learnCourse(AuthUser user, UUID courseId) {
        Course course = accessService.requireContentAccess(user, courseId);
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.id(), courseId)
                .filter(e -> e.getStatus() != EnrollmentStatus.REFUNDED).orElse(null);
        CurriculumResponse curriculum = courseService.curriculum(courseId, user);

        UUID resume = null;
        String certificateCode = null;
        if (enrollment != null) {
            resume = progressRepository.findByEnrollmentId(enrollment.getId()).stream()
                    .max(Comparator.comparing(LessonProgress::getUpdatedAt))
                    .map(p -> p.getLesson().getId()).orElse(null);
            certificateCode = certificateRepository.findByEnrollmentId(enrollment.getId())
                    .map(Certificate::getCertificateCode).orElse(null);
        }
        if (resume == null) {
            resume = curriculum.sections().stream().map(SectionResponse::lessons).flatMap(List::stream)
                    .findFirst().map(l -> l.id()).orElse(null);
        }
        return new LearnCourseResponse(courseId, course.getTitle(), enrollment == null ? null : enrollment.getId(),
                enrollment == null ? null : enrollment.getProgressPercentage(),
                enrollment == null ? null : enrollment.getStatus(), certificateCode, resume, curriculum);
    }

    @Transactional
    public StreamResponse stream(AuthUser user, UUID lessonId) {
        Lesson lesson = findLesson(lessonId);
        UUID courseId = lesson.getSection().getCourse().getId();
        accessService.requireContentAccess(user, courseId);
        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()) {
            throw BusinessException.notFound("Bài học chưa có video");
        }
        int lastPosition = 0;
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.id(), courseId)
                .filter(e -> e.getStatus() != EnrollmentStatus.REFUNDED).orElse(null);
        if (enrollment != null) {
            enrollment.setLastAccessedAt(Instant.now());
            lastPosition = progressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                    .map(LessonProgress::getLastPositionSeconds).orElse(0);
        }
        Duration ttl = storageService.defaultSignedUrlTtl();
        return new StreamResponse(lesson.getId(), lesson.getTitle(),
                storageService.createSignedUrl(lesson.getVideoUrl(), ttl), Instant.now().plus(ttl), lastPosition,
                lesson.getDurationSeconds());
    }

    /**
     * SDS 7.8.2: ghi vị trí xem; bài học hoàn thành một lần khi position ≥ 90% thời lượng (không bỏ đánh dấu);
     * tiến độ khóa = số bài hoàn thành / tổng số bài; đạt 100% thì chuyển COMPLETED và cấp chứng chỉ.
     */
    @Transactional
    public ProgressResponse updateProgress(AuthUser user, UUID lessonId, int positionSeconds) {
        Lesson lesson = findLesson(lessonId);
        Course course = lesson.getSection().getCourse();
        Enrollment enrollment = accessService.requireEnrollment(user.id(), course.getId());

        int duration = lesson.getDurationSeconds();
        int position = duration > 0 ? Math.min(positionSeconds, duration) : positionSeconds;
        Instant now = Instant.now();

        LessonProgress progress = progressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElseGet(() -> LessonProgress.builder().enrollment(enrollment).lesson(lesson).build());
        progress.setLastPositionSeconds(position);
        if (!Boolean.TRUE.equals(progress.getCompleted()) && position >= COMPLETION_RATIO * duration) {
            progress.setCompleted(true);
            progress.setCompletedAt(now);
        }
        progressRepository.saveAndFlush(progress);

        long totalLessons = lessonRepository.countBySectionCourseId(course.getId());
        long completed = progressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());
        BigDecimal percentage = totalLessons == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalLessons), 2, RoundingMode.HALF_UP)
                .min(BigDecimal.valueOf(100));
        enrollment.setProgressPercentage(percentage);
        enrollment.setLastAccessedAt(now);

        String certificateCode = null;
        if (percentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(now);
            }
            certificateCode = certificateService.issue(enrollment).getCertificateCode();
        } else if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            // Chứng chỉ đã cấp không bị thu hồi khi giảng viên thêm bài mới
            certificateCode = certificateRepository.findByEnrollmentId(enrollment.getId())
                    .map(Certificate::getCertificateCode).orElse(null);
        }
        return new ProgressResponse(Boolean.TRUE.equals(progress.getCompleted()), percentage,
                enrollment.getStatus() == EnrollmentStatus.COMPLETED, certificateCode);
    }

    private Lesson findLesson(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy bài học"));
    }
}
