package iuh.fit.elearning.learning.dto.response;

import java.math.BigDecimal;

public record ProgressResponse(
        boolean lessonCompleted,
        BigDecimal courseProgress,
        boolean courseCompleted,
        String certificateCode
) {
}
