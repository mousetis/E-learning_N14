package iuh.fit.elearning.statistics.controller;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.statistics.dto.StatisticsReport;
import iuh.fit.elearning.statistics.service.ReportExporter;
import iuh.fit.elearning.statistics.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/** UC025: thống kê và xuất báo cáo (ADMIN). type = revenue | students | courses | instructors. */
@RestController
@RequestMapping("/api/v1/admin/statistics")
public class StatisticsController {

    private static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final StatisticsService statisticsService;
    private final ReportExporter exporter;

    public StatisticsController(StatisticsService statisticsService, ReportExporter exporter) {
        this.statisticsService = statisticsService;
        this.exporter = exporter;
    }

    @GetMapping("/{type}")
    public ApiResponse<StatisticsReport> statistics(
            @PathVariable String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy) {
        return ApiResponse.ok(statisticsService.build(type, from, to, groupBy));
    }

    @GetMapping("/{type}/export")
    public ResponseEntity<byte[]> export(
            @PathVariable String type,
            @RequestParam(defaultValue = "xlsx") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy) {
        StatisticsReport report = statisticsService.build(type, from, to, groupBy);
        String name = "bao-cao-" + report.type() + "-" + report.from() + "_" + report.to();
        byte[] body;
        MediaType mediaType;
        String extension;
        switch (format.trim().toLowerCase()) {
            case "xlsx" -> {
                body = exporter.xlsx(report);
                mediaType = XLSX;
                extension = "xlsx";
            }
            case "pdf" -> {
                body = exporter.pdf(report);
                mediaType = MediaType.APPLICATION_PDF;
                extension = "pdf";
            }
            default -> throw BusinessException.badRequest("format phải là xlsx hoặc pdf");
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(name + "." + extension).build().toString())
                .cacheControl(CacheControl.noStore())
                .body(body);
    }
}
