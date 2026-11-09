package iuh.fit.elearning.course.service;

import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.repository.LessonRepository;
import iuh.fit.elearning.course.repository.SectionRepository;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Quy tắc dùng chung cho vòng đời khóa học (UC019 giảng viên, UC021 quản trị viên). */
@Component
public class CourseSupport {

    /** Số bài học có video tối thiểu để gửi duyệt / duyệt (SRS UC019 bước 8). */
    public static final int MIN_LESSONS_WITH_VIDEO = 3;

    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    public CourseSupport(SectionRepository sectionRepository, LessonRepository lessonRepository,
                         CourseRepository courseRepository, InstructorProfileRepository instructorProfileRepository) {
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.instructorProfileRepository = instructorProfileRepository;
    }

    /** Các mục còn thiếu để khóa học đủ điều kiện gửi duyệt / duyệt. Rỗng = đủ điều kiện. */
    public List<String> missingForReview(Course course) {
        List<String> missing = new ArrayList<>();
        if (course.getThumbnailUrl() == null || course.getThumbnailUrl().isBlank()) {
            missing.add("Ảnh bìa khóa học");
        }
        if (sectionRepository.countByCourseId(course.getId()) < 1) {
            missing.add("Ít nhất 1 chương");
        }
        long withVideo = lessonRepository.countWithVideoByCourseId(course.getId());
        if (withVideo < MIN_LESSONS_WITH_VIDEO) {
            missing.add("Ít nhất " + MIN_LESSONS_WITH_VIDEO + " bài học có video (hiện có " + withVideo + ")");
        }
        if (course.getOriginalPrice().signum() < 0 || course.getSalePrice().signum() < 0
                || course.getSalePrice().compareTo(course.getOriginalPrice()) > 0) {
            missing.add("Giá bán hợp lệ (không âm và không lớn hơn giá gốc)");
        }
        return missing;
    }

    /** Tính lại tổng số bài học và tổng thời lượng của khóa học sau khi thêm / sửa / xóa bài học. */
    public void refreshTotals(Course course) {
        course.setTotalLessons((int) lessonRepository.countBySectionCourseId(course.getId()));
        course.setTotalDurationSeconds((int) lessonRepository.sumDurationByCourseId(course.getId()));
    }

    /** instructor_profiles.course_count = số khóa học đang PUBLISHED của giảng viên. */
    public void syncInstructorCourseCount(UUID instructorId) {
        long count = courseRepository.countByInstructorIdAndStatus(instructorId, CourseStatus.PUBLISHED);
        instructorProfileRepository.findByUserId(instructorId).ifPresent(p -> p.setCourseCount((int) count));
    }
}
