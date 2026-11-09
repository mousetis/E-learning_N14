package iuh.fit.elearning.course.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.course.dto.response.CourseDetailResponse;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.dto.response.CurriculumResponse;
import iuh.fit.elearning.course.dto.response.LessonPreviewResponse;
import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.service.CourseSearchCriteria;
import iuh.fit.elearning.course.service.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * UC004, UC005, UC006: tìm kiếm, danh mục, chi tiết khóa học (public; có token thì kèm cờ saved/owned...).
 */
@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public ApiResponse<PageResponse<CourseSummaryResponse>> search(
            @AuthenticationPrincipal AuthUser user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CourseSearchCriteria criteria = new CourseSearchCriteria(keyword, categoryId, minPrice, maxPrice,
                minRating, level, duration, sort);
        return ApiResponse.ok(courseService.search(criteria, page, size, AuthUser.idOf(user)));
    }

    @GetMapping("/courses/popular")
    public ApiResponse<PageResponse<CourseSummaryResponse>> popular(@AuthenticationPrincipal AuthUser user,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(courseService.popular(page, size, AuthUser.idOf(user)));
    }

    @GetMapping("/courses/for-you")
    public ApiResponse<PageResponse<CourseSummaryResponse>> forYou(@AuthenticationPrincipal AuthUser user,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(courseService.forYou(page, size, AuthUser.idOf(user)));
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<CourseDetailResponse> detail(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(courseService.detail(id, user));
    }

    @GetMapping("/courses/{id}/curriculum")
    public ApiResponse<CurriculumResponse> curriculum(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(courseService.curriculum(id, user));
    }

    @GetMapping("/courses/{id}/similar")
    public ApiResponse<List<CourseSummaryResponse>> similar(@AuthenticationPrincipal AuthUser user,
                                                            @PathVariable UUID id,
                                                            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(courseService.similar(id, limit, AuthUser.idOf(user)));
    }

    @GetMapping("/categories/{slug}/courses")
    public ApiResponse<PageResponse<CourseSummaryResponse>> byCategory(@AuthenticationPrincipal AuthUser user,
                                                                       @PathVariable String slug,
                                                                       @RequestParam(required = false) String sort,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(courseService.byCategory(slug, sort, page, size, AuthUser.idOf(user)));
    }

    @GetMapping("/lessons/{id}/preview")
    public ApiResponse<LessonPreviewResponse> preview(@PathVariable UUID id) {
        return ApiResponse.ok(courseService.previewLesson(id));
    }
}
