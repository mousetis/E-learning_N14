package iuh.fit.elearning.catalog.service;

import iuh.fit.elearning.catalog.dto.request.CategoryRequest;
import iuh.fit.elearning.catalog.dto.request.TopicRequest;
import iuh.fit.elearning.catalog.dto.response.CategoryResponse;
import iuh.fit.elearning.catalog.dto.response.TopicResponse;
import iuh.fit.elearning.catalog.entity.Category;
import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.catalog.mapper.CatalogMapper;
import iuh.fit.elearning.catalog.repository.CategoryRepository;
import iuh.fit.elearning.catalog.repository.TopicRepository;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.SlugUtils;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.storage.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Danh mục và chủ đề: UC004, UC005 (public) và UC022 (quản trị). */
@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final CatalogMapper mapper;
    private final StorageService storageService;

    public CatalogService(CategoryRepository categoryRepository, TopicRepository topicRepository,
                          CourseRepository courseRepository, CatalogMapper mapper, StorageService storageService) {
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
        this.mapper = mapper;
        this.storageService = storageService;
    }

    // ------------------------------------------------------------------ public

    @Transactional(readOnly = true)
    public List<CategoryResponse> activeCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> hotTopics() {
        return topicRepository.findByHotTrueOrderBySearchCountDesc().stream().map(mapper::toResponse).toList();
    }

    // ------------------------------------------------------------------ admin: danh mục

    @Transactional(readOnly = true)
    public List<CategoryResponse> allCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Tên danh mục đã tồn tại");
        }
        Category category = Category.builder()
                .name(name)
                .slug(uniqueCategorySlug(name, null))
                .iconUrl(storageService.requireImageRef(request.iconUrl()))
                .color(request.color())
                .sortOrder(request.sortOrder() == null ? 0 : request.sortOrder())
                .active(request.active() == null || request.active())
                .build();
        return mapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = request.name().trim();
        if (!name.equalsIgnoreCase(category.getName())
                && categoryRepository.findAll().stream().anyMatch(c -> !c.getId().equals(id)
                && c.getName().equalsIgnoreCase(name))) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Tên danh mục đã tồn tại");
        }
        if (!name.equals(category.getName())) {
            category.setName(name);
            category.setSlug(uniqueCategorySlug(name, id));
        }
        category.setIconUrl(storageService.requireImageRef(request.iconUrl()));
        category.setColor(request.color());
        if (request.sortOrder() != null) {
            category.setSortOrder(request.sortOrder());
        }
        if (request.active() != null) {
            category.setActive(request.active());
        }
        return mapper.toResponse(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findCategory(id);
        if (courseRepository.existsByCategoryId(id)) {
            throw new BusinessException(ErrorCode.CATEGORY_IN_USE);
        }
        categoryRepository.delete(category);
    }

    // ------------------------------------------------------------------ admin: chủ đề

    @Transactional(readOnly = true)
    public List<TopicResponse> allTopics() {
        return topicRepository.findAllByOrderByNameAsc().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public TopicResponse createTopic(TopicRequest request) {
        String name = request.name().trim();
        if (topicRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Tên chủ đề đã tồn tại");
        }
        Topic topic = Topic.builder()
                .name(name)
                .slug(uniqueTopicSlug(name, null))
                .hot(request.hot() != null && request.hot())
                .build();
        return mapper.toResponse(topicRepository.save(topic));
    }

    @Transactional
    public TopicResponse updateTopic(UUID id, TopicRequest request) {
        Topic topic = findTopic(id);
        String name = request.name().trim();
        if (!name.equalsIgnoreCase(topic.getName()) && topicRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Tên chủ đề đã tồn tại");
        }
        if (!name.equals(topic.getName())) {
            topic.setName(name);
            topic.setSlug(uniqueTopicSlug(name, id));
        }
        if (request.hot() != null) {
            topic.setHot(request.hot());
        }
        return mapper.toResponse(topic);
    }

    @Transactional
    public void deleteTopic(UUID id) {
        Topic topic = findTopic(id);
        if (courseRepository.existsByTopicId(id)) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Chủ đề đang được gắn với khóa học, không thể xóa");
        }
        topicRepository.delete(topic);
    }

    // ------------------------------------------------------------------ nội bộ

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy danh mục"));
    }

    private Topic findTopic(UUID id) {
        return topicRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy chủ đề"));
    }

    private String uniqueCategorySlug(String name, UUID selfId) {
        String base = SlugUtils.slugify(name);
        if (base.isEmpty()) {
            base = "danh-muc";
        }
        String slug = base;
        int i = 2;
        while (true) {
            Category existing = categoryRepository.findBySlug(slug).orElse(null);
            if (existing == null || existing.getId().equals(selfId)) {
                return slug;
            }
            slug = base + "-" + i++;
        }
    }

    private String uniqueTopicSlug(String name, UUID selfId) {
        String base = SlugUtils.slugify(name);
        if (base.isEmpty()) {
            base = "chu-de";
        }
        String slug = base;
        int i = 2;
        while (true) {
            Topic existing = topicRepository.findBySlug(slug).orElse(null);
            if (existing == null || existing.getId().equals(selfId)) {
                return slug;
            }
            slug = base + "-" + i++;
        }
    }
}
