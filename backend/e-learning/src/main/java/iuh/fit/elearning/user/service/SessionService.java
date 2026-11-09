package iuh.fit.elearning.user.service;

import iuh.fit.elearning.common.util.HashUtils;
import iuh.fit.elearning.config.JwtProperties;
import iuh.fit.elearning.user.entity.RefreshToken;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Quản lý phiên đăng nhập = refresh token (SDS 8.1): chỉ lưu SHA-256 hash, có xoay vòng, thu hồi theo thiết bị / toàn bộ.
 */
@Service
public class SessionService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public SessionService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    /** Tạo refresh token mới, lưu hash và trả về chuỗi gốc (chỉ trả cho client một lần). */
    @Transactional
    public String createRefreshToken(User user, String fcmToken, String deviceInfo) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(HashUtils.sha256Hex(raw))
                .fcmToken(blankToNull(fcmToken))
                .deviceInfo(blankToNull(deviceInfo))
                .expiresAt(Instant.now().plus(jwtProperties.refreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(token);
        return raw;
    }

    @Transactional(readOnly = true)
    public java.util.Optional<RefreshToken> findByRaw(String rawToken) {
        return refreshTokenRepository.findByTokenHash(HashUtils.sha256Hex(rawToken));
    }

    /** Thu hồi một phiên và gỡ FCM token của thiết bị đó. */
    @Transactional
    public void revoke(RefreshToken token) {
        if (token.getRevokedAt() == null) {
            token.setRevokedAt(Instant.now());
        }
        token.setFcmToken(null);
    }

    /** Thu hồi mọi phiên của người dùng (khóa / xóa tài khoản, đặt lại mật khẩu, phát hiện dùng lại token). */
    @Transactional
    public void revokeAll(UUID userId) {
        for (RefreshToken token : refreshTokenRepository.findByUserIdAndRevokedAtIsNull(userId)) {
            revoke(token);
        }
    }

    /** Thu hồi các phiên khác (đổi mật khẩu); nếu keepRawToken null thì thu hồi tất cả. */
    @Transactional
    public void revokeOthers(UUID userId, String keepRawToken) {
        String keepHash = (keepRawToken == null || keepRawToken.isBlank()) ? null : HashUtils.sha256Hex(keepRawToken);
        List<RefreshToken> active = refreshTokenRepository.findByUserIdAndRevokedAtIsNull(userId);
        for (RefreshToken token : active) {
            if (keepHash == null || !keepHash.equals(token.getTokenHash())) {
                revoke(token);
            }
        }
    }

    /** Các FCM token đang hoạt động của người dùng (dùng để gửi thông báo đẩy). */
    @Transactional(readOnly = true)
    public List<String> activeFcmTokens(UUID userId) {
        Instant now = Instant.now();
        return refreshTokenRepository.findByUserIdAndRevokedAtIsNull(userId).stream()
                .filter(t -> t.getExpiresAt().isAfter(now))
                .map(RefreshToken::getFcmToken)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .toList();
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
