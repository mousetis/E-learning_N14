package iuh.fit.elearning.storage.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.HashUtils;
import iuh.fit.elearning.config.StorageProperties;
import iuh.fit.elearning.storage.dto.StoredFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

/**
 * Lưu tệp cục bộ trong thư mục app.storage.local-dir. Signed URL = HMAC-SHA256(key + ":" + hạn) (SDS 2.5).
 */
@Slf4j
@Service
public class LocalStorageService implements StorageService {

    private static final String PUBLIC_PREFIX = "/api/v1/files/public/";
    private static final String CONTENT_PREFIX = "/api/v1/files/content/";

    private final StorageProperties properties;
    private final String publicBaseUrl;
    private final Path root;

    public LocalStorageService(StorageProperties properties,
                               @Value("${app.public-base-url}") String publicBaseUrl) {
        this.properties = properties;
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
        this.root = Paths.get(properties.localDir()).toAbsolutePath().normalize();
    }

    @Override
    public StoredFile upload(MultipartFile file, FileType type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Vui lòng chọn tệp cần tải lên");
        }
        if (file.getSize() > type.getMaxBytes()) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE,
                    "Tệp vượt quá dung lượng tối đa " + (type.getMaxBytes() / (1024 * 1024)) + "MB");
        }
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = extensionOf(originalName);
        if (!type.getExtensions().contains(ext)) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE,
                    "Định dạng tệp không được hỗ trợ. Chấp nhận: " + String.join(", ", type.getExtensions()));
        }
        LocalDate today = LocalDate.now();
        String key = type.getFolder() + "/" + today.getYear() + "/" + String.format("%02d", today.getMonthValue())
                + "/" + UUID.randomUUID() + "." + ext;
        Path target = resolveSafe(key);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            log.error("Không lưu được tệp {}", key, e);
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "Lỗi tải tệp lên, vui lòng thử lại");
        }
        String contentType = contentTypeOf(ext);
        return new StoredFile(key, resolveUrl(key), originalName, ext, file.getSize(), contentType);
    }

    @Override
    public String resolveUrl(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        if (isExternal(stored)) {
            return stored;
        }
        if (stored.startsWith(FileType.IMAGE.getFolder() + "/")) {
            return publicBaseUrl + PUBLIC_PREFIX + stored;
        }
        return createSignedUrl(stored, defaultSignedUrlTtl());
    }

    @Override
    public String createSignedUrl(String stored, Duration ttl) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        if (isExternal(stored)) {
            return stored;
        }
        long exp = java.time.Instant.now().plus(ttl).getEpochSecond();
        String sig = sign(stored, exp);
        return publicBaseUrl + CONTENT_PREFIX + stored + "?exp=" + exp + "&sig=" + sig;
    }

    @Override
    public String normalizeReference(String urlOrKey) {
        if (urlOrKey == null || urlOrKey.isBlank()) {
            return null;
        }
        String prefix = publicBaseUrl + PUBLIC_PREFIX;
        if (urlOrKey.startsWith(prefix)) {
            return urlOrKey.substring(prefix.length());
        }
        int signedAt = urlOrKey.indexOf(publicBaseUrl + CONTENT_PREFIX);
        if (signedAt == 0) {
            String rest = urlOrKey.substring((publicBaseUrl + CONTENT_PREFIX).length());
            int q = rest.indexOf('?');
            return q >= 0 ? rest.substring(0, q) : rest;
        }
        return urlOrKey;
    }

    @Override
    public boolean verifySignature(String key, long expiresAtEpochSeconds, String signature) {
        if (signature == null || expiresAtEpochSeconds < java.time.Instant.now().getEpochSecond()) {
            return false;
        }
        return HashUtils.constantTimeEquals(sign(key, expiresAtEpochSeconds), signature);
    }

    @Override
    public Resource load(String key) {
        Path path = resolveSafe(key);
        if (!Files.isRegularFile(path)) {
            throw BusinessException.notFound("Không tìm thấy tệp");
        }
        return new FileSystemResource(path);
    }

    @Override
    public void delete(String stored) {
        if (stored == null || stored.isBlank() || isExternal(stored)) {
            return;
        }
        try {
            Files.deleteIfExists(resolveSafe(stored));
        } catch (Exception e) {
            log.warn("Không xóa được tệp {}: {}", stored, e.getMessage());
        }
    }

    @Override
    public Duration defaultSignedUrlTtl() {
        return properties.signedUrlTtl() == null ? Duration.ofHours(1) : properties.signedUrlTtl();
    }

    /** Chuyển key thành đường dẫn tuyệt đối và chặn truy cập ra ngoài thư mục gốc (path traversal). */
    private Path resolveSafe(String key) {
        if (key == null || key.contains("..") || key.startsWith("/") || key.startsWith("\\")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Đường dẫn tệp không hợp lệ");
        }
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Đường dẫn tệp không hợp lệ");
        }
        return target;
    }

    private String sign(String key, long exp) {
        return HashUtils.hmacSha256Hex(properties.signingSecret(), key + ":" + exp);
    }

    private static boolean isExternal(String value) {
        String v = value.toLowerCase(Locale.ROOT);
        return v.startsWith("http://") || v.startsWith("https://");
    }

    private static String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /** Suy ra Content-Type từ phần mở rộng (dùng cả khi phục vụ tệp). */
    public static String contentTypeOf(String ext) {
        return switch (ext.toLowerCase(Locale.ROOT)) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "mp4" -> "video/mp4";
            case "pdf" -> "application/pdf";
            case "zip" -> "application/zip";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }
}
