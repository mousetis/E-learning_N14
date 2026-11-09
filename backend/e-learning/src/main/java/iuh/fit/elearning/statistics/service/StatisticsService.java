package iuh.fit.elearning.statistics.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.course.repository.CourseRepository;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import iuh.fit.elearning.learning.repository.EnrollmentRepository;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.OrderItem;
import iuh.fit.elearning.order.entity.enums.OrderStatus;
import iuh.fit.elearning.order.repository.OrderRepository;
import iuh.fit.elearning.statistics.dto.StatisticsReport;
import iuh.fit.elearning.user.entity.InstructorProfile;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.repository.InstructorProfileRepository;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** UC025: thống kê doanh thu, học viên, khóa học, giảng viên (múi giờ Asia/Ho_Chi_Minh). */
@Service
public class StatisticsService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final long MAX_RANGE_DAYS = 1830;

    private final OrderRepository orderRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    public StatisticsService(OrderRepository orderRepository, EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository, CourseRepository courseRepository,
                             InstructorProfileRepository instructorProfileRepository) {
        this.orderRepository = orderRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.instructorProfileRepository = instructorProfileRepository;
    }

    /** from / to / groupBy tùy chọn: mặc định 30 ngày gần nhất, nhóm theo ngày (day | month). */
    @Transactional(readOnly = true)
    public StatisticsReport build(String type, LocalDate from, LocalDate to, String groupBy) {
        LocalDate end = to != null ? to : LocalDate.now(ZONE);
        LocalDate start = from != null ? from : end.minusDays(29);
        if (start.isAfter(end)) {
            throw BusinessException.badRequest("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_RANGE_DAYS) {
            throw BusinessException.badRequest("Khoảng thời gian thống kê tối đa 5 năm");
        }
        String group = groupBy == null || groupBy.isBlank() ? "day" : groupBy.trim().toLowerCase();
        if (!group.equals("day") && !group.equals("month")) {
            throw BusinessException.badRequest("groupBy phải là day hoặc month");
        }
        Instant fromInstant = start.atStartOfDay(ZONE).toInstant();
        Instant toInstant = end.plusDays(1).atStartOfDay(ZONE).toInstant().minusMillis(1);

        String normalizedType = type == null ? "" : type.trim().toLowerCase();
        return switch (normalizedType) {
            case "revenue" -> revenue(start, end, group, fromInstant, toInstant);
            case "students" -> students(start, end, group, fromInstant, toInstant);
            case "courses" -> courses(start, end, group, fromInstant, toInstant);
            case "instructors" -> instructors(start, end, group, fromInstant, toInstant);
            default -> throw BusinessException.badRequest("type phải là revenue, students, courses hoặc instructors");
        };
    }

    // ------------------------------------------------------------------ doanh thu

    private StatisticsReport revenue(LocalDate from, LocalDate to, String group, Instant a, Instant b) {
        List<Order> orders = orderRepository.findByStatusAndPaidAtBetween(OrderStatus.PAID, a, b);
        Map<String, long[]> counts = periods(from, to, group, 1);
        Map<String, BigDecimal[]> sums = new LinkedHashMap<>();
        for (String key : counts.keySet()) {
            sums.put(key, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal revenue = BigDecimal.ZERO;
        for (Order o : orders) {
            String key = periodKey(o.getPaidAt(), group);
            counts.get(key)[0]++;
            BigDecimal[] s = sums.get(key);
            s[0] = s[0].add(o.getSubtotal());
            s[1] = s[1].add(o.getDiscountAmount());
            s[2] = s[2].add(o.getTotalAmount());
            subtotal = subtotal.add(o.getSubtotal());
            discount = discount.add(o.getDiscountAmount());
            revenue = revenue.add(o.getTotalAmount());
        }
        List<List<Object>> rows = new ArrayList<>();
        for (String key : counts.keySet()) {
            BigDecimal[] s = sums.get(key);
            rows.add(List.of(key, counts.get(key)[0], s[0], s[1], s[2]));
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Tổng doanh thu", revenue);
        summary.put("Số đơn đã thanh toán", (long) orders.size());
        summary.put("Tổng giá trị trước giảm giá", subtotal);
        summary.put("Tổng giảm giá", discount);
        summary.put("Giá trị đơn trung bình", orders.isEmpty() ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(orders.size()), 0, RoundingMode.HALF_UP));
        return new StatisticsReport("revenue", "Báo cáo doanh thu", from, to, group, summary,
                List.of(group.equals("month") ? "Tháng" : "Ngày", "Số đơn", "Tạm tính", "Giảm giá", "Doanh thu"), rows);
    }

    // ------------------------------------------------------------------ học viên

    private StatisticsReport students(LocalDate from, LocalDate to, String group, Instant a, Instant b) {
        List<User> newUsers = userRepository.findByRoleAndCreatedAtBetween(UserRole.STUDENT, a, b);
        List<Enrollment> enrollments = enrollmentRepository.findByEnrolledAtBetween(a, b);
        Map<String, long[]> counts = periods(from, to, group, 2);
        for (User u : newUsers) {
            counts.get(periodKey(u.getCreatedAt(), group))[0]++;
        }
        for (Enrollment e : enrollments) {
            counts.get(periodKey(e.getEnrolledAt(), group))[1]++;
        }
        List<List<Object>> rows = new ArrayList<>();
        counts.forEach((key, c) -> rows.add(List.of(key, c[0], c[1])));
        long completed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long refunded = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.REFUNDED).count();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Học viên đăng ký mới", (long) newUsers.size());
        summary.put("Lượt ghi danh", (long) enrollments.size());
        summary.put("Đã hoàn thành khóa học", completed);
        summary.put("Đã hoàn tiền", refunded);
        return new StatisticsReport("students", "Báo cáo học viên", from, to, group, summary,
                List.of(group.equals("month") ? "Tháng" : "Ngày", "Học viên mới", "Lượt ghi danh"), rows);
    }

    // ------------------------------------------------------------------ khóa học

    private StatisticsReport courses(LocalDate from, LocalDate to, String group, Instant a, Instant b) {
        Map<UUID, long[]> soldCount = new HashMap<>();
        Map<UUID, BigDecimal> soldRevenue = new HashMap<>();
        for (Order o : orderRepository.findByStatusAndPaidAtBetween(OrderStatus.PAID, a, b)) {
            for (OrderItem i : o.getItems()) {
                UUID id = i.getCourse().getId();
                soldCount.computeIfAbsent(id, k -> new long[1])[0]++;
                soldRevenue.merge(id, i.getPrice(), BigDecimal::add);
            }
        }
        List<Course> all = courseRepository.findAll();
        List<List<Object>> rows = new ArrayList<>();
        all.stream()
                .sorted(Comparator.comparing((Course c) -> soldRevenue.getOrDefault(c.getId(), BigDecimal.ZERO)).reversed()
                        .thenComparing(Course::getTitle))
                .forEach(c -> rows.add(List.of(c.getTitle(), c.getInstructor().getFullName(), c.getCategory().getName(),
                        c.getStatus().name(), soldCount.containsKey(c.getId()) ? soldCount.get(c.getId())[0] : 0L,
                        soldRevenue.getOrDefault(c.getId(), BigDecimal.ZERO), c.getStudentCount().longValue(),
                        c.getAvgRating(), c.getReviewCount().longValue())));
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Tổng số khóa học", (long) all.size());
        for (CourseStatus s : CourseStatus.values()) {
            summary.put("Khóa học " + s.name(), all.stream().filter(c -> c.getStatus() == s).count());
        }
        summary.put("Lượt bán trong kỳ", soldCount.values().stream().mapToLong(x -> x[0]).sum());
        return new StatisticsReport("courses", "Báo cáo khóa học", from, to, group, summary,
                List.of("Khóa học", "Giảng viên", "Danh mục", "Trạng thái", "Lượt bán trong kỳ", "Doanh thu trong kỳ",
                        "Tổng học viên", "Điểm đánh giá", "Số đánh giá"), rows);
    }

    // ------------------------------------------------------------------ giảng viên

    private StatisticsReport instructors(LocalDate from, LocalDate to, String group, Instant a, Instant b) {
        Map<UUID, long[]> sold = new HashMap<>();
        Map<UUID, BigDecimal> revenue = new HashMap<>();
        for (Order o : orderRepository.findByStatusAndPaidAtBetween(OrderStatus.PAID, a, b)) {
            for (OrderItem i : o.getItems()) {
                UUID instructorId = i.getCourse().getInstructor().getId();
                sold.computeIfAbsent(instructorId, k -> new long[1])[0]++;
                revenue.merge(instructorId, i.getPrice(), BigDecimal::add);
            }
        }
        List<InstructorProfile> profiles = instructorProfileRepository.findAll();
        List<List<Object>> rows = new ArrayList<>();
        profiles.stream()
                .sorted(Comparator.comparing((InstructorProfile p) -> revenue.getOrDefault(p.getUser().getId(),
                        BigDecimal.ZERO)).reversed().thenComparing(p -> p.getUser().getFullName()))
                .forEach(p -> {
                    UUID id = p.getUser().getId();
                    rows.add(List.of(p.getUser().getFullName(), p.getUser().getEmail(),
                            p.getCourseCount().longValue(), sold.containsKey(id) ? sold.get(id)[0] : 0L,
                            revenue.getOrDefault(id, BigDecimal.ZERO), p.getStudentCount().longValue(),
                            p.getFollowerCount().longValue(), p.getAvgRating()));
                });
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("Tổng số giảng viên", (long) profiles.size());
        summary.put("Lượt bán trong kỳ", sold.values().stream().mapToLong(x -> x[0]).sum());
        summary.put("Doanh thu trong kỳ", revenue.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        return new StatisticsReport("instructors", "Báo cáo giảng viên", from, to, group, summary,
                List.of("Giảng viên", "Email", "Khóa học xuất bản", "Lượt bán trong kỳ", "Doanh thu trong kỳ",
                        "Tổng học viên", "Người theo dõi", "Điểm đánh giá"), rows);
    }

    // ------------------------------------------------------------------ nội bộ

    /** Tạo sẵn mọi kỳ trong khoảng thời gian (giá trị khởi tạo 0) để báo cáo không bị hụt ngày / tháng. */
    private static Map<String, long[]> periods(LocalDate from, LocalDate to, String group, int width) {
        Map<String, long[]> map = new LinkedHashMap<>();
        if (group.equals("month")) {
            for (YearMonth m = YearMonth.from(from); !m.isAfter(YearMonth.from(to)); m = m.plusMonths(1)) {
                map.put(m.toString(), new long[width]);
            }
        } else {
            for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                map.put(d.toString(), new long[width]);
            }
        }
        return map;
    }

    private static String periodKey(Instant instant, String group) {
        LocalDate date = instant.atZone(ZONE).toLocalDate();
        return group.equals("month") ? YearMonth.from(date).toString() : date.toString();
    }
}
