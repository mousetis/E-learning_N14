package iuh.fit.elearning.storage.service;

import java.util.Set;

/**
 * Loại tệp tải lên và quy tắc kiểm tra (SDS mục 8: ảnh ≤ 5MB; dự án/tài liệu ≤ 50MB; video mp4).
 */
public enum FileType {

    IMAGE("images", 5L * 1024 * 1024, Set.of("jpg", "jpeg", "png", "webp")),
    VIDEO("videos", 500L * 1024 * 1024, Set.of("mp4")),
    DOCUMENT("documents", 50L * 1024 * 1024, Set.of("pdf", "zip", "docx", "jpg", "jpeg", "png")),
    PROJECT("projects", 50L * 1024 * 1024, Set.of("jpg", "jpeg", "png", "pdf", "zip"));

    private final String folder;
    private final long maxBytes;
    private final Set<String> extensions;

    FileType(String folder, long maxBytes, Set<String> extensions) {
        this.folder = folder;
        this.maxBytes = maxBytes;
        this.extensions = extensions;
    }

    public String getFolder() {
        return folder;
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public Set<String> getExtensions() {
        return extensions;
    }
}
