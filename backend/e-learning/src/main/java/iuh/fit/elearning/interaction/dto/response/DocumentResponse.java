package iuh.fit.elearning.interaction.dto.response;

import java.util.UUID;

public record DocumentResponse(UUID id, String fileName, String fileType, Long fileSize) {
}
