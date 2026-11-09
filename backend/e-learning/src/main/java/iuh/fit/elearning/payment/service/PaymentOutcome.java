package iuh.fit.elearning.payment.service;

/** Kết quả xử lý một thông báo thanh toán (IPN / return). */
public enum PaymentOutcome {
    /** Thanh toán thành công và đã ghi danh. */
    CONFIRMED,
    /** Thanh toán thất bại hoặc bị hủy; đơn chuyển FAILED. */
    FAILED,
    /** Đơn đã được xử lý trước đó (IPN gửi lặp). */
    ALREADY_PROCESSED,
    ORDER_NOT_FOUND,
    AMOUNT_MISMATCH
}
