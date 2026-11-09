package iuh.fit.elearning.course.dto.response;

import java.util.UUID;

public record BenefitResponse(UUID id, String content, String icon, int sortOrder) {
}
