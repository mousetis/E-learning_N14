package iuh.fit.elearning.course.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.dto.response.InstructorCourseDetailResponse;
import iuh.fit.elearning.course.dto.response.InstructorCourseResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.interaction.entity.Follow;
import iuh.fit.elearning.interaction.repository.FollowRepository;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** UC021: quản trị viên duyệt / từ chối khóa học. */
@Service
public class AdminCourseService {

    private final CourseRepository courseRepository;
    private final FollowRepository followRepository;
    private final CourseSupport support;
    private final InstructorCourseService instructorCourseService;
    private final NotificationService notificationService;

    public AdminCourseService(CourseRepository courseRepository, FollowRepository followRepository,
                              CourseSupport support, InstructorCourseService instructorCourseService,
                              NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.followRepository = followRepository;
        this.support = support;
        this.instructorCourseService = instructorCourseService;
        this.notificationService = notificationService;
    }

    /** status null = mọi trạng thái; mặc định giao diện duyệt dùng PENDING_REVIEW. */
    @Transactional(readOnly = true)
    public PageResponse<InstructorCourseResponse> list(CourseStatus status, int page, int size) {
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.ASC, "updatedAt"));
        Page<Course> result = status == null ? courseRepository.findAll(pageable)
                : courseRepository.findByStatus(status, pageable);
        return PageResponse.of(result, instructorCourseService::toListItem);
    }

    @Transactional(readOnly = true)
    public InstructorCourseDetailResponse detail(UUID courseId) {
        return instructorCourseService.detailOf(find(courseId));
    }

    @Transactional
    public InstructorCourseDetailResponse approve(UUID courseId) {
        Course course = find(courseId);
        requirePending(course);
        List<String> missing = support.missingForReview(course);
        if (!missing.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_READY,
                    "Khóa học chưa đủ điều kiện duyệt: " + String.join("; ", missing));
        }
        boolean firstPublish = course.getPublishedAt() == null;
        course.setStatus(CourseStatus.PUBLISHED);
        course.setRejectionReason(null);
        if (firstPublish) {
            course.setPublishedAt(Instant.now());
        }
        UUID instructorId = course.getInstructor().getId();
        support.syncInstructorCourseCount(instructorId);

        notificationService.notifyUser(instructorId, NotificationType.COURSE_REVIEW, "Khóa học đã được duyệt",
                "Khóa học \"" + course.getTitle() + "\" đã được xuất bản", course.getId());
        if (firstPublish) {
            List<UUID> followers = followRepository.findByInstructorId(instructorId).stream()
                    .map(Follow::getFollower).map(f -> f.getId()).toList();
            notificationService.notifyUsers(followers, NotificationType.NEW_COURSE, "Khóa học mới",
                    course.getInstructor().getFullName() + " vừa ra mắt khóa học \"" + course.getTitle() + "\"",
                    course.getId());
        }
        return instructorCourseService.detailOf(course);
    }

    @Transactional
    public InstructorCourseDetailResponse reject(UUID courseId, String reason) {
        Course course = find(courseId);
        requirePending(course);
        course.setStatus(CourseStatus.REJECTED);
        course.setRejectionReason(reason.trim());
        support.syncInstructorCourseCount(course.getInstructor().getId());
        notificationService.notifyUser(course.getInstructor().getId(), NotificationType.COURSE_REVIEW,
                "Khóa học bị từ chối", "Khóa học \"" + course.getTitle() + "\" bị từ chối: " + reason.trim(),
                course.getId());
        return instructorCourseService.detailOf(course);
    }

    private void requirePending(Course course) {
        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Khóa học không ở trạng thái chờ duyệt");
        }
    }

    private Course find(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
    }
}
