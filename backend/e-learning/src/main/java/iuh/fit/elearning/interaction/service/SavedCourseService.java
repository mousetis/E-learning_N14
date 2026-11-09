package iuh.fit.elearning.interaction.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.interaction.entity.SavedCourse;
import iuh.fit.elearning.interaction.repository.SavedCourseRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** UC008: lưu khóa học để xem sau. */
@Service
public class SavedCourseService {

    private final SavedCourseRepository savedCourseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    public SavedCourseService(SavedCourseRepository savedCourseRepository, UserRepository userRepository,
                              CourseService courseService) {
        this.savedCourseRepository = savedCourseRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public PageResponse<CourseSummaryResponse> list(UUID userId, int page, int size) {
        Page<SavedCourse> result = savedCourseRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageUtils.of(page, size));
        var courses = result.getContent().stream().map(SavedCourse::getCourse).toList();
        return PageResponse.of(result, courseService.summarize(courses, userId));
    }

    /** Idempotent: lưu lại một khóa đã lưu không báo lỗi. */
    @Transactional
    public void save(AuthUser user, UUID courseId) {
        Course course = courseService.requireViewable(courseId, user);
        if (!savedCourseRepository.existsByUserIdAndCourseId(user.id(), courseId)) {
            savedCourseRepository.save(SavedCourse.builder()
                    .user(userRepository.getReferenceById(user.id()))
                    .course(course)
                    .build());
        }
    }

    @Transactional
    public void remove(UUID userId, UUID courseId) {
        savedCourseRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(savedCourseRepository::delete);
    }
}
