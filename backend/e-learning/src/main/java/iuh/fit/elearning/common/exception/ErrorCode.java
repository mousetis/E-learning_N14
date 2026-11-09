package iuh.fit.elearning.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ và HTTP status tương ứng (SDS bảng 6.1).
 */
public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    OTP_INVALID(HttpStatus.BAD_REQUEST, "Mã OTP không đúng"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn hoặc không tồn tại"),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "Chữ ký không hợp lệ"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác"),

    ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN, "Tài khoản chưa xác thực OTP"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    NOT_ENROLLED(HttpStatus.FORBIDDEN, "Bạn chưa ghi danh khóa học này"),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Phương thức không được hỗ trợ"),

    EMAIL_EXISTS(HttpStatus.CONFLICT, "Email đã được đăng ký"),
    ALREADY_OWNED(HttpStatus.CONFLICT, "Bạn đã sở hữu khóa học này"),
    ALREADY_IN_CART(HttpStatus.CONFLICT, "Khóa học đã có trong giỏ hàng"),
    CATEGORY_IN_USE(HttpStatus.CONFLICT, "Danh mục còn khóa học, không thể xóa"),
    COURSE_HAS_STUDENTS(HttpStatus.CONFLICT, "Khóa học đã có học viên, không thể xóa"),
    DUPLICATE(HttpStatus.CONFLICT, "Dữ liệu đã tồn tại"),

    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "Tệp vượt quá dung lượng cho phép"),
    UNSUPPORTED_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Định dạng tệp không được hỗ trợ"),

    COUPON_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "Mã giảm giá không hợp lệ"),
    COURSE_NOT_AVAILABLE(HttpStatus.UNPROCESSABLE_ENTITY, "Khóa học không còn khả dụng"),
    COURSE_NOT_READY(HttpStatus.UNPROCESSABLE_ENTITY, "Khóa học chưa đủ điều kiện"),
    REVIEW_NOT_ALLOWED(HttpStatus.UNPROCESSABLE_ENTITY, "Bạn cần đạt tiến độ tối thiểu 20% để đánh giá"),
    REFUND_NOT_ALLOWED(HttpStatus.UNPROCESSABLE_ENTITY, "Không đủ điều kiện hoàn tiền"),
    INVALID_STATE(HttpStatus.UNPROCESSABLE_ENTITY, "Trạng thái hiện tại không cho phép thao tác này"),

    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "Bạn thao tác quá nhiều lần, vui lòng thử lại sau"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau"),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "Cổng thanh toán đang gặp sự cố"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Dịch vụ tạm thời không khả dụng, vui lòng thử lại sau");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
