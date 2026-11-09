package iuh.fit.elearning.course.mapper;

import iuh.fit.elearning.catalog.entity.Category;
import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.course.dto.response.BenefitResponse;
import iuh.fit.elearning.course.dto.response.CategoryBrief;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.dto.response.InstructorBrief;
import iuh.fit.elearning.course.dto.response.TopicBrief;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.CourseBenefit;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CourseMapper {

    private final StorageService storageService;

    public CourseMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public CourseSummaryResponse toSummary(Course c, boolean bestSeller, Boolean saved, Boolean owned) {
        return CourseSummaryResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .slug(c.getSlug())
                .thumbnailUrl(storageService.resolveUrl(c.getThumbnailUrl()))
                .instructor(toInstructorBrief(c.getInstructor()))
                .category(toCategoryBrief(c.getCategory()))
                .level(c.getLevel())
                .originalPrice(c.getOriginalPrice())
                .salePrice(c.getSalePrice())
                .discountPercent(discountPercent(c.getOriginalPrice(), c.getSalePrice()))
                .avgRating(c.getAvgRating())
                .reviewCount(c.getReviewCount())
                .studentCount(c.getStudentCount())
                .totalLessons(c.getTotalLessons())
                .totalDurationSeconds(c.getTotalDurationSeconds())
                .bestSeller(bestSeller)
                .status(c.getStatus())
                .saved(saved)
                .owned(owned)
                .build();
    }

    public InstructorBrief toInstructorBrief(User u) {
        return new InstructorBrief(u.getId(), u.getFullName(), storageService.resolveUrl(u.getAvatarUrl()),
                u.getJobTitle());
    }

    public CategoryBrief toCategoryBrief(Category c) {
        return new CategoryBrief(c.getId(), c.getName(), c.getSlug());
    }

    public TopicBrief toTopicBrief(Topic t) {
        return new TopicBrief(t.getId(), t.getName(), t.getSlug());
    }

    public BenefitResponse toBenefit(CourseBenefit b) {
        return new BenefitResponse(b.getId(), b.getContent(), b.getIcon(), b.getSortOrder());
    }

    /** % giảm giá so với giá gốc, làm tròn số nguyên. */
    public static int discountPercent(BigDecimal original, BigDecimal sale) {
        if (original == null || sale == null || original.signum() <= 0 || sale.compareTo(original) >= 0) {
            return 0;
        }
        return original.subtract(sale).multiply(BigDecimal.valueOf(100))
                .divide(original, 0, RoundingMode.HALF_UP).intValue();
    }
}
