package iuh.fit.elearning.course.dto.response;

import java.util.List;
import java.util.UUID;

public record InstructorSectionResponse(UUID id, String title, int orderIndex, List<InstructorLessonResponse> lessons) {
}
