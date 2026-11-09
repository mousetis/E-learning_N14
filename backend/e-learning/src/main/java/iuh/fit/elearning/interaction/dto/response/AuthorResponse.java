package iuh.fit.elearning.interaction.dto.response;

import java.util.UUID;

/** Người viết (đánh giá, câu hỏi, bình luận, bài nộp). */
public record AuthorResponse(UUID id, String fullName, String avatarUrl, String role) {
}
