package iuh.fit.elearning.order.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.CodeGenerator;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.config.PaymentProperties;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.learning.service.CourseAccessService;
import iuh.fit.elearning.order.dto.request.CreateOrderRequest;
import iuh.fit.elearning.order.dto.response.OrderItemResponse;
import iuh.fit.elearning.order.dto.response.OrderResponse;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.OrderItem;
import iuh.fit.elearning.order.entity.enums.OrderStatus;
import iuh.fit.elearning.order.repository.OrderRepository;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import iuh.fit.elearning.payment.entity.enums.PaymentStatus;
import iuh.fit.elearning.payment.gateway.PaymentGateway;
import iuh.fit.elearning.payment.repository.PaymentRepository;
import iuh.fit.elearning.payment.service.PaymentService;
import iuh.fit.elearning.promotion.service.PromotionService;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** UC010: tính tiền, tạo đơn hàng, tra cứu và tự hủy đơn quá hạn. */
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseAccessService accessService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;
    private final PaymentProperties paymentProperties;
    private final StorageService storageService;
    private final List<PaymentGateway> gateways;

    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository,
                        CourseRepository courseRepository, UserRepository userRepository,
                        CourseAccessService accessService, PromotionService promotionService,
                        PaymentService paymentService, PaymentProperties paymentProperties,
                        StorageService storageService, List<PaymentGateway> gateways) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
        this.paymentProperties = paymentProperties;
        this.storageService = storageService;
        this.gateways = gateways;
    }

    /**
     * SDS 7.8.1: subtotal = tổng giá bán hiện hành của các khóa hợp lệ (PUBLISHED, chưa sở hữu); không tin số tiền
     * client gửi. Có mã thì kiểm tra và trừ giảm giá.
     */
    @Transactional(readOnly = true)
    public OrderQuote quote(UUID userId, List<UUID> courseIds, String couponCode) {
        List<UUID> ids = courseIds == null ? List.of() : courseIds.stream().distinct().toList();
        if (ids.isEmpty()) {
            throw BusinessException.badRequest("Danh sách khóa học không được để trống");
        }
        List<Course> courses = courseRepository.findAllById(ids);
        if (courses.size() != ids.size()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_AVAILABLE, "Có khóa học không tồn tại");
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Course course : courses) {
            if (!CourseService.isPubliclyVisible(course)) {
                throw new BusinessException(ErrorCode.COURSE_NOT_AVAILABLE,
                        "Khóa học \"" + course.getTitle() + "\" không còn khả dụng");
            }
            if (course.getInstructor().getId().equals(userId)) {
                throw BusinessException.badRequest("Bạn không thể mua khóa học của chính mình");
            }
            if (accessService.isEnrolled(userId, course.getId())) {
                throw new BusinessException(ErrorCode.ALREADY_OWNED,
                        "Bạn đã sở hữu khóa học \"" + course.getTitle() + "\"");
            }
            subtotal = subtotal.add(course.getSalePrice());
        }
        if (couponCode == null || couponCode.isBlank()) {
            return new OrderQuote(courses, subtotal, null, BigDecimal.ZERO, subtotal);
        }
        PromotionService.CouponApplication applied = promotionService.evaluate(couponCode, subtotal);
        return new OrderQuote(courses, subtotal, applied.coupon(), applied.discount(),
                subtotal.subtract(applied.discount()));
    }

    @Transactional
    public OrderResponse create(AuthUser user, CreateOrderRequest request, String clientIp) {
        OrderQuote quote = quote(user.id(), request.courseIds(), request.couponCode());
        boolean free = quote.total().signum() == 0;

        PaymentMethod method;
        if (free) {
            method = PaymentMethod.FREE;
        } else {
            method = request.method();
            if (method == null || method == PaymentMethod.FREE) {
                throw BusinessException.badRequest("Vui lòng chọn phương thức thanh toán (MOCK hoặc VNPAY)");
            }
            if (method == PaymentMethod.MOMO) {
                throw BusinessException.badRequest("Phương thức MOMO chưa được hỗ trợ");
            }
        }

        Order order = Order.builder()
                .orderCode(CodeGenerator.orderCode())
                .user(userRepository.getReferenceById(user.id()))
                .coupon(quote.coupon())
                .subtotal(quote.subtotal())
                .discountAmount(quote.discount())
                .totalAmount(quote.total())
                .status(OrderStatus.PENDING)
                .expiresAt(Instant.now().plus(paymentProperties.orderExpiryOrDefault()))
                .build();
        for (Course course : quote.courses()) {
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .course(course)
                    .courseTitle(course.getTitle())
                    .price(course.getSalePrice())
                    .build());
        }
        orderRepository.saveAndFlush(order);
        Payment payment = paymentRepository.save(Payment.builder()
                .order(order)
                .method(method)
                .amount(quote.total())
                .status(PaymentStatus.PENDING)
                .build());

        if (free) {
            paymentService.fulfill(order, payment, "FREE-" + order.getOrderCode(), null);
            return toResponse(order, method, null);
        }
        String paymentUrl = gatewayOf(method).createPaymentUrl(order, clientIp);
        return toResponse(order, method, paymentUrl);
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID userId, String orderCode) {
        Order order = orderRepository.findByOrderCodeAndUserId(orderCode, userId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy đơn hàng"));
        return toResponse(order, methodOf(order), null);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> list(UUID userId, int page, int size) {
        Page<Order> result = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, PageUtils.of(page, size));
        return PageResponse.of(result, o -> toResponse(o, methodOf(o), null));
    }

    /** Hủy các đơn PENDING quá hạn (mặc định 15 phút) mỗi phút. */
    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT1M")
    @Transactional
    public void expirePendingOrders() {
        List<Order> expired = orderRepository.findByStatusAndExpiresAtBefore(OrderStatus.PENDING, Instant.now());
        for (Order order : expired) {
            order.setStatus(OrderStatus.CANCELLED);
            paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId()).ifPresent(p -> {
                if (p.getStatus() == PaymentStatus.PENDING) {
                    p.setStatus(PaymentStatus.FAILED);
                }
            });
        }
        if (!expired.isEmpty()) {
            log.info("Đã hủy {} đơn hàng quá hạn thanh toán", expired.size());
        }
    }

    // ------------------------------------------------------------------ nội bộ

    private PaymentGateway gatewayOf(PaymentMethod method) {
        return gateways.stream().filter(g -> g.method() == method).findFirst()
                .orElseThrow(() -> BusinessException.badRequest("Phương thức thanh toán không được hỗ trợ"));
    }

    private PaymentMethod methodOf(Order order) {
        return paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId()).map(Payment::getMethod).orElse(null);
    }

    private OrderResponse toResponse(Order o, PaymentMethod method, String paymentUrl) {
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem i : o.getItems()) {
            items.add(new OrderItemResponse(i.getCourse().getId(), i.getCourseTitle(),
                    storageService.resolveUrl(i.getCourse().getThumbnailUrl()), i.getPrice()));
        }
        return new OrderResponse(o.getOrderCode(), o.getStatus(), method, o.getSubtotal(), o.getDiscountAmount(),
                o.getTotalAmount(), o.getCoupon() == null ? null : o.getCoupon().getCode(), o.getExpiresAt(),
                o.getPaidAt(), o.getCreatedAt(), items, paymentUrl);
    }
}
