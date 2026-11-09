package iuh.fit.elearning.interaction.mapper;

import iuh.fit.elearning.interaction.dto.response.AuthorResponse;
import iuh.fit.elearning.interaction.dto.response.QaCommentResponse;
import iuh.fit.elearning.interaction.dto.response.QaPostResponse;
import iuh.fit.elearning.interaction.dto.response.ReviewResponse;
import iuh.fit.elearning.interaction.dto.response.SubmissionResponse;
import iuh.fit.elearning.interaction.entity.ProjectSubmission;
import iuh.fit.elearning.interaction.entity.QaComment;
import iuh.fit.elearning.interaction.entity.QaPost;
import iuh.fit.elearning.interaction.entity.Review;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {

    private final StorageService storageService;

    public InteractionMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public AuthorResponse toAuthor(User u) {
        return new AuthorResponse(u.getId(), u.getFullName(), storageService.resolveUrl(u.getAvatarUrl()),
                u.getRole().name());
    }

    public ReviewResponse toReview(Review r) {
        return new ReviewResponse(r.getId(), r.getCourse().getId(), r.getCourse().getTitle(), toAuthor(r.getUser()),
                r.getRating(), r.getComment(), r.getCreatedAt(), r.getUpdatedAt());
    }

    public QaPostResponse toPost(QaPost p, Boolean liked) {
        return new QaPostResponse(p.getId(), p.getCourse().getId(), toAuthor(p.getUser()), p.getContent(),
                p.getLikeCount(), p.getCommentCount(), Boolean.TRUE.equals(p.getAnswered()),
                Boolean.TRUE.equals(p.getHidden()), liked, p.getCreatedAt());
    }

    public QaCommentResponse toComment(QaComment c) {
        return new QaCommentResponse(c.getId(), c.getPost().getId(), toAuthor(c.getUser()), c.getContent(),
                Boolean.TRUE.equals(c.getInstructorAnswer()), c.getCreatedAt());
    }

    /** Link xem / tải bài nộp có thời hạn (tệp riêng tư). */
    public SubmissionResponse toSubmission(ProjectSubmission s) {
        String url = s.getFileUrl() == null ? null
                : storageService.createSignedUrl(s.getFileUrl(), storageService.defaultSignedUrlTtl());
        return new SubmissionResponse(s.getId(), s.getCourse().getId(), toAuthor(s.getUser()), s.getTitle(),
                s.getDescription(), s.getFileName(), s.getFileType(), s.getFileSize(), url, s.getFeedback(),
                s.getFeedbackAt(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
