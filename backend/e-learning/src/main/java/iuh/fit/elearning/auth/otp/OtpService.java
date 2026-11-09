package iuh.fit.elearning.auth.otp;

import iuh.fit.elearning.auth.mail.EmailSender;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.CodeGenerator;
import iuh.fit.elearning.common.util.HashUtils;
import iuh.fit.elearning.config.OtpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Sinh, gửi và kiểm tra OTP (SDS 8.1): 6 chữ số ngẫu nhiên (SecureRandom), lưu SHA-256 hash trong OtpStore,
 * TTL 5 phút, tối đa 5 lần nhập sai, gửi lại sau 60 giây, xóa ngay sau khi dùng.
 */
@Slf4j
@Service
public class OtpService {

    private final OtpStore store;
    private final OtpProperties props;
    private final EmailSender emailSender;

    public OtpService(OtpStore store, OtpProperties props, EmailSender emailSender) {
        this.store = store;
        this.props = props;
        this.emailSender = emailSender;
    }

    /** Sinh OTP mới (ghi đè OTP cũ) và gửi qua email. */
    public void issue(OtpPurpose purpose, String email, String fullName) {
        try {
            if (!store.tryStartCooldown(purpose, email, props.resendCooldown())) {
                long remain = Math.max(1, store.cooldownRemainingSeconds(purpose, email));
                throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS,
                        "Vui lòng đợi " + remain + " giây trước khi yêu cầu mã mới");
            }
            String code = CodeGenerator.numericOtp(props.length());
            try {
                store.save(purpose, email, HashUtils.sha256Hex(code), props.ttl());
                sendMail(purpose, email, fullName, code);
            } catch (RuntimeException e) {
                // Gửi thất bại: hủy OTP và cho phép người dùng yêu cầu lại ngay
                store.delete(purpose, email);
                store.clearCooldown(purpose, email);
                throw e;
            }
        } catch (DataAccessException e) {
            log.error("Không truy cập được kho OTP (Redis): {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    /** Kiểm tra OTP; thành công thì xóa OTP ngay. */
    public void verify(OtpPurpose purpose, String email, String code) {
        try {
            Optional<String> stored = store.findHash(purpose, email);
            if (stored.isEmpty()) {
                throw new BusinessException(ErrorCode.OTP_EXPIRED,
                        "Mã OTP đã hết hạn hoặc chưa được gửi, vui lòng yêu cầu mã mới");
            }
            if (!HashUtils.constantTimeEquals(stored.get(), HashUtils.sha256Hex(code))) {
                long attempts = store.incrementAttempts(purpose, email, props.ttl());
                if (attempts >= props.maxAttempts()) {
                    store.delete(purpose, email);
                    store.clearCooldown(purpose, email);
                    throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS,
                            "Bạn đã nhập sai quá " + props.maxAttempts() + " lần, vui lòng yêu cầu gửi lại mã");
                }
                throw new BusinessException(ErrorCode.OTP_INVALID,
                        "Mã OTP không đúng, bạn còn " + (props.maxAttempts() - attempts) + " lần thử");
            }
            store.delete(purpose, email);
        } catch (DataAccessException e) {
            log.error("Không truy cập được kho OTP (Redis): {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    public long ttlSeconds() {
        return props.ttl().toSeconds();
    }

    public long resendCooldownSeconds() {
        return props.resendCooldown().toSeconds();
    }

    private void sendMail(OtpPurpose purpose, String email, String fullName, String code) {
        String action = purpose == OtpPurpose.REGISTER ? "xác thực tài khoản" : "đặt lại mật khẩu";
        long minutes = Math.max(1, props.ttl().toMinutes());
        String name = (fullName == null || fullName.isBlank()) ? "bạn" : fullName;
        String subject = "[E-Learning] Mã OTP " + action;
        String plain = "Xin chào " + name + ",\n\nMã OTP để " + action + " của bạn là: " + code
                + "\nMã có hiệu lực trong " + minutes + " phút. Không chia sẻ mã này với bất kỳ ai."
                + "\n\nNếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.";
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:480px;margin:auto\">"
                + "<h2 style=\"color:#2563eb\">E-Learning</h2>"
                + "<p>Xin chào <b>" + escapeHtml(name) + "</b>,</p>"
                + "<p>Mã OTP để " + action + " của bạn là:</p>"
                + "<p style=\"font-size:32px;letter-spacing:8px;font-weight:bold;background:#f1f5f9;"
                + "padding:12px 16px;text-align:center;border-radius:8px\">" + code + "</p>"
                + "<p>Mã có hiệu lực trong <b>" + minutes + " phút</b>. Không chia sẻ mã này với bất kỳ ai.</p>"
                + "<p style=\"color:#64748b;font-size:12px\">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.</p>"
                + "</div>";
        emailSender.send(email, subject, plain, html);
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
