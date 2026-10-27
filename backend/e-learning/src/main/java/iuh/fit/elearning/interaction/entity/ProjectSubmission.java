package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Bài nộp dự án của học viên (BaiNopDuAn) – mỗi học viên một bài cho mỗi khóa học.
 */
@Entity
@Table(name = "project_submissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ProjectSubmission extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "feedback_at")
    private Instant feedbackAt;
}
