package iuh.fit.elearning.auth.otp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phương án dự phòng chỉ dùng khi dev chưa có Redis (app.otp.mode=memory).
 * Dữ liệu nằm trong bộ nhớ tiến trình, mất khi khởi động lại và không dùng được khi chạy nhiều instance.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.otp", name = "mode", havingValue = "memory")
public class InMemoryOtpStore implements OtpStore {

    private record Entry(String value, long expiresAtMillis) {
        boolean expired() {
            return System.currentTimeMillis() >= expiresAtMillis;
        }
    }

    private final Map<String, Entry> data = new ConcurrentHashMap<>();

    public InMemoryOtpStore() {
        log.warn("OTP đang lưu trong bộ nhớ (app.otp.mode=memory) - chỉ dùng cho môi trường phát triển");
    }

    private static String key(String kind, OtpPurpose purpose, String email) {
        return kind + ":" + purpose.name().toLowerCase() + ":" + email;
    }

    private Optional<Entry> get(String key) {
        Entry entry = data.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.expired()) {
            data.remove(key, entry);
            return Optional.empty();
        }
        return Optional.of(entry);
    }

    @Override
    public void save(OtpPurpose purpose, String email, String codeHash, Duration ttl) {
        data.put(key("otp", purpose, email), new Entry(codeHash, System.currentTimeMillis() + ttl.toMillis()));
        data.remove(key("attempts", purpose, email));
    }

    @Override
    public Optional<String> findHash(OtpPurpose purpose, String email) {
        return get(key("otp", purpose, email)).map(Entry::value);
    }

    @Override
    public long incrementAttempts(OtpPurpose purpose, String email, Duration ttl) {
        String key = key("attempts", purpose, email);
        Entry updated = data.compute(key, (k, old) -> {
            if (old == null || old.expired()) {
                return new Entry("1", System.currentTimeMillis() + ttl.toMillis());
            }
            return new Entry(String.valueOf(Long.parseLong(old.value()) + 1), old.expiresAtMillis());
        });
        return Long.parseLong(updated.value());
    }

    @Override
    public void delete(OtpPurpose purpose, String email) {
        data.remove(key("otp", purpose, email));
        data.remove(key("attempts", purpose, email));
    }

    @Override
    public boolean tryStartCooldown(OtpPurpose purpose, String email, Duration cooldown) {
        String key = key("cooldown", purpose, email);
        long now = System.currentTimeMillis();
        Entry fresh = new Entry("1", now + cooldown.toMillis());
        Entry result = data.compute(key, (k, old) -> (old == null || old.expired()) ? fresh : old);
        return result == fresh;
    }

    @Override
    public long cooldownRemainingSeconds(OtpPurpose purpose, String email) {
        return get(key("cooldown", purpose, email))
                .map(e -> Math.max(0, (e.expiresAtMillis() - System.currentTimeMillis() + 999) / 1000))
                .orElse(0L);
    }

    @Override
    public void clearCooldown(OtpPurpose purpose, String email) {
        data.remove(key("cooldown", purpose, email));
    }
}
