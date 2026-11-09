package iuh.fit.elearning.auth.otp;

/** Mục đích của mã OTP; là một phần của khóa lưu trữ otp:<purpose>:<email>. */
public enum OtpPurpose {
    REGISTER,
    RESET_PASSWORD
}
