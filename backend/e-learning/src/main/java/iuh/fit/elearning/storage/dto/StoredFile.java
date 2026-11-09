package iuh.fit.elearning.storage.dto;

/**
 * Kết quả upload: key là giá trị lưu vào CSDL (hoặc gửi lại cho API khác), url để hiển thị / tải ngay.
 */
public record StoredFile(
        String key,
        String url,
        String fileName,
        String fileType,
        long size,
        String contentType
) {
}
