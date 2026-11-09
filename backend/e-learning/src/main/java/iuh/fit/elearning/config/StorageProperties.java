package iuh.fit.elearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** app.storage.*: thư mục lưu tệp cục bộ, thời hạn signed URL, khóa ký. */
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String mode,
        String localDir,
        Duration signedUrlTtl,
        String signingSecret
) {
}
