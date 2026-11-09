package iuh.fit.elearning.catalog.controller;

import iuh.fit.elearning.catalog.dto.request.CategoryRequest;
import iuh.fit.elearning.catalog.dto.request.TopicRequest;
import iuh.fit.elearning.catalog.dto.response.CategoryResponse;
import iuh.fit.elearning.catalog.dto.response.TopicResponse;
import iuh.fit.elearning.catalog.service.CatalogService;
import iuh.fit.elearning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** UC022: quản lý danh mục, chủ đề (ADMIN - phân quyền ở SecurityConfig theo /api/v1/admin/**). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminCatalogController {

    private final CatalogService catalogService;

    public AdminCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.ok(catalogService.allCategories());
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Đã tạo danh mục", catalogService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable UUID id,
                                                        @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Đã cập nhật danh mục", catalogService.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable UUID id) {
        catalogService.deleteCategory(id);
        return ApiResponse.message("Đã xóa danh mục");
    }

    @GetMapping("/topics")
    public ApiResponse<List<TopicResponse>> topics() {
        return ApiResponse.ok(catalogService.allTopics());
    }

    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TopicResponse> createTopic(@Valid @RequestBody TopicRequest request) {
        return ApiResponse.ok("Đã tạo chủ đề", catalogService.createTopic(request));
    }

    @PutMapping("/topics/{id}")
    public ApiResponse<TopicResponse> updateTopic(@PathVariable UUID id, @Valid @RequestBody TopicRequest request) {
        return ApiResponse.ok("Đã cập nhật chủ đề", catalogService.updateTopic(id, request));
    }

    @DeleteMapping("/topics/{id}")
    public ApiResponse<Void> deleteTopic(@PathVariable UUID id) {
        catalogService.deleteTopic(id);
        return ApiResponse.message("Đã xóa chủ đề");
    }
}
