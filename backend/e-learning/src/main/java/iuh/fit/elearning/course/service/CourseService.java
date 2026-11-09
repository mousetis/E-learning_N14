package iuh.fit.elearning.course.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.cart.repository.CartItemRepository;
import iuh.fit.elearning.catalog.entity.Category;
import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.catalog.repository.CategoryRepository;
import iuh.fit.elearning.catalog.repository.TopicRepository;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.dto.response.CourseDetailResponse;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.dto.response.CurriculumResponse;
import iuh.fit.elearning.course.dto.response.InstructorInfoResponse;
import iuh.fit.elearning.course.dto.response.LessonPreviewResponse;
import iuh.fit.elearning.course.dto.response.LessonResponse;
import iuh.fit.elearning.course.dto.response.SectionResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.Lesson;
import iuh.fit.elearning.course.entity.Section;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.mapper.CourseMapper;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.repository.CourseSpecifications;
import iuh.fit.elearning.course.repository.LessonRepository;
import iuh.fit.elearning.interaction.repository.CourseLikeRepository;
import iuh.fit.elearning.interaction.repository.FollowRepository;
import iuh.fit.elearning.interaction.repository.SavedCourseRepository;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.LessonProgress;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.learning.repository.LessonProgressRepository;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.entity.InstructorProfile;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Xem / tìm kiếm khóa học công khai: UC004, UC005, UC006 (và dữ liệu dùng chung cho UC007, UC008, UC012).
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final SavedCourseRepository savedCourseRepository;
    private final CartItemRepository cartItemRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final FollowRepository followRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final CourseMapper mapper;
    private final StorageService storageService;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,
                         TopicRepository topicRepository, LessonRepository lessonRepository,
                         EnrollmentRepository enrollmentRepository, LessonProgressRepository lessonProgressRepository,
                         SavedCourseRepository savedCourseRepository, CartItemRepository cartItemRepository,
                         CourseLikeRepository courseLikeRepository, FollowRepository followRepository,
                         InstructorProfileRepository instructorProfileRepository, CourseMapper mapper,
                         StorageService storageService) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.savedCourseRepository = savedCourseRepository;
        this.cartItemRepository = cartItemRepository;
        this.courseLikeRepository = courseLikeRepository;
        this.followRepository = followRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.mapper = mapper;
        this.storageService = storageService;
    }

    // ------------------------------------------------------------------ UC004: tìm kiếm, lọc

    /** Không đánh dấu readOnly vì có ghi nhận lượt tìm kiếm của chủ đề (topics.search_count). */
    @Transactional
    public PageResponse<CourseSummaryResponse> search(CourseSearchCriteria criteria, int page, int size, UUID viewerId) {
        Specification<Course> spec = CourseSpecifications.publiclyVisible();
        if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
            spec = spec.and(CourseSpecifications.keyword(criteria.keyword()));
            topicRepository.findByNameIgnoreCase(criteria.keyword().trim())
                    .ifPresent(t -> t.setSearchCount(t.getSearchCount() + 1));
        }
        if (criteria.categoryId() != null) {
            spec = spec.and(CourseSpecifications.category(criteria.categoryId()));
        }
        if (criteria.minPrice() != null) {
            spec = spec.and(CourseSpecifications.minPrice(criteria.minPrice()));
        }
        if (criteria.maxPrice() != null) {
            spec = spec.and(CourseSpecifications.maxPrice(criteria.maxPrice()));
        }
        if (criteria.minRating() != null) {
            spec = spec.and(CourseSpecifications.minRating(criteria.minRating()));
        }
        if (criteria.level() != null) {
            spec = spec.and(CourseSpecifications.level(criteria.level()));
        }
        if (criteria.duration() != null && !criteria.duration().isBlank()) {
            spec = spec.and(CourseSpecifications.duration(criteria.duration()));
        }
        Pageable pageable = PageUtils.of(page, size, CourseSpecifications.sortOf(criteria.sort()));
        Page<Course> result = courseRepository.findAll(spec, pageable);
        return PageResponse.of(result, summarize(result.getContent(), viewerId));
    }

    /** UC004: khóa học phổ biến (từ khóa rỗng). */
    @Transactional(readOnly = true)
    public PageResponse<CourseSummaryResponse> popular(int page, int size, UUID viewerId) {
        Pageable pageable = PageUtils.of(page, size, CourseSpecifications.sortOf("popular"));
        Page<Course> result = courseRepository.findAll(CourseSpecifications.publiclyVisible(), pageable);
        return PageResponse.of(result, summarize(result.getContent(), viewerId));
    }

    /** Gợi ý cho bạn: khóa học được đánh giá cao và mới xuất bản. */
    @Transactional(readOnly = true)
    public PageResponse<CourseSummaryResponse> forYou(int page, int size, UUID viewerId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "avgRating", "publishedAt");
        Page<Course> result = courseRepository.findAll(CourseSpecifications.publiclyVisible(),
                PageUtils.of(page, size, sort));
        return PageResponse.of(result, summarize(result.getContent(), viewerId));
    }

    // ------------------------------------------------------------------ UC005: theo danh mục

    @Transactional(readOnly = true)
    public PageResponse<CourseSummaryResponse> byCategory(String slug, String sort, int page, int size, UUID viewerId) {
        Category category = categoryRepository.findBySlug(slug)
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy danh mục"));
        Specification<Course> spec = CourseSpecifications.publiclyVisible()
                .and(CourseSpecifications.category(category.getId()));
        Page<Course> result = courseRepository.findAll(spec, PageUtils.of(page, size, CourseSpecifications.sortOf(sort)));
        return PageResponse.of(result, summarize(result.getContent(), viewerId));
    }

    // ------------------------------------------------------------------ UC006: chi tiết

    @Transactional(readOnly = true)
    public CourseDetailResponse detail(UUID courseId, AuthUser viewer) {
        Course course = requireViewable(courseId, viewer);
        UUID viewerId = AuthUser.idOf(viewer);
        User instructor = course.getInstructor();
        InstructorProfile profile = instructorProfileRepository.findByUserId(instructor.getId()).orElse(null);

        CourseDetailResponse.CourseDetailResponseBuilder b = CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .thumbnailUrl(storageService.resolveUrl(course.getThumbnailUrl()))
                .introVideoUrl(storageService.resolveUrl(course.getIntroVideoUrl()))
                .category(mapper.toCategoryBrief(course.getCategory()))
                .topics(course.getTopics().stream().map(mapper::toTopicBrief).toList())
                .level(course.getLevel())
                .originalPrice(course.getOriginalPrice())
                .salePrice(course.getSalePrice())
                .discountPercent(CourseMapper.discountPercent(course.getOriginalPrice(), course.getSalePrice()))
                .avgRating(course.getAvgRating())
                .reviewCount(course.getReviewCount())
                .studentCount(course.getStudentCount())
                .likeCount(course.getLikeCount())
                .totalLessons(course.getTotalLessons())
                .totalDurationSeconds(course.getTotalDurationSeconds())
                .status(course.getStatus())
                .publishedAt(course.getPublishedAt())
                .benefits(course.getBenefits().stream().map(mapper::toBenefit).toList());

        InstructorInfoResponse.InstructorInfoResponseBuilder ib = InstructorInfoResponse.builder()
                .id(instructor.getId())
                .fullName(instructor.getFullName())
                .avatarUrl(storageService.resolveUrl(instructor.getAvatarUrl()))
                .jobTitle(instructor.getJobTitle());
        if (profile != null) {
            ib.expertise(profile.getExpertise())
                    .followerCount(profile.getFollowerCount())
                    .studentCount(profile.getStudentCount())
                    .courseCount(profile.getCourseCount())
                    .avgRating(profile.getAvgRating());
        }

        if (viewerId != null) {
            Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(viewerId, courseId).orElse(null);
            boolean owned = enrollment != null && enrollment.getStatus() != EnrollmentStatus.REFUNDED;
            b.owned(owned)
                    .saved(savedCourseRepository.existsByUserIdAndCourseId(viewerId, courseId))
                    .inCart(cartItemRepository.existsByCartUserIdAndCourseId(viewerId, courseId))
                    .liked(courseLikeRepository.findByCourseIdAndUserId(courseId, viewerId).isPresent());
            if (owned) {
                b.enrollmentId(enrollment.getId())
                        .progressPercentage(enrollment.getProgressPercentage())
                        .enrollmentStatus(enrollment.getStatus());
            }
            ib.following(followRepository.existsByFollowerIdAndInstructorId(viewerId, instructor.getId()));
        }
        return b.instructor(ib.build()).build();
    }

    @Transactional(readOnly = true)
    public CurriculumResponse curriculum(UUID courseId, AuthUser viewer) {
        Course course = requireViewable(courseId, viewer);
        UUID viewerId = AuthUser.idOf(viewer);

        Enrollment enrollment = viewerId == null ? null
                : enrollmentRepository.findByUserIdAndCourseId(viewerId, courseId).orElse(null);
        boolean owned = enrollment != null && enrollment.getStatus() != EnrollmentStatus.REFUNDED;
        boolean privileged = viewer != null && (viewer.isAdmin() || course.getInstructor().getId().equals(viewerId));

        Map<UUID, LessonProgress> progressByLesson = new HashMap<>();
        if (owned) {
            for (LessonProgress p : lessonProgressRepository.findByEnrollmentId(enrollment.getId())) {
                progressByLesson.put(p.getLesson().getId(), p);
            }
        }

        List<SectionResponse> sections = course.getSections().stream().map(section -> {
            List<LessonResponse> lessons = section.getLessons().stream().map(lesson -> {
                LessonProgress progress = progressByLesson.get(lesson.getId());
                boolean locked = !(Boolean.TRUE.equals(lesson.getPreview()) || owned || privileged);
                return new LessonResponse(lesson.getId(), lesson.getTitle(), lesson.getOrderIndex(),
                        lesson.getDurationSeconds(), Boolean.TRUE.equals(lesson.getPreview()), locked,
                        owned ? (progress != null && Boolean.TRUE.equals(progress.getCompleted())) : null,
                        owned ? (progress == null ? 0 : progress.getLastPositionSeconds()) : null);
            }).toList();
            int duration = section.getLessons().stream().mapToInt(Lesson::getDurationSeconds).sum();
            return new SectionResponse(section.getId(), section.getTitle(), section.getOrderIndex(),
                    lessons.size(), duration, lessons);
        }).toList();

        int totalLessons = sections.stream().mapToInt(SectionResponse::lessonCount).sum();
        int totalDuration = sections.stream().mapToInt(SectionResponse::durationSeconds).sum();
        return new CurriculumResponse(course.getId(), sections.size(), totalLessons, totalDuration, sections);
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryResponse> similar(UUID courseId, int limit, UUID viewerId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        List<Course> similar = courseRepository.findSimilar(course.getCategory().getId(), courseId,
                CourseStatus.PUBLISHED, UserStatus.ACTIVE, PageRequest.of(0, Math.min(Math.max(limit, 1), 20)));
        return summarize(similar, viewerId);
    }

    /** Signed URL video của bài học thử (public). */
    @Transactional(readOnly = true)
    public LessonPreviewResponse previewLesson(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy bài học"));
        Course course = lesson.getSection().getCourse();
        if (!Boolean.TRUE.equals(lesson.getPreview()) || !isPubliclyVisible(course)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Bài học này cần mua khóa học để xem");
        }
        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()) {
            throw BusinessException.notFound("Bài học chưa có video");
        }
        var ttl = storageService.defaultSignedUrlTtl();
        return new LessonPreviewResponse(lesson.getId(), lesson.getTitle(),
                storageService.createSignedUrl(lesson.getVideoUrl(), ttl), Instant.now().plus(ttl));
    }

    // ------------------------------------------------------------------ dùng chung cho module khác

    /** Khóa học PUBLISHED của một giảng viên (hồ sơ giảng viên - UC007). */
    @Transactional(readOnly = true)
    public List<CourseSummaryResponse> publishedByInstructor(UUID instructorId, UUID viewerId) {
        return summarize(courseRepository.findByInstructorAndStatus(instructorId, CourseStatus.PUBLISHED), viewerId);
    }

    /**
     * Chuyển danh sách entity thành DTO tóm tắt: gắn nhãn Best-seller (top 10% theo số học viên trong danh mục)
     * và cờ saved / owned của người xem (nếu có).
     */
    @Transactional(readOnly = true)
    public List<CourseSummaryResponse> summarize(List<Course> courses, UUID viewerId) {
        if (courses.isEmpty()) {
            return List.of();
        }
        Map<UUID, Integer> thresholds = new HashMap<>();
        for (Course c : courses) {
            UUID categoryId = c.getCategory().getId();
            if (!thresholds.containsKey(categoryId)) {
                thresholds.put(categoryId, bestSellerThreshold(categoryId));
            }
        }
        List<UUID> ids = courses.stream().map(Course::getId).toList();
        final Set<UUID> savedIds = viewerId == null ? Set.of()
                : new HashSet<>(savedCourseRepository.findSavedCourseIds(viewerId, ids));
        final Set<UUID> ownedIds = viewerId == null ? Set.of()
                : new HashSet<>(enrollmentRepository.findOwnedCourseIds(viewerId, ids, EnrollmentStatus.REFUNDED));
        return courses.stream().map(c -> {
            int threshold = thresholds.get(c.getCategory().getId());
            boolean best = c.getStudentCount() > 0 && c.getStudentCount() >= threshold;
            return mapper.toSummary(c, best,
                    viewerId == null ? null : savedIds.contains(c.getId()),
                    viewerId == null ? null : ownedIds.contains(c.getId()));
        }).collect(Collectors.toList());
    }

    /** Khóa học hiển thị công khai: PUBLISHED và giảng viên còn ACTIVE. */
    public static boolean isPubliclyVisible(Course course) {
        return course.getStatus() == CourseStatus.PUBLISHED
                && course.getInstructor().getStatus() == UserStatus.ACTIVE;
    }

    /**
     * Lấy khóa học mà người xem được phép xem: công khai, hoặc người xem là giảng viên của khóa / quản trị viên /
     * học viên đã sở hữu (khóa ngừng xuất bản vẫn cho người đã mua xem).
     */
    @Transactional(readOnly = true)
    public Course requireViewable(UUID courseId, AuthUser viewer) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_AVAILABLE, "Khóa học không còn khả dụng"));
        if (isPubliclyVisible(course)) {
            return course;
        }
        if (viewer != null) {
            boolean privileged = viewer.isAdmin() || course.getInstructor().getId().equals(viewer.id());
            boolean owned = enrollmentRepository.existsByUserIdAndCourseIdAndStatusNot(
                    viewer.id(), courseId, EnrollmentStatus.REFUNDED);
            if (privileged || owned) {
                return course;
            }
        }
        throw new BusinessException(ErrorCode.COURSE_NOT_AVAILABLE, "Khóa học không còn khả dụng");
    }

    private int bestSellerThreshold(UUID categoryId) {
        long total = courseRepository.countByCategoryIdAndStatus(categoryId, CourseStatus.PUBLISHED);
        if (total == 0) {
            return Integer.MAX_VALUE;
        }
        int k = (int) Math.max(1, Math.ceil(total * 0.1));
        List<Integer> top = courseRepository.findTopStudentCounts(categoryId, CourseStatus.PUBLISHED,
                PageRequest.of(0, k));
        return top.isEmpty() ? Integer.MAX_VALUE : top.get(top.size() - 1);
    }
}
