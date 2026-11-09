package iuh.fit.elearning.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import iuh.fit.elearning.auth.security.JwtService;
import iuh.fit.elearning.config.JwtProperties;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234567890";

    private static User user() {
        User u = User.builder().fullName("An").email("a@example.com").role(UserRole.INSTRUCTOR).build();
        u.setId(UUID.randomUUID());
        return u;
    }

    @Test
    void accessTokenRoundTrip() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(15), Duration.ofDays(7)));
        User u = user();
        Claims claims = jwt.parse(jwt.generateAccessToken(u));
        assertEquals(u.getId().toString(), claims.getSubject());
        assertEquals("INSTRUCTOR", claims.get("role", String.class));
        assertNotNull(claims.getId());
        assertEquals(900, jwt.getAccessTokenTtlSeconds());
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(new JwtProperties(SECRET, Duration.ofSeconds(-5), Duration.ofDays(7)));
        String token = jwt.generateAccessToken(user());
        assertThrows(ExpiredJwtException.class, () -> jwt.parse(token));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        JwtService a = new JwtService(new JwtProperties(SECRET, Duration.ofMinutes(15), Duration.ofDays(7)));
        JwtService b = new JwtService(new JwtProperties(SECRET.replace('u', 'x'), Duration.ofMinutes(15), Duration.ofDays(7)));
        String token = a.generateAccessToken(user());
        assertThrows(JwtException.class, () -> b.parse(token));
    }

    @Test
    void shortSecretIsRefused() {
        assertThrows(IllegalStateException.class,
                () -> new JwtService(new JwtProperties("short", Duration.ofMinutes(1), Duration.ofDays(1))));
    }
}
