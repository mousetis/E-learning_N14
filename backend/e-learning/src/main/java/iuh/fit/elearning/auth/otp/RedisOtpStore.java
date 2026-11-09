package iuh.fit.elearning.auth.otp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Lưu OTP trong Redis: SET otp:<purpose>:<email> <hash> EX <ttl>.
 * Các khóa phụ: otp:attempts:<purpose>:<email> (đếm nhập sai), otp:cooldown:<purpose>:<email> (chờ gửi lại).
 */
@Component
@ConditionalOnProperty(prefix = "app.otp", name = "mode", havingValue = "redis", matchIfMissing = true)
public class RedisOtpStore implements OtpStore {

    private final StringRedisTemplate redis;

    public RedisOtpStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private static String codeKey(OtpPurpose purpose, String email) {
        return "otp:" + purpose.name().toLowerCase() + ":" + email;
    }

    private static String attemptsKey(OtpPurpose purpose, String email) {
        return "otp:attempts:" + purpose.name().toLowerCase() + ":" + email;
    }

    private static String cooldownKey(OtpPurpose purpose, String email) {
        return "otp:cooldown:" + purpose.name().toLowerCase() + ":" + email;
    }

    @Override
    public void save(OtpPurpose purpose, String email, String codeHash, Duration ttl) {
        redis.opsForValue().set(codeKey(purpose, email), codeHash, ttl);
        redis.delete(attemptsKey(purpose, email));
    }

    @Override
    public Optional<String> findHash(OtpPurpose purpose, String email) {
        return Optional.ofNullable(redis.opsForValue().get(codeKey(purpose, email)));
    }

    @Override
    public long incrementAttempts(OtpPurpose purpose, String email, Duration ttl) {
        String key = attemptsKey(purpose, email);
        Long value = redis.opsForValue().increment(key);
        if (value != null && value == 1L) {
            redis.expire(key, ttl);
        }
        return value == null ? 1L : value;
    }

    @Override
    public void delete(OtpPurpose purpose, String email) {
        redis.delete(List.of(codeKey(purpose, email), attemptsKey(purpose, email)));
    }

    @Override
    public boolean tryStartCooldown(OtpPurpose purpose, String email, Duration cooldown) {
        Boolean created = redis.opsForValue().setIfAbsent(cooldownKey(purpose, email), "1", cooldown);
        return Boolean.TRUE.equals(created);
    }

    @Override
    public long cooldownRemainingSeconds(OtpPurpose purpose, String email) {
        Long seconds = redis.getExpire(cooldownKey(purpose, email), TimeUnit.SECONDS);
        return seconds == null || seconds < 0 ? 0 : seconds;
    }

    @Override
    public void clearCooldown(OtpPurpose purpose, String email) {
        redis.delete(cooldownKey(purpose, email));
    }
}
