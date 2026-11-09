package iuh.fit.elearning.auth.service;

import iuh.fit.elearning.auth.dto.request.ForgotPasswordRequest;
import iuh.fit.elearning.auth.dto.request.LoginRequest;
import iuh.fit.elearning.auth.dto.request.RefreshTokenRequest;
import iuh.fit.elearning.auth.dto.request.RegisterRequest;
import iuh.fit.elearning.auth.dto.request.ResendOtpRequest;
import iuh.fit.elearning.auth.dto.request.ResetPasswordRequest;
import iuh.fit.elearning.auth.dto.request.VerifyOtpRequest;
import iuh.fit.elearning.auth.dto.response.AuthResponse;
import iuh.fit.elearning.auth.dto.response.OtpSentResponse;
import iuh.fit.elearning.auth.otp.OtpPurpose;
import iuh.fit.elearning.auth.otp.OtpService;
import iuh.fit.elearning.auth.security.JwtService;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.user.entity.RefreshToken;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.mapper.UserMapper;
import iuh.fit.elearning.user.repository.UserRepository;
import iuh.fit.elearning.user.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * UC001 (đăng ký + OTP), UC002 (đăng nhập, làm mới token, quên mật khẩu), UC003 (đăng xuất).
 */
@Slf4j
@Service
public class AuthService {

    /** Sai mật khẩu liên tiếp tối đa trước khi khóa đăng nhập tạm thời (UC002). */
    private static final int MAX_FAILED_LOGINS = 5;
    private static final Duration LOGIN_LOCK_DURATION = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, OtpService otpService,
                       JwtService jwtService, SessionService sessionService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userMapper = userMapper;
    }

    // ------------------------------------------------------------------ UC001

    /**
     * Tạo tài khoản PENDING và gửi OTP. Gửi email lỗi => rollback, không để lại tài khoản.
     * Email đang PENDING (chưa xác thực) được phép đăng ký lại để cập nhật thông tin và nhận OTP mới.
     */
    @Transactional
    public OtpSentResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (request.confirmPassword() != null && !request.confirmPassword().equals(request.password())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Mật khẩu xác nhận không khớp");
        }
        if (Boolean.FALSE.equals(request.acceptedTerms())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Bạn cần đồng ý với điều khoản sử dụng");
        }

        Optional<User> existing = userRepository.findByEmail(email);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
            if (user.getStatus() != UserStatus.PENDING) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS);
            }
            user.setFullName(request.fullName().trim());
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        } else {
            user = User.builder()
                    .fullName(request.fullName().trim())
                    .email(email)
                    .passwordHash(passwordEncoder.encode(request.password()))
                    .role(UserRole.STUDENT)
                    .status(UserStatus.PENDING)
                    .build();
        }
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        otpService.issue(OtpPurpose.REGISTER, email, user.getFullName());
        return sentResponse(email);
    }

    /** Xác thực OTP đăng ký: PENDING -> ACTIVE. */
    @Transactional
    public void verifyRegistration(VerifyOtpRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .filter(u -> u.getStatus() == UserStatus.PENDING)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_EXPIRED,
                        "Mã OTP đã hết hạn hoặc không hợp lệ, vui lòng yêu cầu mã mới"));
        otpService.verify(OtpPurpose.REGISTER, email, request.otp());
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerifiedAt(Instant.now());
    }

    /**
     * Gửi lại OTP. Luôn trả phản hồi giống nhau để không lộ email có tồn tại hay không.
     */
    @Transactional
    public OtpSentResponse resendOtp(ResendOtpRequest request) {
        String email = normalizeEmail(request.email());
        OtpPurpose purpose = request.purpose() == null ? OtpPurpose.REGISTER : request.purpose();
        UserStatus required = purpose == OtpPurpose.REGISTER ? UserStatus.PENDING : UserStatus.ACTIVE;
        userRepository.findByEmail(email)
                .filter(u -> u.getStatus() == required)
                .ifPresent(u -> otpService.issue(purpose, email, u.getFullName()));
        return sentResponse(email);
    }

    // ------------------------------------------------------------------ UC002

    /**
     * Đăng nhập. noRollbackFor để bộ đếm đăng nhập sai vẫn được lưu khi ném BusinessException.
     */
    @Transactional(noRollbackFor = BusinessException.class)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        Instant now = Instant.now();

        if (user.getLoginLockedUntil() != null && user.getLoginLockedUntil().isAfter(now)) {
            long minutes = Math.max(1, Duration.between(now, user.getLoginLockedUntil()).toMinutes() + 1);
            throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS,
                    "Tài khoản tạm khóa đăng nhập do nhập sai nhiều lần, vui lòng thử lại sau " + minutes + " phút");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            int failed = user.getFailedLoginCount() + 1;
            if (failed >= MAX_FAILED_LOGINS) {
                user.setFailedLoginCount(0);
                user.setLoginLockedUntil(now.plus(LOGIN_LOCK_DURATION));
                userRepository.save(user);
                throw new BusinessException(ErrorCode.TOO_MANY_ATTEMPTS,
                        "Bạn đã nhập sai mật khẩu " + MAX_FAILED_LOGINS + " lần, đăng nhập bị khóa tạm thời "
                                + LOGIN_LOCK_DURATION.toMinutes() + " phút");
            }
            user.setFailedLoginCount(failed);
            userRepository.save(user);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() == UserStatus.LOCKED || user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (user.getStatus() == UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_VERIFIED,
                    "Tài khoản chưa xác thực, vui lòng nhập mã OTP đã gửi tới email (hoặc yêu cầu gửi lại mã)");
        }

        user.setFailedLoginCount(0);
        user.setLoginLockedUntil(null);
        user.setLastLoginAt(now);
        userRepository.save(user);
        return issueTokens(user, request.fcmToken(), request.deviceInfo());
    }

    /**
     * Đổi refresh token lấy cặp token mới (xoay vòng). Dùng lại token đã thu hồi => thu hồi toàn bộ phiên của tài khoản.
     */
    @Transactional(noRollbackFor = BusinessException.class)
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = sessionService.findByRaw(request.refreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh token không hợp lệ"));
        User user = token.getUser();
        if (token.getRevokedAt() != null) {
            log.warn("Phát hiện dùng lại refresh token đã thu hồi của user {}", user.getId());
            sessionService.revokeAll(user.getId());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        String fcmToken = token.getFcmToken();
        String deviceInfo = token.getDeviceInfo();
        sessionService.revoke(token);
        // Giữ lại FCM token cho phiên mới (revoke() đã xóa fcm của token cũ)
        return issueTokens(user, fcmToken, deviceInfo);
    }

    /** Quên mật khẩu: gửi OTP nếu tài khoản tồn tại và ACTIVE; luôn trả cùng một phản hồi. */
    @Transactional
    public OtpSentResponse forgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.email());
        userRepository.findByEmail(email)
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .ifPresent(u -> otpService.issue(OtpPurpose.RESET_PASSWORD, email, u.getFullName()));
        return sentResponse(email);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_EXPIRED,
                        "Mã OTP đã hết hạn hoặc không hợp lệ, vui lòng yêu cầu mã mới"));
        otpService.verify(OtpPurpose.RESET_PASSWORD, email, request.otp());
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setFailedLoginCount(0);
        user.setLoginLockedUntil(null);
        sessionService.revokeAll(user.getId());
    }

    // ------------------------------------------------------------------ UC003

    /** Đăng xuất: thu hồi refresh token hiện tại và gỡ FCM token của thiết bị. Token không hợp lệ thì bỏ qua. */
    @Transactional
    public void logout(UUID userId, String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        sessionService.findByRaw(rawRefreshToken)
                .filter(t -> t.getUser().getId().equals(userId))
                .ifPresent(sessionService::revoke);
    }

    // ------------------------------------------------------------------ helpers

    private AuthResponse issueTokens(User user, String fcmToken, String deviceInfo) {
        String access = jwtService.generateAccessToken(user);
        String refresh = sessionService.createRefreshToken(user, fcmToken, deviceInfo);
        return new AuthResponse(access, refresh, "Bearer", jwtService.getAccessTokenTtlSeconds(),
                userMapper.toSummary(user));
    }

    private OtpSentResponse sentResponse(String email) {
        return new OtpSentResponse(email, otpService.ttlSeconds(), otpService.resendCooldownSeconds());
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
