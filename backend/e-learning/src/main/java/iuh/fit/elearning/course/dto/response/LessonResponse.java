package iuh.fit.elearning.course.dto.response;

import java.util.UUID;

/** Bài học trong mục lục. Không bao giờ chứa đường dẫn video; locked = cần mua khóa để xem. */
public record LessonResponse(
        UUID id,
        String title,
        int orderIndex,
        int durationSeconds,
        boolean preview,
        boolean locked,
        Boolean completed,
        Integer lastPositionSeconds
) {
}
