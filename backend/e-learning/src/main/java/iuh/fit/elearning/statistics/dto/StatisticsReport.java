package iuh.fit.elearning.statistics.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Báo cáo thống kê dạng bảng chung cho 4 loại (revenue, students, courses, instructors):
 * summary = số liệu tổng, columns = tên cột, rows = các dòng theo thứ tự cột.
 */
public record StatisticsReport(
        String type,
        String title,
        LocalDate from,
        LocalDate to,
        String groupBy,
        Map<String, Object> summary,
        List<String> columns,
        List<List<Object>> rows
) {
}
