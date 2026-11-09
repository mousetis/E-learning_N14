package iuh.fit.elearning.user.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/** UC007: hồ sơ giảng viên công khai. following chỉ có khi gọi kèm access token. */
public record InstructorProfileResponse(
        UUID id,
        String fullName,
        String avatarUrl,
        String coverUrl,
        String jobTitle,
        String bio,
        String expertise,
        String introduction,
        String location,
        int followerCount,
        int studentCount,
        int courseCount,
        BigDecimal avgRating,
        int reviewCount,
        Boolean following
) {
}
