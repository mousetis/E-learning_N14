package iuh.fit.elearning.user.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.dto.response.AdminUserResponse;
import iuh.fit.elearning.user.entity.InstructorProfile;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** UC023: quản trị viên quản lý người dùng. Không thao tác lên chính mình. */
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final CourseRepository courseRepository;
    private final SessionService sessionService;
    private final StorageService storageService;

    public AdminUserService(UserRepository userRepository, InstructorProfileRepository instructorProfileRepository,
                            CourseRepository courseRepository, SessionService sessionService,
                            StorageService storageService) {
        this.userRepository = userRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.courseRepository = courseRepository;
        this.sessionService = sessionService;
        this.storageService = storageService;
    }

    /** Lọc theo vai trò, trạng thái, tìm theo tên / email. Mặc định không hiển thị tài khoản đã xóa mềm. */
    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> list(UserRole role, UserStatus status, String keyword, int page, int size) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(cb.notEqual(root.get("status"), UserStatus.DELETED));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<User> result = userRepository.findAll(spec,
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.of(result, this::toResponse);
    }

    @Transactional
    public AdminUserResponse lock(UUID actorId, UUID userId) {
        User user = requireTarget(actorId, userId);
        if (user.getStatus() == UserStatus.LOCKED) {
            return toResponse(user);
        }
        user.setStatus(UserStatus.LOCKED);
        sessionService.revokeAll(userId);
        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse unlock(UUID actorId, UUID userId) {
        User user = requireTarget(actorId, userId);
        if (user.getStatus() != UserStatus.LOCKED) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Tài khoản không ở trạng thái bị khóa");
        }
        user.setStatus(user.getEmailVerifiedAt() != null ? UserStatus.ACTIVE : UserStatus.PENDING);
        user.setFailedLoginCount(0);
        user.setLoginLockedUntil(null);
        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse changeRole(UUID actorId, UUID userId, UserRole role) {
        User user = requireTarget(actorId, userId);
        if (user.getRole() == role) {
            return toResponse(user);
        }
        if (user.getRole() == UserRole.INSTRUCTOR
                && courseRepository.existsByInstructorIdAndStatus(userId, CourseStatus.PUBLISHED)) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    "Giảng viên còn khóa học đang xuất bản, hãy ngừng xuất bản trước khi hạ quyền");
        }
        user.setRole(role);
        if (role == UserRole.INSTRUCTOR && instructorProfileRepository.findByUserId(userId).isEmpty()) {
            instructorProfileRepository.save(InstructorProfile.builder().user(user).build());
        }
        return toResponse(user);
    }

    /** Xóa mềm: giữ dữ liệu liên quan (đơn hàng, ghi danh...), thu hồi mọi phiên đăng nhập. */
    @Transactional
    public void delete(UUID actorId, UUID userId) {
        User user = requireTarget(actorId, userId);
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(Instant.now());
        sessionService.revokeAll(userId);
    }

    private User requireTarget(UUID actorId, UUID userId) {
        if (actorId.equals(userId)) {
            throw BusinessException.badRequest("Bạn không thể thao tác lên chính tài khoản của mình");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy người dùng"));
        if (user.getStatus() == UserStatus.DELETED) {
            throw BusinessException.notFound("Không tìm thấy người dùng");
        }
        return user;
    }

    private AdminUserResponse toResponse(User u) {
        return new AdminUserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getStatus(),
                storageService.resolveUrl(u.getAvatarUrl()), u.getJobTitle(), u.getCreatedAt(), u.getLastLoginAt());
    }
}
