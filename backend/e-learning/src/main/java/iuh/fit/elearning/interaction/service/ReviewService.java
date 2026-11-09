package iuh.fit.elearning.interaction.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.interaction.dto.request.ReviewRequest;
import iuh.fit.elearning.interaction.dto.response.ReviewListResponse;
import iuh.fit.elearning.interaction.dto.response.ReviewResponse;
import iuh.fit.elearning.interaction.entity.Review;
import iuh.fit.elearning.interaction.mapper.InteractionMapper;
import iuh.fit.elearning.interaction.repository.ReviewRepository;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.service.CourseAccessService;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** UC015: đánh giá khóa học. */
@Service
public class ReviewService {

    /** Tiến độ tối thiểu để được đánh giá (%). */
    private static final BigDecimal MIN_PROGRESS = BigDecimal.valueOf(20);

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final UserRepository userRepository;
    private final CourseAccessService accessService;
    private final CourseService courseService;
    private final InteractionMapper mapper;

    public ReviewService(ReviewRepository reviewRepository, CourseRepository courseRepository,
                         InstructorProfileRepository instructorProfileRepository, UserRepository userRepository,
                         CourseAccessService accessService, CourseService courseService, InteractionMapper mapper) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
        this.courseService = courseService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ReviewListResponse list(UUID courseId, Integer rating, int page, int size, AuthUser viewer) {
        Course course = courseService.requireViewable(courseId, viewer);
        Pageable pageable = PageUtils.of(page, size);
        Page<Review> result = (rating == null)
                ? reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable)
                : reviewRepository.findByCourseIdAndRatingOrderByCreatedAtDesc(courseId, rating, pageable);

        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int star = 5; star >= 1; star--) {
            distribution.put(star, 0L);
        }
        for (Object[] row : reviewRepository.ratingDistribution(courseId)) {
            distribution.put((Integer) row[0], (Long) row[1]);
        }
        return new ReviewListResponse(course.getAvgRating(), course.getReviewCount(), distribution,
                PageResponse.of(result, mapper::toReview));
    }

    /** Đánh giá của chính người dùng cho khóa học; null nếu chưa đánh giá. */
    @Transactional(readOnly = true)
    public ReviewResponse mine(UUID userId, UUID courseId) {
        return reviewRepository.findByCourseIdAndUserId(courseId, userId).map(mapper::toReview).orElse(null);
    }

    @Transactional
    public ReviewResponse create(AuthUser user, UUID courseId, ReviewRequest request) {
        Enrollment enrollment = accessService.requireEnrollment(user.id(), courseId);
        if (enrollment.getProgressPercentage().compareTo(MIN_PROGRESS) < 0) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_ALLOWED);
        }
        if (reviewRepository.findByCourseIdAndUserId(courseId, user.id()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE,
                    "Bạn đã đánh giá khóa học này, hãy chỉnh sửa đánh giá cũ");
        }
        Course course = enrollment.getCourse();
        Review review = Review.builder()
                .enrollment(enrollment)
                .course(course)
                .user(userRepository.getReferenceById(user.id()))
                .rating(request.rating())
                .comment(blankToNull(request.comment()))
                .build();
        reviewRepository.saveAndFlush(review);
        recomputeRatings(course);
        return mapper.toReview(review);
    }

    @Transactional
    public ReviewResponse update(AuthUser user, UUID reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy đánh giá"));
        if (!review.getUser().getId().equals(user.id())) {
            throw BusinessException.forbidden("Bạn chỉ được sửa đánh giá của chính mình");
        }
        review.setRating(request.rating());
        review.setComment(blankToNull(request.comment()));
        reviewRepository.saveAndFlush(review);
        recomputeRatings(review.getCourse());
        return mapper.toReview(review);
    }

    /** SDS 7.8.5: tính lại điểm trung bình / số đánh giá của khóa học và của giảng viên bằng UPDATE tổng hợp. */
    private void recomputeRatings(Course course) {
        courseRepository.updateRating(course.getId(), toScale2(reviewRepository.averageRatingByCourse(course.getId())),
                (int) reviewRepository.countByCourseId(course.getId()));
        UUID instructorId = course.getInstructor().getId();
        instructorProfileRepository.updateRating(instructorId,
                toScale2(reviewRepository.averageRatingByInstructor(instructorId)),
                (int) reviewRepository.countByCourseInstructorId(instructorId));
    }

    private static BigDecimal toScale2(Double avg) {
        return avg == null ? BigDecimal.ZERO.setScale(2) : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
