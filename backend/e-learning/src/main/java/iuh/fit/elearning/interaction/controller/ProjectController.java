package iuh.fit.elearning.interaction.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.interaction.dto.response.DownloadResponse;
import iuh.fit.elearning.interaction.dto.response.ProjectResponse;
import iuh.fit.elearning.interaction.dto.response.SubmissionResponse;
import iuh.fit.elearning.interaction.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/** UC013: dự án, tài liệu và bài nộp (học viên đã ghi danh). */
@RestController
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/courses/{id}/project")
    public ApiResponse<ProjectResponse> project(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(projectService.project(user, id));
    }

    @GetMapping("/documents/{id}/download")
    public ApiResponse<DownloadResponse> download(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(projectService.download(user, id));
    }

    @GetMapping("/courses/{id}/submissions")
    public ApiResponse<PageResponse<SubmissionResponse>> submissions(@AuthenticationPrincipal AuthUser user,
                                                                     @PathVariable UUID id,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(projectService.submissions(user, id, page, size));
    }

    /** multipart/form-data: title, description (tùy chọn), file (jpg/png/pdf/zip, tối đa 50MB). */
    @PostMapping(value = "/courses/{id}/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubmissionResponse> submit(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id,
                                                  @RequestParam("title") String title,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("Đã nộp dự án", projectService.submit(user, id, title, description, file));
    }
}
