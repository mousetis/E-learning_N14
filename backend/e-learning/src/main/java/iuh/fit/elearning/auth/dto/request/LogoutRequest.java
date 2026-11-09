package iuh.fit.elearning.auth.dto.request;

/** refreshToken hiện tại của thiết bị (tùy chọn); nếu thiếu, server không thu hồi gì và vẫn trả thành công. */
public record LogoutRequest(String refreshToken) {
}
