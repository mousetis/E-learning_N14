package iuh.fit.elearning.course.dto.response;

import java.util.UUID;

public record InstructorBrief(UUID id, String fullName, String avatarUrl, String jobTitle) {
}
