package iuh.fit.elearning.payment.service;

import iuh.fit.elearning.cart.repository.CartItemRepository;
import iuh.fit.elearning.learning.service.EnrollmentService;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.OrderItem;
import iuh.fit.elearning.order.entity.enums.OrderStatus;
import iuh.fit.elearning.order.repository.OrderRepository;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentStatus;
import iuh.fit.elearning.payment.repository.PaymentRepository;
import iuh.fit.elearning.promotion.entity.CouponUsage;
import iuh.fit.elearning.promotion.repository.CouponRepository;
import iuh.fit.elearning.promotion.repository.CouponUsageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * UC010 / SDS 7.3: xử lý kết quả thanh toán. Mọi thay đổi (payments, orders, enrollments, coupon_usages, giỏ hàng)
 * nằm trong một transaction; dòng đơn hàng được khóa nên IPN gửi trùng không ghi danh hai lần.
 */
@Slf4j
@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EnrollmentService enrollmentService;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CartItemRepository cartItemRepository;
    private final NotificationService notificationService;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository,
                          EnrollmentService enrollmentService, CouponRepository couponRepository,
                          CouponUsageRepository couponUsageRepository, CartItemRepository cartItemRepository,
                          NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.enrollmentService = enrollmentService;
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.cartItemRepository = cartItemRepository;
        this.notificationService = notificationService;
    }

    /**
     * @param paidAmount số tiền cổng báo về (null nếu cổng không trả, vd MOCK) - phải khớp tổng đơn
     * @param raw        dữ liệu gốc từ cổng, lưu vào payments.raw_response
     */
    @Transactional
    public PaymentOutcome confirm(String orderCode, boolean success, String gatewayTxnCode, BigDecimal paidAmount,
                                  String raw) {
        Order order = orderRepository.findByOrderCodeForUpdate(orderCode).orElse(null);
        if (order == null) {
            return PaymentOutcome.ORDER_NOT_FOUND;
        }
        if (order.getStatus() == OrderStatus.PAID) {
            return PaymentOutcome.ALREADY_PROCESSED;
        }
        if (paidAmount != null && paidAmount.compareTo(order.getTotalAmount()) != 0) {
            log.warn("Số tiền thanh toán không khớp đơn {}: nhận {} - cần {}", orderCode, paidAmount,
                    order.getTotalAmount());
            return PaymentOutcome.AMOUNT_MISMATCH;
        }
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId()).orElse(null);
        if (payment == null) {
            return PaymentOutcome.ORDER_NOT_FOUND;
        }

        if (success) {
            // Đơn hết hạn (CANCELLED) mà cổng vẫn thu tiền: vẫn ghi danh để không mất quyền lợi của học viên
            if (order.getStatus() == OrderStatus.FAILED) {
                return PaymentOutcome.ALREADY_PROCESSED;
            }
            fulfill(order, payment, gatewayTxnCode, raw);
            return PaymentOutcome.CONFIRMED;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            return PaymentOutcome.ALREADY_PROCESSED;
        }
        order.setStatus(OrderStatus.FAILED);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setRawResponse(raw);
        notificationService.notifyUser(order.getUser().getId(), NotificationType.PAYMENT, "Thanh toán không thành công",
                "Đơn hàng " + order.getOrderCode() + " chưa được thanh toán. Bạn có thể thử lại.", order.getId());
        return PaymentOutcome.FAILED;
    }

    /** Đánh dấu đơn đã thanh toán, ghi danh, ghi nhận mã giảm giá, dọn giỏ hàng, gửi thông báo. */
    @Transactional
    public void fulfill(Order order, Payment payment, String txnCode, String raw) {
        Instant now = Instant.now();
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(now);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(now);
        payment.setTransactionCode(txnCode);
        payment.setRawResponse(raw);

        List<UUID> courseIds = order.getItems().stream().map(i -> i.getCourse().getId()).toList();
        for (OrderItem item : order.getItems()) {
            enrollmentService.enroll(order.getUser(), item.getCourse(), item);
        }
        if (order.getCoupon() != null) {
            if (couponRepository.tryIncrementUsage(order.getCoupon().getId()) == 0) {
                log.warn("Mã {} đã hết lượt khi đơn {} thanh toán xong; vẫn áp dụng giảm giá đã tính",
                        order.getCoupon().getCode(), order.getOrderCode());
            }
            couponUsageRepository.save(CouponUsage.builder()
                    .coupon(order.getCoupon())
                    .user(order.getUser())
                    .order(order)
                    .discountAmount(order.getDiscountAmount())
                    .build());
        }
        if (!courseIds.isEmpty()) {
            cartItemRepository.deleteByUserAndCourses(order.getUser().getId(), courseIds);
        }
        notificationService.notifyUser(order.getUser().getId(), NotificationType.PAYMENT, "Thanh toán thành công",
                "Đơn hàng " + order.getOrderCode() + " đã thanh toán, bạn có thể bắt đầu học ngay.", order.getId());
    }
}
