package iuh.fit.elearning.auth.otp;

import java.time.Duration;
import java.util.Optional;

/**
 * Kho lưu OTP tạm thời có TTL (SDS 2.5). Cài đặt: RedisOtpStore (mặc định) hoặc InMemoryOtpStore (chỉ để dev).
 * Chỉ lưu mã băm SHA-256 của OTP, không lưu mã gốc.
 */
public interface OtpStore {

    /** Lưu (ghi đè) mã băm OTP với TTL và đặt lại bộ đếm số lần nhập sai. */
    void save(OtpPurpose purpose, String email, String codeHash, Duration ttl);

    Optional<String> findHash(OtpPurpose purpose, String email);

    /** Tăng bộ đếm nhập sai và trả về giá trị mới. */
    long incrementAttempts(OtpPurpose purpose, String email, Duration ttl);

    /** Xóa OTP cùng bộ đếm nhập sai. */
    void delete(OtpPurpose purpose, String email);

    /** Bắt đầu thời gian chờ gửi lại. Trả về false nếu vẫn đang trong thời gian chờ. */
    boolean tryStartCooldown(OtpPurpose purpose, String email, Duration cooldown);

    /** Số giây còn lại của thời gian chờ gửi lại (0 nếu không còn). */
    long cooldownRemainingSeconds(OtpPurpose purpose, String email);

    void clearCooldown(OtpPurpose purpose, String email);
}
