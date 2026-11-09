package iuh.fit.elearning.interaction.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.CourseDocument;
import iuh.fit.elearning.course.entity.CourseProject;
import iuh.fit.elearning.course.repository.CourseDocumentRepository;
import iuh.fit.elearning.course.repository.CourseProjectRepository;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.interaction.dto.request.FeedbackRequest;
import iuh.fit.elearning.interaction.dto.response.DocumentResponse;
import iuh.fit.elearning.interaction.dto.response.DownloadResponse;
import iuh.fit.elearning.interaction.dto.response.ProjectResponse;
import iuh.fit.elearning.interaction.dto.response.SubmissionResponse;
import iuh.fit.elearning.interaction.entity.ProjectSubmission;
import iuh.fit.elearning.interaction.mapper.InteractionMapper;
import iuh.fit.elearning.interaction.repository.ProjectSubmissionRepository;
import iuh.fit.elearning.learning.service.CourseAccessService;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.storage.dto.StoredFile;
import iuh.fit.elearning.storage.service.FileType;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** UC013 (học viên: dự án, tài liệu) và phần nhận xét bài dự án của UC020 (giảng viên). */
@Service
public class ProjectService {

    private final CourseProjectRepository projectRepository;
    private final CourseDocumentRepository documentRepository;
    private final ProjectSubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseAccessService accessService;
    private final StorageService storageService;
    private final NotificationService notificationService;
    private final InteractionMapper mapper;

    public ProjectService(CourseProjectRepository projectRepository, CourseDocumentRepository documentRepository,
                          ProjectSubmissionRepository submissionRepository, CourseRepository courseRepository,
                          UserRepository userRepository, CourseAccessService accessService,
                          StorageService storageService, NotificationService notificationService,
                          InteractionMapper mapper) {
        this.projectRepository = projectRepository;
        this.documentRepository = documentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
        this.storageService = storageService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ProjectResponse project(AuthUser user, UUID courseId) {
        accessService.requireContentAccess(user, courseId);
        String description = projectRepository.findByCourseId(courseId).map(CourseProject::getDescription).orElse(null);
        List<DocumentResponse> documents = documentRepository.findByCourseId(courseId).stream()
                .map(d -> new DocumentResponse(d.getId(), d.getFileName(), d.getFileType(), d.getFileSize()))
                .toList();
        return new ProjectResponse(courseId, description, documents);
    }

    @Transactional(readOnly = true)
    public DownloadResponse download(AuthUser user, UUID documentId) {
        CourseDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy tài liệu"));
        accessService.requireContentAccess(user, doc.getCourse().getId());
        Duration ttl = storageService.defaultSignedUrlTtl();
        return new DownloadResponse(doc.getFileName(), storageService.createSignedUrl(doc.getFileUrl(), ttl),
                Instant.now().plus(ttl));
    }

    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> submissions(AuthUser user, UUID courseId, int page, int size) {
        accessService.requireContentAccess(user, courseId);
        Page<ProjectSubmission> result = submissionRepository
                .findByCourseIdOrderByCreatedAtDesc(courseId, PageUtils.of(page, size));
        return PageResponse.of(result, mapper::toSubmission);
    }

    /** Mỗi học viên một bài nộp cho mỗi khóa học: nộp lại sẽ thay thế bài cũ. */
    @Transactional
    public SubmissionResponse submit(AuthUser user, UUID courseId, String title, String description,
                                     MultipartFile file) {
        if (title == null || title.isBlank() || title.length() > 150) {
            throw BusinessException.badRequest("Tên dự án là bắt buộc, tối đa 150 ký tự");
        }
        if (description != null && description.length() > 2000) {
            throw BusinessException.badRequest("Mô tả dự án tối đa 2000 ký tự");
        }
        var enrollment = accessService.requireEnrollment(user.id(), courseId);
        StoredFile stored = storageService.upload(file, FileType.PROJECT);

        ProjectSubmission submission = submissionRepository.findByCourseIdAndUserId(courseId, user.id()).orElse(null);
        String oldKey = null;
        if (submission == null) {
            submission = ProjectSubmission.builder()
                    .course(enrollment.getCourse())
                    .user(userRepository.getReferenceById(user.id()))
                    .build();
        } else {
            oldKey = submission.getFileUrl();
        }
        submission.setTitle(title.trim());
        submission.setDescription(description == null || description.isBlank() ? null : description.trim());
        submission.setFileUrl(stored.key());
        submission.setFileName(stored.fileName());
        submission.setFileType(stored.fileType());
        submission.setFileSize(stored.size());
        submission.setFeedback(null);
        submission.setFeedbackAt(null);
        submissionRepository.saveAndFlush(submission);
        if (oldKey != null) {
            storageService.delete(oldKey);
        }
        return mapper.toSubmission(submission);
    }

    /** UC020: giảng viên nhận xét bài dự án của học viên. */
    @Transactional
    public SubmissionResponse feedback(AuthUser user, UUID submissionId, FeedbackRequest request) {
        ProjectSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy bài nộp"));
        Course course = courseRepository.findById(submission.getCourse().getId())
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        if (!user.isAdmin() && !course.getInstructor().getId().equals(user.id())) {
            throw BusinessException.forbidden("Bạn không phải giảng viên của khóa học này");
        }
        submission.setFeedback(request.feedback().trim());
        submission.setFeedbackAt(Instant.now());
        notificationService.notifyUser(submission.getUser().getId(), NotificationType.PROJECT_FEEDBACK,
                "Giảng viên đã nhận xét dự án của bạn", "Khóa học \"" + course.getTitle() + "\"", submission.getId());
        return mapper.toSubmission(submission);
    }
}
