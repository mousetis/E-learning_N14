package iuh.fit.elearning.learning.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Kiểm tra quyền truy cập nội dung khóa học: học viên có ghi danh hợp lệ (không phải REFUNDED),
 * giảng viên sở hữu khóa học hoặc quản trị viên.
 */
@Service
public class CourseAccessService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public CourseAccessService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    /** Ghi danh hợp lệ của học viên; NOT_ENROLLED nếu chưa ghi danh hoặc đã hoàn tiền. */
    @Transactional(readOnly = true)
    public Enrollment requireEnrollment(UUID userId, UUID courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .filter(e -> e.getStatus() != EnrollmentStatus.REFUNDED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_ENROLLED));
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(UUID userId, UUID courseId) {
        return userId != null
                && enrollmentRepository.existsByUserIdAndCourseIdAndStatusNot(userId, courseId, EnrollmentStatus.REFUNDED);
    }

    /** Học viên đã ghi danh, giảng viên của khóa học hoặc quản trị viên đều được xem nội dung. */
    @Transactional(readOnly = true)
    public Course requireContentAccess(AuthUser user, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        if (user.isAdmin() || course.getInstructor().getId().equals(user.id())) {
            return course;
        }
        if (!isEnrolled(user.id(), courseId)) {
            throw new BusinessException(ErrorCode.NOT_ENROLLED);
        }
        return course;
    }
}
