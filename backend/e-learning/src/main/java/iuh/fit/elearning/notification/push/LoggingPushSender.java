package iuh.fit.elearning.notification.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.notification", name = "mode", havingValue = "mock", matchIfMissing = true)
public class LoggingPushSender implements PushSender {

    @Override
    public void send(Collection<String> deviceTokens, String title, String body, Map<String, String> data) {
        log.info("[PUSH-MOCK] {} thiết bị | {} | {} | {}", deviceTokens.size(), title, body, data);
    }
}
