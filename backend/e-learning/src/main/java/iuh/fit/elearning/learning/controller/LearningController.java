package iuh.fit.elearning.learning.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.learning.dto.request.ProgressRequest;
import iuh.fit.elearning.learning.dto.request.RefundRequestBody;
import iuh.fit.elearning.learning.dto.response.CertificateResponse;
import iuh.fit.elearning.learning.dto.response.EnrollmentResponse;
import iuh.fit.elearning.learning.dto.response.LearnCourseResponse;
import iuh.fit.elearning.learning.dto.response.ProgressResponse;
import iuh.fit.elearning.learning.dto.response.RefundResponse;
import iuh.fit.elearning.learning.dto.response.StreamResponse;
import iuh.fit.elearning.learning.service.CertificateService;
import iuh.fit.elearning.learning.service.LearningService;
import iuh.fit.elearning.learning.service.RefundService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** UC011, UC012: học tập, khóa học của tôi, chứng chỉ, hoàn tiền. */
@RestController
@RequestMapping("/api/v1")
public class LearningController {

    private final LearningService learningService;
    private final CertificateService certificateService;
    private final RefundService refundService;

    public LearningController(LearningService learningService, CertificateService certificateService,
                              RefundService refundService) {
        this.learningService = learningService;
        this.certificateService = certificateService;
        this.refundService = refundService;
    }

    @GetMapping("/me/enrollments")
    public ApiResponse<List<EnrollmentResponse>> enrollments(@AuthenticationPrincipal AuthUser user,
                                                             @RequestParam(required = false) String status) {
        return ApiResponse.ok(learningService.myEnrollments(user.id(), status));
    }

    @GetMapping("/learn/courses/{courseId}")
    public ApiResponse<LearnCourseResponse> learn(@AuthenticationPrincipal AuthUser user,
                                                  @PathVariable UUID courseId) {
        return ApiResponse.ok(learningService.learnCourse(user, courseId));
    }

    @GetMapping("/learn/lessons/{lessonId}/stream")
    public ApiResponse<StreamResponse> stream(@AuthenticationPrincipal AuthUser user, @PathVariable UUID lessonId) {
        return ApiResponse.ok(learningService.stream(user, lessonId));
    }

    @PutMapping("/learn/lessons/{lessonId}/progress")
    public ApiResponse<ProgressResponse> progress(@AuthenticationPrincipal AuthUser user,
                                                  @PathVariable UUID lessonId,
                                                  @Valid @RequestBody ProgressRequest request) {
        return ApiResponse.ok(learningService.updateProgress(user, lessonId, request.positionSeconds()));
    }

    @GetMapping("/me/certificates")
    public ApiResponse<List<CertificateResponse>> certificates(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(certificateService.mine(user.id()));
    }

    /** Công khai: xem thông tin chứng chỉ theo mã. */
    @GetMapping("/certificates/{code}")
    public ApiResponse<CertificateResponse> certificate(@PathVariable String code) {
        return ApiResponse.ok(certificateService.byCode(code));
    }

    /** Công khai: tải PDF chứng chỉ theo mã. */
    @GetMapping("/certificates/{code}/pdf")
    public ResponseEntity<byte[]> certificatePdf(@PathVariable String code) {
        byte[] pdf = certificateService.pdf(code);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + code + ".pdf\"")
                .cacheControl(CacheControl.noStore())
                .body(pdf);
    }

    @PostMapping("/me/enrollments/{id}/refund")
    public ApiResponse<RefundResponse> refund(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                              @Valid @RequestBody RefundRequestBody request,
                                              HttpServletRequest http) {
        String forwarded = http.getHeader("X-Forwarded-For");
        String ip = forwarded != null && !forwarded.isBlank() ? forwarded.split(",")[0].trim() : http.getRemoteAddr();
        return ApiResponse.ok("Yêu cầu hoàn tiền đã được xử lý", refundService.request(user, id, request.reason(), ip));
    }
}
