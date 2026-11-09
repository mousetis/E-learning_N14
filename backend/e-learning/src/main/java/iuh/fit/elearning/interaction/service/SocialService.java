package iuh.fit.elearning.interaction.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.interaction.dto.response.LikeResponse;
import iuh.fit.elearning.interaction.entity.CourseLike;
import iuh.fit.elearning.interaction.entity.Follow;
import iuh.fit.elearning.interaction.repository.CourseLikeRepository;
import iuh.fit.elearning.interaction.repository.FollowRepository;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** UC016 (theo dõi giảng viên) và thích khóa học (UC011). */
@Service
public class SocialService {

    private final FollowRepository followRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final CourseService courseService;

    public SocialService(FollowRepository followRepository, CourseLikeRepository courseLikeRepository,
                         CourseRepository courseRepository, UserRepository userRepository,
                         InstructorProfileRepository instructorProfileRepository, CourseService courseService) {
        this.followRepository = followRepository;
        this.courseLikeRepository = courseLikeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.courseService = courseService;
    }

    @Transactional
    public void follow(UUID userId, UUID instructorId) {
        if (userId.equals(instructorId)) {
            throw BusinessException.badRequest("Bạn không thể tự theo dõi chính mình");
        }
        User instructor = userRepository.findById(instructorId)
                .filter(u -> u.getRole() == UserRole.INSTRUCTOR && u.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy giảng viên"));
        if (followRepository.existsByFollowerIdAndInstructorId(userId, instructorId)) {
            return;
        }
        followRepository.save(Follow.builder()
                .follower(userRepository.getReferenceById(userId))
                .instructor(instructor)
                .build());
        instructorProfileRepository.addFollowerCount(instructorId, 1);
    }

    @Transactional
    public void unfollow(UUID userId, UUID instructorId) {
        followRepository.findByFollowerIdAndInstructorId(userId, instructorId).ifPresent(f -> {
            followRepository.delete(f);
            instructorProfileRepository.addFollowerCount(instructorId, -1);
        });
    }

    @Transactional
    public LikeResponse likeCourse(AuthUser user, UUID courseId) {
        Course course = courseService.requireViewable(courseId, user);
        if (courseLikeRepository.findByCourseIdAndUserId(courseId, user.id()).isPresent()) {
            return new LikeResponse(true, course.getLikeCount());
        }
        courseLikeRepository.save(CourseLike.builder()
                .course(course)
                .user(userRepository.getReferenceById(user.id()))
                .build());
        courseRepository.addLikeCount(courseId, 1);
        return new LikeResponse(true, course.getLikeCount() + 1);
    }

    @Transactional
    public LikeResponse unlikeCourse(UUID userId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        var like = courseLikeRepository.findByCourseIdAndUserId(courseId, userId);
        if (like.isEmpty()) {
            return new LikeResponse(false, course.getLikeCount());
        }
        courseLikeRepository.delete(like.get());
        courseRepository.addLikeCount(courseId, -1);
        return new LikeResponse(false, Math.max(0, course.getLikeCount() - 1));
    }
}
