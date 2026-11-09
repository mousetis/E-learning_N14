package iuh.fit.elearning.storage.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.storage.dto.StoredFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * Lưu trữ tệp (SDS mục 2.5). Giai đoạn 1: LocalStorageService (thư mục uploads/);
 * có thể thay bằng Cloudinary / S3 mà không sửa nghiệp vụ.
 * <p>
 * Giá trị lưu trong CSDL ("stored") là key (vd images/2026/11/abc.png) hoặc một URL http(s) bên ngoài.
 */
public interface StorageService {

    StoredFile upload(MultipartFile file, FileType type);

    /** URL ứng dụng dùng được: URL http(s) giữ nguyên; key ảnh -> URL công khai; key khác -> signed URL có thời hạn. */
    String resolveUrl(String stored);

    /** Signed URL có thời hạn cho tệp riêng tư (video, tài liệu, bài nộp). */
    String createSignedUrl(String stored, Duration ttl);

    /** Đưa URL công khai của chính hệ thống về key để lưu CSDL; giá trị khác giữ nguyên. */
    String normalizeReference(String urlOrKey);

    boolean verifySignature(String key, long expiresAtEpochSeconds, String signature);

    /** Mở tệp theo key; ném BusinessException RESOURCE_NOT_FOUND nếu không tồn tại. */
    Resource load(String key);

    void delete(String stored);

    Duration defaultSignedUrlTtl();

    /**
     * Chuẩn hóa tham chiếu ảnh do client gửi lên (avatar, ảnh bìa, thumbnail, banner...):
     * chỉ chấp nhận URL http(s) bên ngoài hoặc key ảnh (images/...) đã upload qua POST /files?type=IMAGE,
     * để không thể gắn tệp riêng tư (video, bài nộp) làm ảnh công khai.
     */
    default String requireImageRef(String urlOrKey) {
        String key = normalizeReference(urlOrKey);
        if (key == null || key.isBlank()) {
            return null;
        }
        String lower = key.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || key.startsWith("images/")) {
            return key;
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST,
                "Ảnh không hợp lệ: hãy tải ảnh lên qua POST /api/v1/files với type=IMAGE");
    }
}
