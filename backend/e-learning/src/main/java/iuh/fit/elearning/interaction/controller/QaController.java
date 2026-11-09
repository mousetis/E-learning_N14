package iuh.fit.elearning.interaction.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.interaction.dto.request.QaContentRequest;
import iuh.fit.elearning.interaction.dto.response.LikeResponse;
import iuh.fit.elearning.interaction.dto.response.QaCommentResponse;
import iuh.fit.elearning.interaction.dto.response.QaPostResponse;
import iuh.fit.elearning.interaction.service.QaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** UC014: hỏi đáp Q&A (học viên). */
@RestController
@RequestMapping("/api/v1")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    @GetMapping("/courses/{id}/qa-posts")
    public ApiResponse<PageResponse<QaPostResponse>> posts(@AuthenticationPrincipal AuthUser user,
                                                           @PathVariable UUID id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(qaService.list(id, page, size, user));
    }

    @PostMapping("/courses/{id}/qa-posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QaPostResponse> create(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                              @Valid @RequestBody QaContentRequest request) {
        return ApiResponse.ok("Đã đăng câu hỏi", qaService.create(user, id, request));
    }

    @DeleteMapping("/qa-posts/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        qaService.delete(user, id);
        return ApiResponse.message("Đã xóa câu hỏi");
    }

    @GetMapping("/qa-posts/{id}/comments")
    public ApiResponse<List<QaCommentResponse>> comments(@AuthenticationPrincipal AuthUser user,
                                                         @PathVariable UUID id) {
        return ApiResponse.ok(qaService.comments(id, user));
    }

    @PostMapping("/qa-posts/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QaCommentResponse> addComment(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                     @Valid @RequestBody QaContentRequest request) {
        return ApiResponse.ok("Đã bình luận", qaService.addComment(user, id, request));
    }

    @PostMapping("/qa-posts/{id}/like")
    public ApiResponse<LikeResponse> like(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(qaService.toggleLike(user, id));
    }
}
