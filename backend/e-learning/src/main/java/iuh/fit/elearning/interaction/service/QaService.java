package iuh.fit.elearning.interaction.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.interaction.dto.request.QaContentRequest;
import iuh.fit.elearning.interaction.dto.response.LikeResponse;
import iuh.fit.elearning.interaction.dto.response.QaCommentResponse;
import iuh.fit.elearning.interaction.dto.response.QaPostResponse;
import iuh.fit.elearning.interaction.entity.QaComment;
import iuh.fit.elearning.interaction.entity.QaPost;
import iuh.fit.elearning.interaction.entity.QaPostLike;
import iuh.fit.elearning.interaction.mapper.InteractionMapper;
import iuh.fit.elearning.interaction.repository.QaCommentRepository;
import iuh.fit.elearning.interaction.repository.QaPostLikeRepository;
import iuh.fit.elearning.interaction.repository.QaPostRepository;
import iuh.fit.elearning.learning.service.CourseAccessService;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** UC014 (học viên) và UC020 (giảng viên): hỏi đáp Q&A của khóa học. */
@Service
public class QaService {

    private final QaPostRepository postRepository;
    private final QaCommentRepository commentRepository;
    private final QaPostLikeRepository likeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseAccessService accessService;
    private final CourseService courseService;
    private final NotificationService notificationService;
    private final InteractionMapper mapper;

    public QaService(QaPostRepository postRepository, QaCommentRepository commentRepository,
                     QaPostLikeRepository likeRepository, CourseRepository courseRepository,
                     UserRepository userRepository, CourseAccessService accessService, CourseService courseService,
                     NotificationService notificationService, InteractionMapper mapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
        this.courseService = courseService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    // ------------------------------------------------------------------ học viên

    @Transactional(readOnly = true)
    public PageResponse<QaPostResponse> list(UUID courseId, int page, int size, AuthUser viewer) {
        courseService.requireViewable(courseId, viewer);
        Page<QaPost> result = postRepository
                .findByCourseIdAndHiddenFalseAndDeletedAtIsNullOrderByCreatedAtDesc(courseId, PageUtils.of(page, size));
        return PageResponse.of(result, toPostResponses(result.getContent(), AuthUser.idOf(viewer)));
    }

    @Transactional
    public QaPostResponse create(AuthUser user, UUID courseId, QaContentRequest request) {
        Course course = accessService.requireContentAccess(user, courseId);
        QaPost post = QaPost.builder()
                .course(course)
                .user(userRepository.getReferenceById(user.id()))
                .content(request.content().trim())
                .build();
        postRepository.saveAndFlush(post);
        UUID instructorId = course.getInstructor().getId();
        if (!instructorId.equals(user.id())) {
            notificationService.notifyUser(instructorId, NotificationType.QA_NEW_QUESTION,
                    "Câu hỏi mới trong khóa học", "Có câu hỏi mới trong khóa học \"" + course.getTitle() + "\"",
                    post.getId());
        }
        return mapper.toPost(post, false);
    }

    @Transactional
    public void delete(AuthUser user, UUID postId) {
        QaPost post = findPost(postId);
        if (!post.getUser().getId().equals(user.id())) {
            throw BusinessException.forbidden("Bạn chỉ được xóa câu hỏi của chính mình");
        }
        post.setDeletedAt(Instant.now());
    }

    @Transactional(readOnly = true)
    public List<QaCommentResponse> comments(UUID postId, AuthUser viewer) {
        QaPost post = findPost(postId);
        courseService.requireViewable(post.getCourse().getId(), viewer);
        if (Boolean.TRUE.equals(post.getHidden()) && !canModerate(viewer, post.getCourse())) {
            throw BusinessException.notFound("Không tìm thấy câu hỏi");
        }
        return commentRepository.findByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(postId).stream()
                .map(mapper::toComment).toList();
    }

    @Transactional
    public QaCommentResponse addComment(AuthUser user, UUID postId, QaContentRequest request) {
        QaPost post = findVisiblePost(postId);
        accessService.requireContentAccess(user, post.getCourse().getId());
        QaComment comment = QaComment.builder()
                .post(post)
                .user(userRepository.getReferenceById(user.id()))
                .content(request.content().trim())
                .instructorAnswer(false)
                .build();
        commentRepository.saveAndFlush(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        notifyAuthor(post, user, "Có bình luận mới cho câu hỏi của bạn");
        return mapper.toComment(comment);
    }

    /** Thả / bỏ tim (toggle). */
    @Transactional
    public LikeResponse toggleLike(AuthUser user, UUID postId) {
        QaPost post = findVisiblePost(postId);
        accessService.requireContentAccess(user, post.getCourse().getId());
        var existing = likeRepository.findByPostIdAndUserId(postId, user.id());
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            return new LikeResponse(false, post.getLikeCount());
        }
        likeRepository.save(QaPostLike.builder().post(post).user(userRepository.getReferenceById(user.id())).build());
        post.setLikeCount(post.getLikeCount() + 1);
        return new LikeResponse(true, post.getLikeCount());
    }

    // ------------------------------------------------------------------ giảng viên (UC020)

    @Transactional(readOnly = true)
    public PageResponse<QaPostResponse> listForInstructor(AuthUser user, UUID courseId, int page, int size) {
        requireOwnerOrAdmin(user, courseId);
        Page<QaPost> result = postRepository.findForInstructor(courseId, PageUtils.of(page, size));
        return PageResponse.of(result, toPostResponses(result.getContent(), user.id()));
    }

    @Transactional
    public QaCommentResponse answer(AuthUser user, UUID postId, QaContentRequest request) {
        QaPost post = findPost(postId);
        requireOwnerOrAdmin(user, post.getCourse().getId());
        QaComment answer = QaComment.builder()
                .post(post)
                .user(userRepository.getReferenceById(user.id()))
                .content(request.content().trim())
                .instructorAnswer(true)
                .build();
        commentRepository.saveAndFlush(answer);
        post.setCommentCount(post.getCommentCount() + 1);
        post.setAnswered(true);
        notifyAuthor(post, user, "Giảng viên đã trả lời câu hỏi của bạn");
        return mapper.toComment(answer);
    }

    @Transactional
    public void hide(AuthUser user, UUID postId) {
        QaPost post = findPost(postId);
        requireOwnerOrAdmin(user, post.getCourse().getId());
        post.setHidden(true);
    }

    // ------------------------------------------------------------------ nội bộ

    private List<QaPostResponse> toPostResponses(List<QaPost> posts, UUID viewerId) {
        if (posts.isEmpty()) {
            return List.of();
        }
        final Set<UUID> liked = viewerId == null ? Set.of()
                : new HashSet<>(likeRepository.findLikedPostIds(viewerId, posts.stream().map(QaPost::getId).toList()));
        return posts.stream()
                .map(p -> mapper.toPost(p, viewerId == null ? null : liked.contains(p.getId())))
                .toList();
    }

    private void notifyAuthor(QaPost post, AuthUser actor, String title) {
        UUID authorId = post.getUser().getId();
        if (!authorId.equals(actor.id())) {
            notificationService.notifyUser(authorId, NotificationType.QA_REPLY, title,
                    "Khóa học \"" + post.getCourse().getTitle() + "\"", post.getId());
        }
    }

    private QaPost findPost(UUID postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy câu hỏi"));
    }

    private QaPost findVisiblePost(UUID postId) {
        QaPost post = findPost(postId);
        if (Boolean.TRUE.equals(post.getHidden())) {
            throw BusinessException.notFound("Không tìm thấy câu hỏi");
        }
        return post;
    }

    private boolean canModerate(AuthUser viewer, Course course) {
        return viewer != null && (viewer.isAdmin() || course.getInstructor().getId().equals(viewer.id()));
    }

    private void requireOwnerOrAdmin(AuthUser user, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        if (!canModerate(user, course)) {
            throw BusinessException.forbidden("Bạn không phải giảng viên của khóa học này");
        }
    }
}
