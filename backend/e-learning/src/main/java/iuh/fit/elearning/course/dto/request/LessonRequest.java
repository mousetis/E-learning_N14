package iuh.fit.elearning.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonRequest(
        @NotBlank(message = "Tên bài học không được để trống")
        @Size(max = 255, message = "Tên bài học tối đa 255 ký tự")
        String title,
        /** Key trả về từ POST /files?type=VIDEO (hoặc URL http(s) bên ngoài). */
        String videoUrl,
        /** Thời lượng video (giây) do giảng viên cung cấp. */
        @Min(value = 0, message = "Thời lượng không âm")
        Integer durationSeconds,
        Boolean preview,
        @Min(value = 1, message = "Thứ tự bắt đầu từ 1")
        Integer orderIndex
) {
}
