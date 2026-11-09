package iuh.fit.elearning.course.controller;

import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.course.dto.request.RejectCourseRequest;
import iuh.fit.elearning.course.dto.response.InstructorCourseDetailResponse;
import iuh.fit.elearning.course.dto.response.InstructorCourseResponse;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.service.AdminCourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC021: duyệt khóa học (ADMIN). */
@RestController
@RequestMapping("/api/v1/admin/courses")
public class AdminCourseController {

    private final AdminCourseService adminCourseService;

    public AdminCourseController(AdminCourseService adminCourseService) {
        this.adminCourseService = adminCourseService;
    }

    @GetMapping
    public ApiResponse<PageResponse<InstructorCourseResponse>> list(
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(adminCourseService.list(status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<InstructorCourseDetailResponse> detail(@PathVariable UUID id) {
        return ApiResponse.ok(adminCourseService.detail(id));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<InstructorCourseDetailResponse> approve(@PathVariable UUID id) {
        return ApiResponse.ok("Đã duyệt khóa học", adminCourseService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<InstructorCourseDetailResponse> reject(@PathVariable UUID id,
                                                              @Valid @RequestBody RejectCourseRequest request) {
        return ApiResponse.ok("Đã từ chối khóa học", adminCourseService.reject(id, request.reason()));
    }
}
