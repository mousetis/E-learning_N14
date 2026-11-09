package iuh.fit.elearning.auth.dto.response;

import iuh.fit.elearning.user.dto.response.UserSummaryResponse;

/** Kết quả đăng nhập / làm mới token. expiresIn tính bằng giây (thời hạn access token). */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserSummaryResponse user
) {
}
