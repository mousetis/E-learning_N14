package iuh.fit.elearning.auth;

import iuh.fit.elearning.auth.mail.EmailSender;
import iuh.fit.elearning.auth.otp.InMemoryOtpStore;
import iuh.fit.elearning.auth.otp.OtpPurpose;
import iuh.fit.elearning.auth.otp.OtpService;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.config.OtpProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OtpServiceTest {

    private static final String EMAIL = "a@example.com";

    private EmailSender mailer;
    private OtpService service;

    @BeforeEach
    void setUp() {
        mailer = mock(EmailSender.class);
        OtpProperties props = new OtpProperties("memory", 6, Duration.ofMinutes(5), 3, Duration.ofSeconds(60));
        service = new OtpService(new InMemoryOtpStore(), props, mailer);
    }

    private String issueAndCapture() {
        service.issue(OtpPurpose.REGISTER, EMAIL, "An");
        ArgumentCaptor<String> plain = ArgumentCaptor.forClass(String.class);
        verify(mailer).send(anyString(), anyString(), plain.capture(), anyString());
        Matcher m = Pattern.compile("\\b(\\d{6})\\b").matcher(plain.getValue());
        assertTrue(m.find());
        return m.group(1);
    }

    @Test
    void correctCodeIsAcceptedOnceOnly() {
        String code = issueAndCapture();
        service.verify(OtpPurpose.REGISTER, EMAIL, code);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.REGISTER, EMAIL, code));
        assertEquals(ErrorCode.OTP_EXPIRED, ex.getErrorCode());
    }

    @Test
    void wrongCodeIsRejectedThenLockedAfterMaxAttempts() {
        String code = issueAndCapture();
        String wrong = code.equals("000000") ? "111111" : "000000";
        assertEquals(ErrorCode.OTP_INVALID, assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.REGISTER, EMAIL, wrong)).getErrorCode());
        assertEquals(ErrorCode.OTP_INVALID, assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.REGISTER, EMAIL, wrong)).getErrorCode());
        assertEquals(ErrorCode.TOO_MANY_ATTEMPTS, assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.REGISTER, EMAIL, wrong)).getErrorCode());
        // OTP đã bị hủy nên mã đúng cũng không dùng được nữa
        assertEquals(ErrorCode.OTP_EXPIRED, assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.REGISTER, EMAIL, code)).getErrorCode());
    }

    @Test
    void resendWithinCooldownIsRejected() {
        service.issue(OtpPurpose.REGISTER, EMAIL, "An");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.issue(OtpPurpose.REGISTER, EMAIL, "An"));
        assertEquals(ErrorCode.TOO_MANY_ATTEMPTS, ex.getErrorCode());
    }

    @Test
    void otpOfOnePurposeCannotBeUsedForAnother() {
        String code = issueAndCapture();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.verify(OtpPurpose.RESET_PASSWORD, EMAIL, code));
        assertEquals(ErrorCode.OTP_EXPIRED, ex.getErrorCode());
    }

    @Test
    void failedEmailSendRevokesOtpAndCooldown() {
        EmailSender failing = mock(EmailSender.class);
        org.mockito.Mockito.doThrow(new IllegalStateException("smtp down"))
                .when(failing).send(anyString(), anyString(), anyString(), any());
        OtpProperties props = new OtpProperties("memory", 6, Duration.ofMinutes(5), 3, Duration.ofSeconds(60));
        OtpService svc = new OtpService(new InMemoryOtpStore(), props, failing);
        assertThrows(IllegalStateException.class, () -> svc.issue(OtpPurpose.REGISTER, EMAIL, "An"));
        // cooldown đã được hủy: lần gửi lại ngay không bị chặn bởi cooldown
        assertThrows(IllegalStateException.class, () -> svc.issue(OtpPurpose.REGISTER, EMAIL, "An"));
    }
}
