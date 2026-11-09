package iuh.fit.elearning.course.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.course.dto.request.CourseRequest;
import iuh.fit.elearning.course.dto.request.DocumentRequest;
import iuh.fit.elearning.course.dto.request.LessonRequest;
import iuh.fit.elearning.course.dto.request.ProjectDescriptionRequest;
import iuh.fit.elearning.course.dto.request.SectionRequest;
import iuh.fit.elearning.course.dto.response.InstructorCourseDetailResponse;
import iuh.fit.elearning.course.dto.response.InstructorCourseResponse;
import iuh.fit.elearning.course.dto.response.InstructorLessonResponse;
import iuh.fit.elearning.course.dto.response.InstructorSectionResponse;
import iuh.fit.elearning.course.service.InstructorCourseService;
import iuh.fit.elearning.interaction.dto.request.FeedbackRequest;
import iuh.fit.elearning.interaction.dto.request.QaContentRequest;
import iuh.fit.elearning.interaction.dto.response.DocumentResponse;
import iuh.fit.elearning.interaction.dto.response.QaCommentResponse;
import iuh.fit.elearning.interaction.dto.response.QaPostResponse;
import iuh.fit.elearning.interaction.dto.response.SubmissionResponse;
import iuh.fit.elearning.interaction.service.ProjectService;
import iuh.fit.elearning.interaction.service.QaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * UC019, UC020: khu vực giảng viên (/api/v1/instructor/** - INSTRUCTOR hoặc ADMIN, cấu hình ở SecurityConfig;
 * quyền sở hữu từng khóa học được kiểm tra trong service).
 */
@RestController
@RequestMapping("/api/v1/instructor")
public class InstructorCourseController {

    private final InstructorCourseService courseService;
    private final QaService qaService;
    private final ProjectService projectService;

    public InstructorCourseController(InstructorCourseService courseService, QaService qaService,
                                      ProjectService projectService) {
        this.courseService = courseService;
        this.qaService = qaService;
        this.projectService = projectService;
    }

    // ---------------------------------------------------------------- khóa học

    @GetMapping("/courses")
    public ApiResponse<List<InstructorCourseResponse>> courses(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(courseService.myCourses(user.id()));
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<InstructorCourseDetailResponse> course(@AuthenticationPrincipal AuthUser user,
                                                              @PathVariable UUID id) {
        return ApiResponse.ok(courseService.detail(user, id));
    }

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InstructorCourseDetailResponse> create(@AuthenticationPrincipal AuthUser user,
                                                              @Valid @RequestBody CourseRequest request) {
        return ApiResponse.ok("Đã tạo khóa học (DRAFT)", courseService.create(user, request));
    }

    @PutMapping("/courses/{id}")
    public ApiResponse<InstructorCourseDetailResponse> update(@AuthenticationPrincipal AuthUser user,
                                                              @PathVariable UUID id,
                                                              @Valid @RequestBody CourseRequest request) {
        return ApiResponse.ok("Đã cập nhật khóa học", courseService.update(user, id, request));
    }

    @DeleteMapping("/courses/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        courseService.delete(user, id);
        return ApiResponse.message("Đã xóa khóa học");
    }

    @PostMapping("/courses/{id}/submit-review")
    public ApiResponse<InstructorCourseDetailResponse> submitReview(@AuthenticationPrincipal AuthUser user,
                                                                    @PathVariable UUID id) {
        return ApiResponse.ok("Đã gửi khóa học đi duyệt", courseService.submitForReview(user, id));
    }

    @PostMapping("/courses/{id}/unpublish")
    public ApiResponse<InstructorCourseDetailResponse> unpublish(@AuthenticationPrincipal AuthUser user,
                                                                 @PathVariable UUID id) {
        return ApiResponse.ok("Đã ngừng xuất bản khóa học", courseService.unpublish(user, id));
    }

    // ---------------------------------------------------------------- chương, bài học

    @PostMapping("/courses/{id}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InstructorSectionResponse> addSection(@AuthenticationPrincipal AuthUser user,
                                                             @PathVariable UUID id,
                                                             @Valid @RequestBody SectionRequest request) {
        return ApiResponse.ok("Đã thêm chương", courseService.addSection(user, id, request));
    }

    @PutMapping("/sections/{id}")
    public ApiResponse<InstructorSectionResponse> updateSection(@AuthenticationPrincipal AuthUser user,
                                                                @PathVariable UUID id,
                                                                @Valid @RequestBody SectionRequest request) {
        return ApiResponse.ok("Đã cập nhật chương", courseService.updateSection(user, id, request));
    }

    @DeleteMapping("/sections/{id}")
    public ApiResponse<Void> deleteSection(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        courseService.deleteSection(user, id);
        return ApiResponse.message("Đã xóa chương");
    }

    @PostMapping("/sections/{id}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InstructorLessonResponse> addLesson(@AuthenticationPrincipal AuthUser user,
                                                           @PathVariable UUID id,
                                                           @Valid @RequestBody LessonRequest request) {
        return ApiResponse.ok("Đã thêm bài học", courseService.addLesson(user, id, request));
    }

    @PutMapping("/lessons/{id}")
    public ApiResponse<InstructorLessonResponse> updateLesson(@AuthenticationPrincipal AuthUser user,
                                                              @PathVariable UUID id,
                                                              @Valid @RequestBody LessonRequest request) {
        return ApiResponse.ok("Đã cập nhật bài học", courseService.updateLesson(user, id, request));
    }

    @DeleteMapping("/lessons/{id}")
    public ApiResponse<Void> deleteLesson(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        courseService.deleteLesson(user, id);
        return ApiResponse.message("Đã xóa bài học");
    }

    // ---------------------------------------------------------------- tài liệu, dự án

    @PostMapping("/courses/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DocumentResponse> addDocument(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                     @Valid @RequestBody DocumentRequest request) {
        return ApiResponse.ok("Đã thêm tài liệu", courseService.addDocument(user, id, request));
    }

    @DeleteMapping("/courses/{id}/documents/{documentId}")
    public ApiResponse<Void> deleteDocument(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                            @PathVariable UUID documentId) {
        courseService.deleteDocument(user, id, documentId);
        return ApiResponse.message("Đã xóa tài liệu");
    }

    @PutMapping("/courses/{id}/project")
    public ApiResponse<InstructorCourseDetailResponse> updateProject(@AuthenticationPrincipal AuthUser user,
                                                                     @PathVariable UUID id,
                                                                     @Valid @RequestBody ProjectDescriptionRequest request) {
        return ApiResponse.ok("Đã cập nhật mô tả dự án", courseService.updateProject(user, id, request));
    }

    // ---------------------------------------------------------------- Q&A, nhận xét dự án (UC020)

    @GetMapping("/courses/{id}/qa-posts")
    public ApiResponse<PageResponse<QaPostResponse>> qaPosts(@AuthenticationPrincipal AuthUser user,
                                                             @PathVariable UUID id,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(qaService.listForInstructor(user, id, page, size));
    }

    @PostMapping("/qa-posts/{id}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QaCommentResponse> answer(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                 @Valid @RequestBody QaContentRequest request) {
        return ApiResponse.ok("Đã trả lời câu hỏi", qaService.answer(user, id, request));
    }

    @PatchMapping("/qa-posts/{id}/hide")
    public ApiResponse<Void> hide(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        qaService.hide(user, id);
        return ApiResponse.message("Đã ẩn câu hỏi");
    }

    @PutMapping("/submissions/{id}/feedback")
    public ApiResponse<SubmissionResponse> feedback(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                    @Valid @RequestBody FeedbackRequest request) {
        return ApiResponse.ok("Đã gửi nhận xét", projectService.feedback(user, id, request));
    }
}
