package iuh.fit.elearning.user.dto.response;

import iuh.fit.elearning.course.dto.response.CategoryBrief;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;

import java.util.List;

/** Khóa học của giảng viên nhóm theo danh mục. */
public record InstructorCoursesGroupResponse(CategoryBrief category, List<CourseSummaryResponse> courses) {
}
