package iuh.fit.elearning.auth.dto.response;

/** Phản hồi sau khi gửi OTP: email nhận mã, thời hạn mã và thời gian chờ trước khi được gửi lại (giây). */
public record OtpSentResponse(
        String email,
        long expiresInSeconds,
        long resendAfterSeconds
) {
}
