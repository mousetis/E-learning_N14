package iuh.fit.elearning.course.dto.response;

import java.util.UUID;

/** videoKey là giá trị lưu trong CSDL; link xem video có thời hạn lấy qua API học / xem thử. */
public record InstructorLessonResponse(
        UUID id,
        String title,
        int orderIndex,
        int durationSeconds,
        boolean preview,
        boolean hasVideo,
        String videoKey
) {
}
