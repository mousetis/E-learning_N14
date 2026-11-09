package iuh.fit.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** app.payment.*: bật cổng MOCK, thời hạn chờ thanh toán của đơn, deep link quay về ứng dụng. */
@ConfigurationProperties(prefix = "app.payment")
public record PaymentProperties(
        boolean mockEnabled,
        Duration orderExpiry,
        String appRedirectUrl
) {
    public Duration orderExpiryOrDefault() {
        return orderExpiry == null ? Duration.ofMinutes(15) : orderExpiry;
    }
}
