package iuh.fit.elearning.course.entity;

import iuh.fit.elearning.common.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Lợi ích của khóa học (LoiIchKhoaHoc).
 */
@Entity
@Table(name = "course_benefits")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CourseBenefit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "icon", length = 100)
    private String icon;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
