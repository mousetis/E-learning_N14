package iuh.fit.elearning.catalog.controller;

import iuh.fit.elearning.catalog.dto.response.CategoryResponse;
import iuh.fit.elearning.catalog.dto.response.TopicResponse;
import iuh.fit.elearning.catalog.service.CatalogService;
import iuh.fit.elearning.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** UC004, UC005: danh mục và chủ đề nổi bật (public). */
@RestController
@RequestMapping("/api/v1")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.ok(catalogService.activeCategories());
    }

    @GetMapping("/topics/hot")
    public ApiResponse<List<TopicResponse>> hotTopics() {
        return ApiResponse.ok(catalogService.hotTopics());
    }
}
