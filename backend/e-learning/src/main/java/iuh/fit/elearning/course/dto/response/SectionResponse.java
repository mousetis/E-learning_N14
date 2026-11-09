package iuh.fit.elearning.course.dto.response;

import java.util.List;
import java.util.UUID;

public record SectionResponse(
        UUID id,
        String title,
        int orderIndex,
        int lessonCount,
        int durationSeconds,
        List<LessonResponse> lessons
) {
}
