package iuh.fit.elearning.auth.security;

import iuh.fit.elearning.config.JwtProperties;
import iuh.fit.elearning.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Sinh và kiểm tra access token (JWT, HS256). Claim: sub = userId, role, iat, exp, jti.
 */
@Component
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        byte[] bytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret phải có tối thiểu 32 ký tự (HS256)");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.accessTokenExpiration())))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Kiểm tra chữ ký + hạn dùng. Ném io.jsonwebtoken.ExpiredJwtException khi hết hạn
     * hoặc io.jsonwebtoken.JwtException khi token sai.
     */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessTokenTtlSeconds() {
        return properties.accessTokenExpiration().toSeconds();
    }

    public java.time.Duration getRefreshTokenTtl() {
        return properties.refreshTokenExpiration();
    }
}
