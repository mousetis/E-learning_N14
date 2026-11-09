package iuh.fit.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** app.otp.*: chế độ lưu (redis | memory), độ dài, TTL, số lần nhập sai tối đa, thời gian chờ gửi lại. */
@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        String mode,
        int length,
        Duration ttl,
        int maxAttempts,
        Duration resendCooldown
) {
}
