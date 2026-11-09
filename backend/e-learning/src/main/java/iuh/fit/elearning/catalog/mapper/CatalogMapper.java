package iuh.fit.elearning.catalog.mapper;

import iuh.fit.elearning.catalog.dto.response.CategoryResponse;
import iuh.fit.elearning.catalog.dto.response.TopicResponse;
import iuh.fit.elearning.catalog.entity.Category;
import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.storage.service.StorageService;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    private final StorageService storageService;

    public CatalogMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), storageService.resolveUrl(c.getIconUrl()),
                c.getColor(), c.getSortOrder(), Boolean.TRUE.equals(c.getActive()));
    }

    public TopicResponse toResponse(Topic t) {
        return new TopicResponse(t.getId(), t.getName(), t.getSlug(), t.getSearchCount(),
                Boolean.TRUE.equals(t.getHot()));
    }
}
