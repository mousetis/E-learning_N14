package iuh.fit.elearning.learning.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.learning.dto.response.RefundResponse;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.RefundRequest;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.entity.enums.RefundStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.learning.repository.RefundRequestRepository;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.OrderItem;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import iuh.fit.elearning.payment.entity.enums.PaymentStatus;
import iuh.fit.elearning.payment.gateway.PaymentGateway;
import iuh.fit.elearning.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** UC012 / SDS 7.5, 7.8.3: yêu cầu hoàn tiền trong 7 ngày khi tiến độ dưới 20%. */
@Service
public class RefundService {

    public static final Duration REFUND_WINDOW = Duration.ofDays(7);
    public static final BigDecimal MAX_PROGRESS = BigDecimal.valueOf(20);

    private final EnrollmentRepository enrollmentRepository;
    private final RefundRequestRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;
    private final List<PaymentGateway> gateways;

    public RefundService(EnrollmentRepository enrollmentRepository, RefundRequestRepository refundRepository,
                         PaymentRepository paymentRepository, EnrollmentService enrollmentService,
                         NotificationService notificationService, List<PaymentGateway> gateways) {
        this.enrollmentRepository = enrollmentRepository;
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.enrollmentService = enrollmentService;
        this.notificationService = notificationService;
        this.gateways = gateways;
    }

    /** Lý do không đủ điều kiện hoàn tiền; null nếu đủ điều kiện. */
    @Transactional(readOnly = true)
    public String ineligibleReason(Enrollment e) {
        if (e.getStatus() != EnrollmentStatus.ONGOING) {
            return "Chỉ có thể hoàn tiền khi khóa học đang học (chưa hoàn thành hoặc đã hoàn tiền)";
        }
        OrderItem item = e.getOrderItem();
        if (item == null || item.getPrice().signum() == 0) {
            return "Khóa học miễn phí không áp dụng hoàn tiền";
        }
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(item.getOrder().getId()).orElse(null);
        if (payment == null || payment.getStatus() != PaymentStatus.PAID || payment.getMethod() == PaymentMethod.FREE) {
            return "Khóa học này không có giao dịch thanh toán để hoàn tiền";
        }
        if (Instant.now().isAfter(e.getEnrolledAt().plus(REFUND_WINDOW))) {
            return "Đã quá 7 ngày kể từ ngày ghi danh";
        }
        if (e.getProgressPercentage().compareTo(MAX_PROGRESS) >= 0) {
            return "Tiến độ học đã đạt 20% trở lên";
        }
        boolean existing = refundRepository.findByEnrollmentId(e.getId()).stream()
                .anyMatch(r -> r.getStatus() != RefundStatus.REJECTED && !r.getCreatedAt().isBefore(e.getEnrolledAt()));
        if (existing) {
            return "Đã có yêu cầu hoàn tiền cho khóa học này";
        }
        if (refundAmount(item).signum() <= 0) {
            return "Khóa học này không phát sinh số tiền thanh toán";
        }
        return null;
    }

    /** Số tiền hoàn = giá trong đơn trừ phần giảm giá phân bổ theo tỷ lệ giá (làm tròn đến đồng). */
    public static BigDecimal refundAmount(OrderItem item) {
        Order order = item.getOrder();
        BigDecimal share = BigDecimal.ZERO;
        if (order.getSubtotal() != null && order.getSubtotal().signum() > 0 && order.getDiscountAmount() != null) {
            share = order.getDiscountAmount().multiply(item.getPrice())
                    .divide(order.getSubtotal(), 0, RoundingMode.HALF_UP);
        }
        return item.getPrice().subtract(share).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Mặc định rollback; BusinessException không rollback để bản ghi yêu cầu bị từ chối (REJECTED) vẫn được lưu.
     * Lệnh gọi cổng chạy trước khi ghi bất cứ thứ gì nên lỗi mạng không để lại dữ liệu dở dang.
     */
    @Transactional(noRollbackFor = BusinessException.class)
    public RefundResponse request(AuthUser user, UUID enrollmentId, String reason, String clientIp) {
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentId)
                .filter(e -> e.getUser().getId().equals(user.id()))
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy ghi danh"));
        String problem = ineligibleReason(enrollment);
        if (problem != null) {
            throw new BusinessException(ErrorCode.REFUND_NOT_ALLOWED, problem);
        }
        OrderItem item = enrollment.getOrderItem();
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(item.getOrder().getId()).orElseThrow();
        BigDecimal amount = refundAmount(item);

        PaymentGateway gateway = gateways.stream().filter(g -> g.method() == payment.getMethod()).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_NOT_ALLOWED,
                        "Phương thức thanh toán này chưa hỗ trợ hoàn tiền tự động"));
        PaymentGateway.RefundResult result = gateway.refund(payment, amount, reason, clientIp);

        RefundRequest refund = RefundRequest.builder()
                .enrollment(enrollment)
                .payment(payment)
                .amount(amount)
                .reason(reason.trim())
                .build();
        if (!result.success()) {
            refund.setStatus(RefundStatus.REJECTED);
            refund.setRejectReason(result.message());
            refund.setProcessedAt(Instant.now());
            refundRepository.saveAndFlush(refund);
            throw new BusinessException(ErrorCode.REFUND_NOT_ALLOWED, result.message());
        }

        refund.setStatus(RefundStatus.COMPLETED);
        refund.setProcessedAt(Instant.now());
        refundRepository.saveAndFlush(refund);

        enrollment.setStatus(EnrollmentStatus.REFUNDED);
        enrollmentService.bumpStudentCounts(enrollment.getCourse(), -1);
        if (amount.compareTo(payment.getAmount()) >= 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
        notificationService.notifyUser(user.id(), NotificationType.REFUND, "Hoàn tiền thành công",
                "Đã hoàn " + amount.toBigInteger() + "đ cho khóa học \"" + enrollment.getCourse().getTitle() + "\"",
                refund.getId());
        return toResponse(refund);
    }

    private static RefundResponse toResponse(RefundRequest r) {
        return new RefundResponse(r.getId(), r.getEnrollment().getId(), r.getStatus(), r.getAmount(), r.getReason(),
                r.getRejectReason(), r.getProcessedAt(), r.getCreatedAt());
    }
}
