package iuh.fit.elearning.storage.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.storage.dto.StoredFile;
import iuh.fit.elearning.storage.service.FileType;
import iuh.fit.elearning.storage.service.LocalStorageService;
import iuh.fit.elearning.storage.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * Upload tệp (UC013, UC018, UC019) và phục vụ tệp lưu cục bộ.
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    /** POST /api/v1/files (multipart: file, type = IMAGE | VIDEO | DOCUMENT | PROJECT). */
    @PostMapping
    public ApiResponse<StoredFile> upload(@AuthenticationPrincipal AuthUser user,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "type", defaultValue = "IMAGE") FileType type) {
        // Video và tài liệu khóa học chỉ dành cho giảng viên / quản trị viên
        if ((type == FileType.VIDEO || type == FileType.DOCUMENT) && !(user.isInstructor() || user.isAdmin())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Chỉ giảng viên mới được tải lên loại tệp này");
        }
        return ApiResponse.ok("Tải tệp lên thành công", storageService.upload(file, type));
    }

    /** Ảnh công khai (avatar, ảnh bìa, thumbnail, banner). */
    @GetMapping("/public/{*key}")
    public ResponseEntity<Resource> publicFile(@PathVariable("key") String key) {
        String cleaned = key.startsWith("/") ? key.substring(1) : key;
        if (!cleaned.startsWith(FileType.IMAGE.getFolder() + "/")) {
            throw BusinessException.notFound("Không tìm thấy tệp");
        }
        Resource resource = storageService.load(cleaned);
        return ResponseEntity.ok()
                .contentType(mediaTypeOf(cleaned))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(resource);
    }

    /** Tệp riêng tư (video, tài liệu, bài nộp) - cần exp + sig hợp lệ do backend cấp sau khi kiểm tra quyền. */
    @GetMapping("/content/{*key}")
    public ResponseEntity<Resource> signedFile(@PathVariable("key") String key,
                                               @RequestParam("exp") long exp,
                                               @RequestParam("sig") String sig) {
        String cleaned = key.startsWith("/") ? key.substring(1) : key;
        if (!storageService.verifySignature(cleaned, exp, sig)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Liên kết tải tệp không hợp lệ hoặc đã hết hạn");
        }
        Resource resource = storageService.load(cleaned);
        String fileName = cleaned.substring(cleaned.lastIndexOf('/') + 1);
        return ResponseEntity.ok()
                .contentType(mediaTypeOf(cleaned))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .cacheControl(CacheControl.noStore())
                .body(resource);
    }

    private static MediaType mediaTypeOf(String key) {
        int dot = key.lastIndexOf('.');
        String ext = dot < 0 ? "" : key.substring(dot + 1);
        return MediaType.parseMediaType(LocalStorageService.contentTypeOf(ext));
    }
}
