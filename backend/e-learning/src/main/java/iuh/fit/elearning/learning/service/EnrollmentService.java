package iuh.fit.elearning.learning.service;

import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.learning.repository.LessonProgressRepository;
import iuh.fit.elearning.order.entity.OrderItem;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/** Tạo / khôi phục ghi danh và cập nhật bộ đếm học viên (UC010, UC009 khóa miễn phí). */
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             LessonProgressRepository lessonProgressRepository, CourseRepository courseRepository,
                             InstructorProfileRepository instructorProfileRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.courseRepository = courseRepository;
        this.instructorProfileRepository = instructorProfileRepository;
    }

    /**
     * Ghi danh học viên vào khóa học. Đã sở hữu thì giữ nguyên; ghi danh đã hoàn tiền (REFUNDED) được kích hoạt lại
     * với tiến độ bắt đầu từ đầu. orderItem null với khóa miễn phí.
     */
    @Transactional
    public Enrollment enroll(User user, Course course, OrderItem orderItem) {
        Enrollment existing = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId()).orElse(null);
        if (existing != null) {
            if (existing.getStatus() != EnrollmentStatus.REFUNDED) {
                return existing;
            }
            lessonProgressRepository.deleteByEnrollmentId(existing.getId());
            existing.setStatus(EnrollmentStatus.ONGOING);
            existing.setProgressPercentage(BigDecimal.ZERO);
            existing.setCompletedAt(null);
            existing.setLastAccessedAt(null);
            existing.setEnrolledAt(Instant.now());
            existing.setOrderItem(orderItem);
            enrollmentRepository.saveAndFlush(existing);
            bumpStudentCounts(course, 1);
            return existing;
        }
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .orderItem(orderItem)
                .build();
        enrollmentRepository.saveAndFlush(enrollment);
        bumpStudentCounts(course, 1);
        return enrollment;
    }

    /** Dùng khi hoàn tiền: giảm bộ đếm học viên. */
    public void bumpStudentCounts(Course course, int delta) {
        courseRepository.addStudentCount(course.getId(), delta);
        instructorProfileRepository.addStudentCount(course.getInstructor().getId(), delta);
    }
}
