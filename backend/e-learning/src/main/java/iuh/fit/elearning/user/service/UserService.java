package iuh.fit.elearning.user.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.interaction.repository.SavedCourseRepository;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.dto.request.ChangePasswordRequest;
import iuh.fit.elearning.user.dto.request.UpdateProfileRequest;
import iuh.fit.elearning.user.dto.response.ProfileResponse;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** UC018: hồ sơ cá nhân và đổi mật khẩu. */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final SavedCourseRepository savedCourseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StorageService storageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SessionService sessionService,
                       SavedCourseRepository savedCourseRepository, EnrollmentRepository enrollmentRepository,
                       StorageService storageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.savedCourseRepository = savedCourseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public ProfileResponse me(UUID userId) {
        return toProfile(find(userId));
    }

    @Transactional
    public ProfileResponse update(UUID userId, UpdateProfileRequest request) {
        User user = find(userId);
        user.setFullName(request.fullName().trim());
        user.setJobTitle(blankToNull(request.jobTitle()));
        user.setBio(blankToNull(request.bio()));
        user.setAvatarUrl(storageService.requireImageRef(request.avatarUrl()));
        user.setCoverUrl(storageService.requireImageRef(request.coverUrl()));
        return toProfile(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = find(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu hiện tại không đúng");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw BusinessException.badRequest("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        sessionService.revokeOthers(userId, request.refreshToken());
    }

    private ProfileResponse toProfile(User u) {
        return new ProfileResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(),
                storageService.resolveUrl(u.getAvatarUrl()), storageService.resolveUrl(u.getCoverUrl()),
                u.getJobTitle(), u.getBio(), u.getCreatedAt(),
                savedCourseRepository.countByUserId(u.getId()),
                enrollmentRepository.countByUserIdAndStatus(u.getId(), EnrollmentStatus.ONGOING),
                enrollmentRepository.countByUserIdAndStatus(u.getId(), EnrollmentStatus.COMPLETED));
    }

    private User find(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
