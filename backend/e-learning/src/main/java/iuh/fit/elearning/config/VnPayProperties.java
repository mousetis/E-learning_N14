package iuh.fit.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** vnpay.*: cấu hình cổng VNPay (sandbox). Để trống tmnCode / hashSecret nếu chỉ dùng MOCK. */
@ConfigurationProperties(prefix = "vnpay")
public record VnPayProperties(
        String tmnCode,
        String hashSecret,
        String payUrl,
        String apiUrl,
        String returnUrl
) {
    public boolean configured() {
        return tmnCode != null && !tmnCode.isBlank() && hashSecret != null && !hashSecret.isBlank();
    }
}
