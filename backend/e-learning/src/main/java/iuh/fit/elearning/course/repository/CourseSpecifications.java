package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.catalog.entity.Topic;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

/**
 * Các điều kiện tìm kiếm khóa học ghép bằng JPA Specification (SDS 7.8.4).
 */
public final class CourseSpecifications {

    private static final int SHORT_MAX_SECONDS = 2 * 3600;
    private static final int MEDIUM_MAX_SECONDS = 10 * 3600;

    private CourseSpecifications() {
    }

    /** Chỉ khóa học PUBLISHED của giảng viên đang ACTIVE (khóa của giảng viên bị khóa bị ẩn khỏi tìm kiếm). */
    public static Specification<Course> publiclyVisible() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), CourseStatus.PUBLISHED),
                cb.equal(root.get("instructor").get("status"), UserStatus.ACTIVE));
    }

    /** Khớp không phân biệt hoa thường trên tiêu đề, mô tả, tên giảng viên, tên chủ đề. */
    public static Specification<Course> keyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + escapeLike(keyword.trim().toLowerCase(Locale.ROOT)) + "%";
            Join<Course, Topic> topics = root.join("topics", JoinType.LEFT);
            query.distinct(true);
            return cb.or(
                    cb.like(cb.lower(root.<String>get("title")), pattern, '\\'),
                    cb.like(cb.lower(root.<String>get("description")), pattern, '\\'),
                    cb.like(cb.lower(root.get("instructor").<String>get("fullName")), pattern, '\\'),
                    cb.like(cb.lower(topics.<String>get("name")), pattern, '\\'));
        };
    }

    public static Specification<Course> category(UUID categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Course> minPrice(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("salePrice"), min);
    }

    public static Specification<Course> maxPrice(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("salePrice"), max);
    }

    public static Specification<Course> minRating(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("avgRating"), min);
    }

    public static Specification<Course> level(CourseLevel level) {
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }

    /** SHORT: dưới 2 giờ; MEDIUM: 2-10 giờ; LONG: trên 10 giờ. */
    public static Specification<Course> duration(String duration) {
        return (root, query, cb) -> {
            switch (duration.toUpperCase(Locale.ROOT)) {
                case "SHORT":
                    return cb.lessThan(root.<Integer>get("totalDurationSeconds"), SHORT_MAX_SECONDS);
                case "MEDIUM":
                    return cb.and(
                            cb.greaterThanOrEqualTo(root.<Integer>get("totalDurationSeconds"), SHORT_MAX_SECONDS),
                            cb.lessThanOrEqualTo(root.<Integer>get("totalDurationSeconds"), MEDIUM_MAX_SECONDS));
                case "LONG":
                    return cb.greaterThan(root.<Integer>get("totalDurationSeconds"), MEDIUM_MAX_SECONDS);
                default:
                    return cb.conjunction();
            }
        };
    }

    /** popular (mặc định) | newest | rating | price_asc | price_desc. */
    public static Sort sortOf(String sort) {
        String key = sort == null ? "popular" : sort.trim().toLowerCase(Locale.ROOT);
        return switch (key) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "publishedAt", "createdAt");
            case "rating" -> Sort.by(Sort.Direction.DESC, "avgRating", "reviewCount");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "salePrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "salePrice");
            default -> Sort.by(Sort.Direction.DESC, "studentCount", "avgRating");
        };
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
