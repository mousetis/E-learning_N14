package iuh.fit.elearning.interaction.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.interaction.dto.request.ReviewRequest;
import iuh.fit.elearning.interaction.dto.response.ReviewListResponse;
import iuh.fit.elearning.interaction.dto.response.ReviewResponse;
import iuh.fit.elearning.interaction.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC015: đánh giá khóa học. */
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/courses/{id}/reviews")
    public ApiResponse<ReviewListResponse> list(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                @RequestParam(required = false) Integer rating,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(reviewService.list(id, rating, page, size, user));
    }

    /** Đánh giá của tôi cho khóa học (data = null nếu chưa đánh giá). */
    @GetMapping("/courses/{id}/reviews/me")
    public ApiResponse<ReviewResponse> mine(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(reviewService.mine(user.id(), id));
    }

    @PostMapping("/courses/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                              @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Đã gửi đánh giá", reviewService.create(user, id, request));
    }

    @PutMapping("/reviews/{id}")
    public ApiResponse<ReviewResponse> update(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                              @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Đã cập nhật đánh giá", reviewService.update(user, id, request));
    }
}
