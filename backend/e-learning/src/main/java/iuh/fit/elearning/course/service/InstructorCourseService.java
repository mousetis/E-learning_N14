package iuh.fit.elearning.course.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.catalog.entity.Category;
import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.catalog.repository.CategoryRepository;
import iuh.fit.elearning.catalog.repository.TopicRepository;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.SlugUtils;
import iuh.fit.elearning.course.dto.request.CourseRequest;
import iuh.fit.elearning.course.dto.request.DocumentRequest;
import iuh.fit.elearning.course.dto.request.LessonRequest;
import iuh.fit.elearning.course.dto.request.ProjectDescriptionRequest;
import iuh.fit.elearning.course.dto.request.SectionRequest;
import iuh.fit.elearning.course.dto.response.InstructorCourseDetailResponse;
import iuh.fit.elearning.course.dto.response.InstructorCourseResponse;
import iuh.fit.elearning.course.dto.response.InstructorLessonResponse;
import iuh.fit.elearning.course.dto.response.InstructorSectionResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.CourseBenefit;
import iuh.fit.elearning.course.entity.CourseDocument;
import iuh.fit.elearning.course.entity.CourseProject;
import iuh.fit.elearning.course.entity.Lesson;
import iuh.fit.elearning.course.entity.Section;
import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.mapper.CourseMapper;
import iuh.fit.elearning.course.repository.CourseDocumentRepository;
import iuh.fit.elearning.course.repository.CourseProjectRepository;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.repository.LessonRepository;
import iuh.fit.elearning.course.repository.SectionRepository;
import iuh.fit.elearning.interaction.dto.response.DocumentResponse;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** UC019: giảng viên quản lý khóa học, chương, bài học, tài liệu, dự án; gửi duyệt / ngừng xuất bản. */
@Service
public class InstructorCourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final CourseDocumentRepository documentRepository;
    private final CourseProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseSupport support;
    private final CourseMapper mapper;
    private final StorageService storageService;
    private final NotificationService notificationService;

    public InstructorCourseService(CourseRepository courseRepository, SectionRepository sectionRepository,
                                   LessonRepository lessonRepository, CourseDocumentRepository documentRepository,
                                   CourseProjectRepository projectRepository, CategoryRepository categoryRepository,
                                   TopicRepository topicRepository, EnrollmentRepository enrollmentRepository,
                                   UserRepository userRepository, CourseSupport support, CourseMapper mapper,
                                   StorageService storageService, NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.support = support;
        this.mapper = mapper;
        this.storageService = storageService;
        this.notificationService = notificationService;
    }

    // ------------------------------------------------------------------ khóa học

    @Transactional(readOnly = true)
    public List<InstructorCourseResponse> myCourses(UUID instructorId) {
        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream()
                .map(this::toListItem).toList();
    }

    @Transactional(readOnly = true)
    public InstructorCourseDetailResponse detail(AuthUser user, UUID courseId) {
        return detailOf(requireOwned(user, courseId));
    }

    @Transactional
    public InstructorCourseDetailResponse create(AuthUser user, CourseRequest request) {
        validatePrices(request);
        Course course = Course.builder()
                .instructor(userRepository.getReferenceById(user.id()))
                .category(requireCategory(request.categoryId()))
                .title(request.title().trim())
                .slug(SlugUtils.uniqueSlug(request.title()))
                .status(CourseStatus.DRAFT)
                .build();
        apply(course, request);
        courseRepository.saveAndFlush(course);
        return detailOf(course);
    }

    @Transactional
    public InstructorCourseDetailResponse update(AuthUser user, UUID courseId, CourseRequest request) {
        Course course = requireOwned(user, courseId);
        requireEditable(course);
        validatePrices(request);

        boolean sensitiveChange = course.getStatus() == CourseStatus.PUBLISHED
                && (!course.getTitle().equals(request.title().trim())
                || course.getOriginalPrice().compareTo(request.originalPrice()) != 0
                || course.getSalePrice().compareTo(request.salePrice()) != 0);
        course.setCategory(requireCategory(request.categoryId()));
        apply(course, request);

        // Khóa PUBLISHED đổi tiêu đề / giá phải được duyệt lại (học viên đã mua vẫn học bình thường)
        if (sensitiveChange) {
            course.setStatus(CourseStatus.PENDING_REVIEW);
            course.setRejectionReason(null);
            support.syncInstructorCourseCount(course.getInstructor().getId());
            notifyAdmins(course);
        }
        return detailOf(course);
    }

    @Transactional
    public void delete(AuthUser user, UUID courseId) {
        Course course = requireOwned(user, courseId);
        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Chỉ xóa được khóa học ở trạng thái DRAFT hoặc REJECTED");
        }
        if (enrollmentRepository.existsByCourseId(courseId)) {
            throw new BusinessException(ErrorCode.COURSE_HAS_STUDENTS);
        }
        List<String> files = new ArrayList<>();
        for (Section s : course.getSections()) {
            for (Lesson l : s.getLessons()) {
                files.add(l.getVideoUrl());
            }
        }
        documentRepository.findByCourseId(courseId).forEach(d -> files.add(d.getFileUrl()));
        projectRepository.findByCourseId(courseId).ifPresent(projectRepository::delete);
        documentRepository.deleteAll(documentRepository.findByCourseId(courseId));
        courseRepository.delete(course);
        courseRepository.flush();
        files.forEach(this::deletePrivateFile);
    }

    @Transactional
    public InstructorCourseDetailResponse submitForReview(AuthUser user, UUID courseId) {
        Course course = requireOwned(user, courseId);
        CourseStatus status = course.getStatus();
        if (status != CourseStatus.DRAFT && status != CourseStatus.REJECTED && status != CourseStatus.UNPUBLISHED) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    "Khóa học ở trạng thái " + status + " không thể gửi duyệt");
        }
        List<String> missing = support.missingForReview(course);
        if (!missing.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_READY,
                    "Khóa học còn thiếu: " + String.join("; ", missing));
        }
        course.setStatus(CourseStatus.PENDING_REVIEW);
        course.setRejectionReason(null);
        notifyAdmins(course);
        return detailOf(course);
    }

    @Transactional
    public InstructorCourseDetailResponse unpublish(AuthUser user, UUID courseId) {
        Course course = requireOwned(user, courseId);
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Chỉ ngừng xuất bản được khóa học đang PUBLISHED");
        }
        course.setStatus(CourseStatus.UNPUBLISHED);
        support.syncInstructorCourseCount(course.getInstructor().getId());
        return detailOf(course);
    }

    // ------------------------------------------------------------------ chương

    @Transactional
    public InstructorSectionResponse addSection(AuthUser user, UUID courseId, SectionRequest request) {
        Course course = requireOwned(user, courseId);
        requireEditable(course);
        int index = request.orderIndex() != null ? request.orderIndex() : sectionRepository.maxOrderIndex(courseId) + 1;
        boolean taken = sectionRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .anyMatch(s -> s.getOrderIndex() == index);
        if (taken) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Thứ tự chương " + index + " đã được dùng");
        }
        Section section = sectionRepository.saveAndFlush(Section.builder()
                .course(course)
                .title(request.title().trim())
                .orderIndex(index)
                .build());
        return toSection(section);
    }

    @Transactional
    public InstructorSectionResponse updateSection(AuthUser user, UUID sectionId, SectionRequest request) {
        Section section = findSection(sectionId);
        Course course = requireOwned(user, section.getCourse().getId());
        requireEditable(course);
        if (request.orderIndex() != null && request.orderIndex().intValue() != section.getOrderIndex().intValue()) {
            boolean taken = sectionRepository.findByCourseIdOrderByOrderIndexAsc(course.getId()).stream()
                    .anyMatch(s -> s.getOrderIndex().intValue() == request.orderIndex().intValue() && !s.getId().equals(sectionId));
            if (taken) {
                throw new BusinessException(ErrorCode.DUPLICATE, "Thứ tự chương " + request.orderIndex() + " đã được dùng");
            }
            section.setOrderIndex(request.orderIndex());
        }
        section.setTitle(request.title().trim());
        return toSection(section);
    }

    @Transactional
    public void deleteSection(AuthUser user, UUID sectionId) {
        Section section = findSection(sectionId);
        Course course = requireOwned(user, section.getCourse().getId());
        requireEditable(course);
        List<String> videos = section.getLessons().stream().map(Lesson::getVideoUrl).toList();
        course.getSections().remove(section);
        courseRepository.flush();
        support.refreshTotals(course);
        videos.forEach(this::deletePrivateFile);
    }

    // ------------------------------------------------------------------ bài học

    @Transactional
    public InstructorLessonResponse addLesson(AuthUser user, UUID sectionId, LessonRequest request) {
        Section section = findSection(sectionId);
        Course course = requireOwned(user, section.getCourse().getId());
        requireEditable(course);
        int index = request.orderIndex() != null ? request.orderIndex() : lessonRepository.maxOrderIndex(sectionId) + 1;
        boolean taken = lessonRepository.findBySectionIdOrderByOrderIndexAsc(sectionId).stream()
                .anyMatch(l -> l.getOrderIndex() == index);
        if (taken) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Thứ tự bài học " + index + " đã được dùng");
        }
        Lesson lesson = Lesson.builder()
                .section(section)
                .title(request.title().trim())
                .videoUrl(requireVideoRef(request.videoUrl()))
                .durationSeconds(request.durationSeconds() == null ? 0 : request.durationSeconds())
                .orderIndex(index)
                .preview(request.preview() != null && request.preview())
                .build();
        lessonRepository.saveAndFlush(lesson);
        support.refreshTotals(course);
        return toLesson(lesson);
    }

    @Transactional
    public InstructorLessonResponse updateLesson(AuthUser user, UUID lessonId, LessonRequest request) {
        Lesson lesson = findLesson(lessonId);
        Course course = requireOwned(user, lesson.getSection().getCourse().getId());
        requireEditable(course);
        if (request.orderIndex() != null && request.orderIndex().intValue() != lesson.getOrderIndex().intValue()) {
            boolean taken = lessonRepository.findBySectionIdOrderByOrderIndexAsc(lesson.getSection().getId()).stream()
                    .anyMatch(l -> l.getOrderIndex().intValue() == request.orderIndex().intValue() && !l.getId().equals(lessonId));
            if (taken) {
                throw new BusinessException(ErrorCode.DUPLICATE, "Thứ tự bài học " + request.orderIndex() + " đã được dùng");
            }
            lesson.setOrderIndex(request.orderIndex());
        }
        String oldVideo = lesson.getVideoUrl();
        String newVideo = requireVideoRef(request.videoUrl());
        lesson.setTitle(request.title().trim());
        lesson.setVideoUrl(newVideo);
        if (request.durationSeconds() != null) {
            lesson.setDurationSeconds(request.durationSeconds());
        }
        if (request.preview() != null) {
            lesson.setPreview(request.preview());
        }
        lessonRepository.flush();
        support.refreshTotals(course);
        if (oldVideo != null && !oldVideo.equals(newVideo)) {
            deletePrivateFile(oldVideo);
        }
        return toLesson(lesson);
    }

    @Transactional
    public void deleteLesson(AuthUser user, UUID lessonId) {
        Lesson lesson = findLesson(lessonId);
        Course course = requireOwned(user, lesson.getSection().getCourse().getId());
        requireEditable(course);
        String video = lesson.getVideoUrl();
        lesson.getSection().getLessons().remove(lesson);
        courseRepository.flush();
        support.refreshTotals(course);
        deletePrivateFile(video);
    }

    // ------------------------------------------------------------------ tài liệu, dự án

    @Transactional
    public DocumentResponse addDocument(AuthUser user, UUID courseId, DocumentRequest request) {
        Course course = requireOwned(user, courseId);
        String key = storageService.normalizeReference(request.fileUrl());
        if (key == null || !key.startsWith("documents/")) {
            throw BusinessException.badRequest(
                    "Tài liệu không hợp lệ: hãy tải lên qua POST /api/v1/files với type=DOCUMENT");
        }
        String fileType = request.fileType();
        if (fileType == null || fileType.isBlank()) {
            int dot = key.lastIndexOf('.');
            fileType = dot < 0 ? null : key.substring(dot + 1).toLowerCase();
        }
        CourseDocument doc = documentRepository.saveAndFlush(CourseDocument.builder()
                .course(course)
                .fileName(request.fileName().trim())
                .fileUrl(key)
                .fileSize(request.fileSize())
                .fileType(fileType)
                .build());
        return new DocumentResponse(doc.getId(), doc.getFileName(), doc.getFileType(), doc.getFileSize());
    }

    @Transactional
    public void deleteDocument(AuthUser user, UUID courseId, UUID documentId) {
        requireOwned(user, courseId);
        CourseDocument doc = documentRepository.findByIdAndCourseId(documentId, courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy tài liệu"));
        documentRepository.delete(doc);
        documentRepository.flush();
        deletePrivateFile(doc.getFileUrl());
    }

    @Transactional
    public InstructorCourseDetailResponse updateProject(AuthUser user, UUID courseId, ProjectDescriptionRequest request) {
        Course course = requireOwned(user, courseId);
        CourseProject project = projectRepository.findByCourseId(courseId)
                .orElseGet(() -> CourseProject.builder().course(course).build());
        project.setDescription(request.description().trim());
        projectRepository.save(project);
        return detailOf(course);
    }

    // ------------------------------------------------------------------ dùng chung (admin gọi lại detailOf)

    @Transactional(readOnly = true)
    public InstructorCourseDetailResponse detailOf(Course c) {
        List<InstructorSectionResponse> sections = c.getSections().stream().map(this::toSection).toList();
        List<DocumentResponse> documents = documentRepository.findByCourseId(c.getId()).stream()
                .map(d -> new DocumentResponse(d.getId(), d.getFileName(), d.getFileType(), d.getFileSize())).toList();
        String projectDescription = projectRepository.findByCourseId(c.getId()).map(CourseProject::getDescription)
                .orElse(null);
        return InstructorCourseDetailResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .slug(c.getSlug())
                .description(c.getDescription())
                .thumbnailUrl(storageService.resolveUrl(c.getThumbnailUrl()))
                .introVideoUrl(storageService.resolveUrl(c.getIntroVideoUrl()))
                .category(mapper.toCategoryBrief(c.getCategory()))
                .topics(c.getTopics().stream().map(mapper::toTopicBrief).toList())
                .level(c.getLevel())
                .originalPrice(c.getOriginalPrice())
                .salePrice(c.getSalePrice())
                .status(c.getStatus())
                .rejectionReason(c.getRejectionReason())
                .publishedAt(c.getPublishedAt())
                .studentCount(c.getStudentCount())
                .avgRating(c.getAvgRating())
                .reviewCount(c.getReviewCount())
                .totalLessons(c.getTotalLessons())
                .totalDurationSeconds(c.getTotalDurationSeconds())
                .instructor(mapper.toInstructorBrief(c.getInstructor()))
                .benefits(c.getBenefits().stream().map(mapper::toBenefit).toList())
                .sections(sections)
                .documents(documents)
                .projectDescription(projectDescription)
                .missingForReview(support.missingForReview(c))
                .build();
    }

    @Transactional(readOnly = true)
    public InstructorCourseResponse toListItem(Course c) {
        return new InstructorCourseResponse(c.getId(), c.getTitle(), c.getSlug(),
                storageService.resolveUrl(c.getThumbnailUrl()), c.getStatus(), c.getRejectionReason(),
                c.getOriginalPrice(), c.getSalePrice(), c.getStudentCount(), c.getAvgRating(), c.getReviewCount(),
                c.getTotalLessons(), c.getTotalDurationSeconds(), c.getPublishedAt(), c.getCreatedAt(), c.getUpdatedAt());
    }

    // ------------------------------------------------------------------ nội bộ

    private void apply(Course course, CourseRequest r) {
        course.setTitle(r.title().trim());
        course.setDescription(r.description() == null || r.description().isBlank() ? null : r.description().trim());
        course.setLevel(r.level() == null ? CourseLevel.ALL_LEVELS : r.level());
        course.setThumbnailUrl(storageService.requireImageRef(r.thumbnailUrl()));
        course.setIntroVideoUrl(requireVideoRef(r.introVideoUrl()));
        course.setOriginalPrice(r.originalPrice());
        course.setSalePrice(r.salePrice());

        Set<Topic> topics = new HashSet<>();
        if (r.topicIds() != null && !r.topicIds().isEmpty()) {
            List<Topic> found = topicRepository.findAllById(r.topicIds().stream().distinct().toList());
            if (found.size() != r.topicIds().stream().distinct().count()) {
                throw BusinessException.badRequest("Có chủ đề không tồn tại");
            }
            topics.addAll(found);
        }
        course.getTopics().clear();
        course.getTopics().addAll(topics);

        course.getBenefits().clear();
        if (r.benefits() != null) {
            int order = 1;
            for (CourseRequest.BenefitRequest b : r.benefits()) {
                course.getBenefits().add(CourseBenefit.builder()
                        .course(course)
                        .content(b.content().trim())
                        .icon(b.icon() == null || b.icon().isBlank() ? null : b.icon().trim())
                        .sortOrder(order++)
                        .build());
            }
        }
    }

    private void validatePrices(CourseRequest r) {
        if (r.salePrice().compareTo(r.originalPrice()) > 0) {
            throw BusinessException.badRequest("Giá bán không được lớn hơn giá gốc");
        }
    }

    private Category requireCategory(UUID id) {
        return categoryRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .orElseThrow(() -> BusinessException.badRequest("Danh mục không tồn tại hoặc đã bị ẩn"));
    }

    private Course requireOwned(AuthUser user, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        if (!course.getInstructor().getId().equals(user.id())) {
            throw BusinessException.forbidden("Bạn không phải giảng viên của khóa học này");
        }
        return course;
    }

    private void requireEditable(Course course) {
        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Khóa học đang chờ duyệt, không thể chỉnh sửa");
        }
    }

    private Section findSection(UUID id) {
        return sectionRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy chương"));
    }

    private Lesson findLesson(UUID id) {
        return lessonRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy bài học"));
    }

    /** Video: key tải lên qua /files (videos/...) hoặc URL http(s) bên ngoài. */
    private String requireVideoRef(String ref) {
        String key = storageService.normalizeReference(ref);
        if (key == null || key.isBlank()) {
            return null;
        }
        String lower = key.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || key.startsWith("videos/")) {
            return key;
        }
        throw BusinessException.badRequest("Video không hợp lệ: hãy tải lên qua POST /api/v1/files với type=VIDEO");
    }

    /** Chỉ xóa tệp do hệ thống lưu (videos/, documents/); bỏ qua URL ngoài. */
    private void deletePrivateFile(String stored) {
        if (stored != null && (stored.startsWith("videos/") || stored.startsWith("documents/"))) {
            try {
                storageService.delete(stored);
            } catch (RuntimeException ignored) {
                // tệp mồ côi không ảnh hưởng nghiệp vụ
            }
        }
    }

    private void notifyAdmins(Course course) {
        List<UUID> admins = userRepository.findIdsByRoleAndStatus(UserRole.ADMIN, UserStatus.ACTIVE);
        notificationService.notifyUsers(admins, NotificationType.COURSE_REVIEW, "Khóa học chờ duyệt",
                "Khóa học \"" + course.getTitle() + "\" đang chờ được duyệt", course.getId());
    }

    private InstructorSectionResponse toSection(Section s) {
        return new InstructorSectionResponse(s.getId(), s.getTitle(), s.getOrderIndex(),
                s.getLessons().stream().map(this::toLesson).toList());
    }

    private InstructorLessonResponse toLesson(Lesson l) {
        return new InstructorLessonResponse(l.getId(), l.getTitle(), l.getOrderIndex(), l.getDurationSeconds(),
                Boolean.TRUE.equals(l.getPreview()), l.getVideoUrl() != null && !l.getVideoUrl().isBlank(),
                l.getVideoUrl());
    }
}
