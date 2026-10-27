package iuh.fit.elearning.course.entity;

import iuh.fit.elearning.common.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Tài liệu đính kèm của khóa học (TaiLieu).
 */
@Entity
@Table(name = "course_documents")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CourseDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;
}
