package iuh.fit.elearning.user.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/** Giảng viên nổi bật (danh sách rút gọn). */
public record InstructorCardResponse(
        UUID id,
        String fullName,
        String avatarUrl,
        String jobTitle,
        String expertise,
        int followerCount,
        int studentCount,
        int courseCount,
        BigDecimal avgRating,
        int reviewCount
) {
}
