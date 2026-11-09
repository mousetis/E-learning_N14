package iuh.fit.elearning.auth.controller;

import iuh.fit.elearning.auth.dto.request.ForgotPasswordRequest;
import iuh.fit.elearning.auth.dto.request.LoginRequest;
import iuh.fit.elearning.auth.dto.request.LogoutRequest;
import iuh.fit.elearning.auth.dto.request.RefreshTokenRequest;
import iuh.fit.elearning.auth.dto.request.RegisterRequest;
import iuh.fit.elearning.auth.dto.request.ResendOtpRequest;
import iuh.fit.elearning.auth.dto.request.ResetPasswordRequest;
import iuh.fit.elearning.auth.dto.request.VerifyOtpRequest;
import iuh.fit.elearning.auth.dto.response.AuthResponse;
import iuh.fit.elearning.auth.dto.response.OtpSentResponse;
import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.auth.service.AuthService;
import iuh.fit.elearning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Xác thực: UC001 - UC003 (SDS bảng 6.2). */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OtpSentResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Đăng ký thành công, vui lòng nhập mã OTP đã gửi tới email", authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyRegistration(request);
        return ApiResponse.message("Xác thực thành công, bạn có thể đăng nhập");
    }

    @PostMapping("/resend-otp")
    public ApiResponse<OtpSentResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ApiResponse.ok("Nếu email hợp lệ, mã OTP mới đã được gửi", authService.resendOtp(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Đăng nhập thành công", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal AuthUser user,
                                    @RequestBody(required = false) LogoutRequest request) {
        authService.logout(user.id(), request == null ? null : request.refreshToken());
        return ApiResponse.message("Đăng xuất thành công");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<OtpSentResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.ok("Nếu email hợp lệ, mã OTP đặt lại mật khẩu đã được gửi",
                authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.message("Đặt lại mật khẩu thành công, vui lòng đăng nhập lại");
    }
}
