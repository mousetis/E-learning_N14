package iuh.fit.elearning.user.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.dto.response.CategoryBrief;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.interaction.dto.response.ReviewResponse;
import iuh.fit.elearning.interaction.mapper.InteractionMapper;
import iuh.fit.elearning.interaction.repository.FollowRepository;
import iuh.fit.elearning.interaction.repository.ReviewRepository;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.dto.response.InstructorCardResponse;
import iuh.fit.elearning.user.dto.response.InstructorCoursesGroupResponse;
import iuh.fit.elearning.user.dto.response.InstructorProfileResponse;
import iuh.fit.elearning.user.entity.InstructorProfile;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** UC007: xem hồ sơ giảng viên (public). */
@Service
public class InstructorService {

    private final UserRepository userRepository;
    private final InstructorProfileRepository profileRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;
    private final CourseService courseService;
    private final InteractionMapper interactionMapper;
    private final StorageService storageService;

    public InstructorService(UserRepository userRepository, InstructorProfileRepository profileRepository,
                             FollowRepository followRepository, ReviewRepository reviewRepository,
                             CourseService courseService,
                             InteractionMapper interactionMapper, StorageService storageService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.followRepository = followRepository;
        this.reviewRepository = reviewRepository;
        this.courseService = courseService;
        this.interactionMapper = interactionMapper;
        this.storageService = storageService;
    }

    /** Giảng viên nổi bật: đang hoạt động, có khóa học, xếp theo số học viên rồi điểm đánh giá. */
    @Transactional(readOnly = true)
    public PageResponse<InstructorCardResponse> top(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "studentCount", "avgRating");
        Page<InstructorProfile> result = profileRepository
                .findByUserStatusAndCourseCountGreaterThan(UserStatus.ACTIVE, 0, PageUtils.of(page, size, sort));
        return PageResponse.of(result, this::toCard);
    }

    @Transactional(readOnly = true)
    public InstructorProfileResponse detail(UUID instructorId, AuthUser viewer) {
        User user = requireInstructor(instructorId);
        InstructorProfile p = profileRepository.findByUserId(instructorId).orElse(null);
        Boolean following = viewer == null ? null
                : followRepository.existsByFollowerIdAndInstructorId(viewer.id(), instructorId);
        return new InstructorProfileResponse(user.getId(), user.getFullName(),
                storageService.resolveUrl(user.getAvatarUrl()), storageService.resolveUrl(user.getCoverUrl()),
                user.getJobTitle(), user.getBio(),
                p == null ? null : p.getExpertise(), p == null ? null : p.getIntroduction(),
                p == null ? null : p.getLocation(),
                p == null ? 0 : p.getFollowerCount(), p == null ? 0 : p.getStudentCount(),
                p == null ? 0 : p.getCourseCount(), p == null ? BigDecimal.ZERO : p.getAvgRating(),
                p == null ? 0 : p.getReviewCount(), following);
    }

    /** Khóa học PUBLISHED của giảng viên, nhóm theo danh mục (giữ thứ tự xuất hiện). */
    @Transactional(readOnly = true)
    public List<InstructorCoursesGroupResponse> coursesGroupedByCategory(UUID instructorId, AuthUser viewer) {
        requireInstructor(instructorId);
        List<CourseSummaryResponse> courses = courseService.publishedByInstructor(instructorId, AuthUser.idOf(viewer));
        Map<UUID, List<CourseSummaryResponse>> grouped = new LinkedHashMap<>();
        Map<UUID, CategoryBrief> categories = new LinkedHashMap<>();
        for (CourseSummaryResponse c : courses) {
            grouped.computeIfAbsent(c.category().id(), k -> new ArrayList<>()).add(c);
            categories.putIfAbsent(c.category().id(), c.category());
        }
        List<InstructorCoursesGroupResponse> result = new ArrayList<>();
        grouped.forEach((id, list) -> result.add(new InstructorCoursesGroupResponse(categories.get(id), list)));
        return result;
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> reviews(UUID instructorId, int page, int size) {
        requireInstructor(instructorId);
        return PageResponse.of(
                reviewRepository.findByCourseInstructorIdOrderByCreatedAtDesc(instructorId, PageUtils.of(page, size)),
                interactionMapper::toReview);
    }

    private InstructorCardResponse toCard(InstructorProfile p) {
        User u = p.getUser();
        return new InstructorCardResponse(u.getId(), u.getFullName(), storageService.resolveUrl(u.getAvatarUrl()),
                u.getJobTitle(), p.getExpertise(), p.getFollowerCount(), p.getStudentCount(), p.getCourseCount(),
                p.getAvgRating(), p.getReviewCount());
    }

    private User requireInstructor(UUID id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.INSTRUCTOR && u.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy giảng viên"));
    }
}
