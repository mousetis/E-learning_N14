package iuh.fit.elearning.user.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.interaction.dto.response.ReviewResponse;
import iuh.fit.elearning.user.dto.response.InstructorCardResponse;
import iuh.fit.elearning.user.dto.response.InstructorCoursesGroupResponse;
import iuh.fit.elearning.user.dto.response.InstructorProfileResponse;
import iuh.fit.elearning.user.service.InstructorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** UC007: hồ sơ giảng viên (public; có token thì kèm cờ following). */
@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public ApiResponse<PageResponse<InstructorCardResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(instructorService.top(page, size));
    }

    @GetMapping("/top")
    public ApiResponse<PageResponse<InstructorCardResponse>> top(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(instructorService.top(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<InstructorProfileResponse> detail(@AuthenticationPrincipal AuthUser user,
                                                         @PathVariable UUID id) {
        return ApiResponse.ok(instructorService.detail(id, user));
    }

    @GetMapping("/{id}/courses")
    public ApiResponse<List<InstructorCoursesGroupResponse>> courses(@AuthenticationPrincipal AuthUser user,
                                                                     @PathVariable UUID id) {
        return ApiResponse.ok(instructorService.coursesGroupedByCategory(id, user));
    }

    @GetMapping("/{id}/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> reviews(@PathVariable UUID id,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(instructorService.reviews(id, page, size));
    }
}
