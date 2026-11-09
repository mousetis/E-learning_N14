package iuh.fit.elearning.auth.mail;

/**
 * Gửi email (SDS 2.5). Cài đặt hiện tại: SmtpEmailSender (spring-boot-starter-mail),
 * tự chuyển sang ghi log khi chưa cấu hình MAIL_USERNAME (chỉ dùng khi dev).
 */
public interface EmailSender {

    /**
     * @param plainText nội dung dạng văn bản (cũng là nội dung được ghi log ở chế độ dev)
     * @param html      nội dung HTML
     */
    void send(String to, String subject, String plainText, String html);
}
