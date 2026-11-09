package iuh.fit.elearning.auth.mail;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public class SmtpEmailSender implements EmailSender {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String username;
    private final String from;
    private final String fromName;

    public SmtpEmailSender(ObjectProvider<JavaMailSender> mailSenderProvider,
                           @Value("${spring.mail.username:}") String username,
                           @Value("${app.mail.from:}") String from,
                           @Value("${app.mail.from-name:E-Learning}") String fromName) {
        this.mailSenderProvider = mailSenderProvider;
        this.username = username;
        this.from = (from == null || from.isBlank()) ? username : from;
        this.fromName = fromName;
    }

    @Override
    public void send(String to, String subject, String plainText, String html) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (username == null || username.isBlank() || sender == null) {
            // Chế độ dev: chưa cấu hình SMTP -> ghi nội dung ra log để vẫn lấy được mã OTP
            log.warn("[DEV - CHƯA CẤU HÌNH SMTP] Email tới {} | {} | {}", to, subject, plainText);
            return;
        }
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, html);
            sender.send(message);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("Gửi email tới {} thất bại: {}", to, e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE,
                    "Không gửi được email xác thực, vui lòng thử lại sau");
        }
    }
}
