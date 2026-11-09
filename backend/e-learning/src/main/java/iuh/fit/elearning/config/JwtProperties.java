package iuh.fit.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Cấu hình JWT: app.jwt.secret, app.jwt.access-token-expiration (vd 15m), app.jwt.refresh-token-expiration (vd 7d).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration
) {
}
