package iuh.fit.elearning.notification.push;

import java.util.Collection;
import java.util.Map;

/**
 * Gửi thông báo đẩy tới thiết bị (SDS 2.5). Giai đoạn 1: LoggingPushSender (chỉ ghi log);
 * giai đoạn 2: FcmPushSender (Firebase Admin SDK) - thêm lớp cài đặt và đổi app.notification.mode.
 */
public interface PushSender {

    void send(Collection<String> deviceTokens, String title, String body, Map<String, String> data);
}
