package iuh.fit.elearning.interaction.dto.response;

import java.util.List;
import java.util.UUID;

/** Mô tả dự án của khóa học và danh sách tài liệu (UC013). */
public record ProjectResponse(UUID courseId, String description, List<DocumentResponse> documents) {
}
