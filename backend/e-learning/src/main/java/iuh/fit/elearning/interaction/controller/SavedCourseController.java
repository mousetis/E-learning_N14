package iuh.fit.elearning.interaction.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.interaction.service.SavedCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC008: khóa học đã lưu. */
@RestController
@RequestMapping("/api/v1/saved-courses")
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    public SavedCourseController(SavedCourseService savedCourseService) {
        this.savedCourseService = savedCourseService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CourseSummaryResponse>> list(@AuthenticationPrincipal AuthUser user,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(savedCourseService.list(user.id(), page, size));
    }

    @PostMapping("/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> save(@AuthenticationPrincipal AuthUser user, @PathVariable UUID courseId) {
        savedCourseService.save(user, courseId);
        return ApiResponse.message("Đã lưu khóa học");
    }

    @DeleteMapping("/{courseId}")
    public ApiResponse<Void> remove(@AuthenticationPrincipal AuthUser user, @PathVariable UUID courseId) {
        savedCourseService.remove(user.id(), courseId);
        return ApiResponse.message("Đã bỏ lưu khóa học");
    }
}
