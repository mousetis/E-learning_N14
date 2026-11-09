package iuh.fit.elearning.cart.service;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.cart.dto.response.AddToCartResponse;
import iuh.fit.elearning.cart.dto.response.CartItemResponse;
import iuh.fit.elearning.cart.dto.response.CartResponse;
import iuh.fit.elearning.cart.entity.Cart;
import iuh.fit.elearning.cart.entity.CartItem;
import iuh.fit.elearning.cart.repository.CartItemRepository;
import iuh.fit.elearning.cart.repository.CartRepository;
import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.course.service.CourseService;
import iuh.fit.elearning.learning.service.CourseAccessService;
import iuh.fit.elearning.learning.service.EnrollmentService;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** UC009: giỏ hàng. */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final CourseAccessService accessService;
    private final EnrollmentService enrollmentService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       CourseRepository courseRepository, UserRepository userRepository,
                       CourseService courseService, CourseAccessService accessService,
                       EnrollmentService enrollmentService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
        this.accessService = accessService;
        this.enrollmentService = enrollmentService;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        return cartRepository.findByUserId(userId).map(c -> toResponse(c, userId)).orElseGet(CartService::emptyCart);
    }

    @Transactional
    public AddToCartResponse addItem(AuthUser user, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy khóa học"));
        if (!CourseService.isPubliclyVisible(course)) {
            throw new BusinessException(ErrorCode.COURSE_NOT_AVAILABLE);
        }
        if (course.getInstructor().getId().equals(user.id())) {
            throw BusinessException.badRequest("Bạn không thể mua khóa học của chính mình");
        }
        if (accessService.isEnrolled(user.id(), courseId)) {
            throw new BusinessException(ErrorCode.ALREADY_OWNED);
        }
        User owner = userRepository.getReferenceById(user.id());

        // Khóa học miễn phí: ghi danh ngay, không đưa vào giỏ
        if (course.getSalePrice().signum() == 0) {
            enrollmentService.enroll(owner, course, null);
            return new AddToCartResponse(true, getCart(user.id()));
        }

        Cart cart = cartRepository.findByUserId(user.id())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(owner).build()));
        if (cartItemRepository.existsByCartIdAndCourseId(cart.getId(), courseId)) {
            throw new BusinessException(ErrorCode.ALREADY_IN_CART);
        }
        cartItemRepository.saveAndFlush(CartItem.builder()
                .cart(cart)
                .course(course)
                .priceAtAdded(course.getSalePrice())
                .build());
        return new AddToCartResponse(false, toResponse(cart, user.id()));
    }

    @Transactional
    public CartResponse removeItem(UUID userId, UUID courseId) {
        cartRepository.findByUserId(userId).flatMap(cart -> cartItemRepository.findByCartIdAndCourseId(cart.getId(), courseId))
                .ifPresent(cartItemRepository::delete);
        cartItemRepository.flush();
        return cartRepository.findByUserId(userId).map(c -> toResponse(c, userId)).orElseGet(CartService::emptyCart);
    }

    private CartResponse toResponse(Cart cart, UUID userId) {
        return build(cartItemRepository.findByCartIdOrderByCreatedAtAsc(cart.getId()), userId);
    }

    private CartResponse build(List<CartItem> items, UUID userId) {
        List<Course> courses = items.stream().map(CartItem::getCourse).toList();
        List<CourseSummaryResponse> summaries = courseService.summarize(courses, userId);
        List<CartItemResponse> result = new ArrayList<>();
        BigDecimal original = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Course course = item.getCourse();
            boolean available = CourseService.isPubliclyVisible(course);
            if (available) {
                original = original.add(course.getOriginalPrice());
                subtotal = subtotal.add(course.getSalePrice());
            }
            boolean changed = item.getPriceAtAdded() != null
                    && item.getPriceAtAdded().compareTo(course.getSalePrice()) != 0;
            result.add(new CartItemResponse(summaries.get(i), item.getPriceAtAdded(), available, changed));
        }
        return new CartResponse(result, result.size(), original, subtotal, original.subtract(subtotal).max(BigDecimal.ZERO));
    }

    private static CartResponse emptyCart() {
        return new CartResponse(List.of(), 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
