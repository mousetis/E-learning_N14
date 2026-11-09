package iuh.fit.elearning.course.dto.response;

import java.util.List;
import java.util.UUID;

public record CurriculumResponse(
        UUID courseId,
        int totalSections,
        int totalLessons,
        int totalDurationSeconds,
        List<SectionResponse> sections
) {
}
