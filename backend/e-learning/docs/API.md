# E-Learning Backend – Tài liệu API

Base URL: `http://<host>:8080/api/v1` · Spring Boot 4.1 · Java 21 · PostgreSQL · Redis · JWT.

## 1. Chạy dự án

```bash
cp .env.example .env          # điền DB_*, JWT_SECRET, REDIS_*, MAIL_* ...
# nạp biến môi trường (hoặc cấu hình trong IDE), rồi:
./mvnw clean verify
./mvnw spring-boot:run
```

* Schema DB: `documents/database/e-learning.sql` (`spring.jpa.hibernate.ddl-auto=validate`, hãy chạy script trước).
* Chưa có Redis: đặt `OTP_MODE=memory` (chỉ dùng khi dev).
* `MAIL_USERNAME` để trống: OTP được ghi ra log thay vì gửi email.
* `PAYMENT_MOCK_ENABLED=false` khi chạy production (tắt cổng thanh toán giả lập).
* `PDF_FONT_PATH`: đường dẫn font `.ttf` hỗ trợ tiếng Việt cho chứng chỉ/báo cáo PDF (để trống = tự dò font hệ thống).
* Không commit `.env`, thư mục upload.

## 2. Quy ước chung

**Phản hồi thành công / lỗi** (`ApiResponse`):

```json
{ "success": true,  "code": "OK", "message": "Thành công", "data": { }, "timestamp": "2026-01-01T00:00:00Z" }
{ "success": false, "code": "OTP_INVALID", "message": "Mã OTP không đúng", "errors": [{"field":"email","message":"..."}], "timestamp": "..." }
```

**Phân trang** (`?page=0&size=20`, `page` bắt đầu từ 0): `data = { items, page, size, totalElements, totalPages }`.

**Xác thực**: header `Authorization: Bearer <accessToken>`.
Vai trò: `STUDENT`, `INSTRUCTOR`, `ADMIN`. `/admin/**` chỉ ADMIN; `/instructor/**` cho INSTRUCTOR và ADMIN.
Vai trò và trạng thái tài khoản được đọc từ DB ở mỗi request nên khóa tài khoản/đổi vai trò có hiệu lực ngay.

## 3. JWT & OTP

| Mục | Giá trị |
|---|---|
| Access token | JWT HS256, 15 phút, claim `sub`, `role`, `jti` |
| Refresh token | chuỗi ngẫu nhiên 256 bit, 7 ngày, DB chỉ lưu SHA-256; **xoay vòng** mỗi lần refresh, dùng lại token cũ → thu hồi cả phiên |
| Đăng nhập sai | 5 lần → khóa 15 phút |
| OTP | 6 số, lưu băm SHA-256 trong Redis, TTL 5 phút, tối đa 5 lần sai, gửi lại sau 60 giây |

Luồng: `register` → email nhận OTP → `verify-otp` → nhận cặp token. Quên mật khẩu: `forgot-password` → `reset-password` (kèm OTP).
Refresh: `POST /auth/refresh {refreshToken}` → cặp token mới. Đăng xuất: `POST /auth/logout {refreshToken}`.

## 4. Mã lỗi

| HTTP | Mã |
|---|---|
| 400 | `VALIDATION_ERROR`, `BAD_REQUEST`, `OTP_INVALID`, `OTP_EXPIRED`, `INVALID_SIGNATURE` |
| 401 | `UNAUTHORIZED`, `TOKEN_EXPIRED` (client gọi refresh), `INVALID_CREDENTIALS` |
| 403 | `ACCOUNT_NOT_VERIFIED`, `ACCOUNT_LOCKED`, `FORBIDDEN`, `NOT_ENROLLED` |
| 404 / 405 | `RESOURCE_NOT_FOUND` / `METHOD_NOT_ALLOWED` |
| 409 | `EMAIL_EXISTS`, `ALREADY_OWNED`, `ALREADY_IN_CART`, `CATEGORY_IN_USE`, `COURSE_HAS_STUDENTS`, `DUPLICATE` |
| 413 / 415 | `FILE_TOO_LARGE` / `UNSUPPORTED_FILE_TYPE` |
| 422 | `COUPON_INVALID`, `COURSE_NOT_AVAILABLE`, `COURSE_NOT_READY`, `REVIEW_NOT_ALLOWED`, `REFUND_NOT_ALLOWED`, `INVALID_STATE` |
| 429 | `TOO_MANY_ATTEMPTS` |
| 500 / 502 / 503 | `INTERNAL_ERROR`, `PAYMENT_GATEWAY_ERROR`, `SERVICE_UNAVAILABLE` |

## 5. Endpoint

Ký hiệu: 🌐 công khai · 🔐 cần đăng nhập · 👨‍🏫 giảng viên/admin · 🛡️ admin.

### Xác thực (`/auth`)
| | Method | Path |
|---|---|---|
| 🌐 | POST | `/auth/register` · `/auth/verify-otp` · `/auth/resend-otp` · `/auth/login` · `/auth/refresh` · `/auth/forgot-password` · `/auth/reset-password` |
| 🔐 | POST | `/auth/logout` |

### Hồ sơ (`/users/me`)
🔐 `GET /users/me` · `PUT /users/me` · `PUT /users/me/password` (đổi mật khẩu thu hồi các phiên khác).

### Danh mục & khóa học (công khai)
🌐 `GET /categories` · `/topics/hot` · `/courses` (tìm kiếm/lọc/sắp xếp: `keyword, categoryId, minPrice, maxPrice, minRating, level, duration=SHORT|MEDIUM|LONG, sort=popular|newest|rating|price_asc|price_desc, page, size`) · `/courses/popular` · `/courses/for-you` · `/courses/{id}` · `/courses/{id}/curriculum` · `/courses/{id}/similar` · `/categories/{slug}/courses` · `/lessons/{id}/preview`

### Giảng viên (công khai)
🌐 `GET /instructors` · `/instructors/top` · `/instructors/{id}` · `/instructors/{id}/courses` (nhóm theo danh mục) · `/instructors/{id}/reviews`
🔐 `POST|DELETE /instructors/{id}/follow`

### Học viên – tương tác
🔐 `GET /saved-courses` · `POST|DELETE /saved-courses/{courseId}` · `POST|DELETE /courses/{id}/like`
Đánh giá: 🌐 `GET /courses/{id}/reviews` · 🔐 `GET /courses/{id}/reviews/me` · `POST /courses/{id}/reviews` · `PUT /reviews/{id}` (cần tiến độ ≥ 20%, mỗi khóa 1 đánh giá).
Hỏi đáp: 🌐 `GET /courses/{id}/qa-posts` · `GET /qa-posts/{id}/comments` · 🔐 `POST /courses/{id}/qa-posts` · `DELETE /qa-posts/{id}` · `POST /qa-posts/{id}/comments` · `POST /qa-posts/{id}/like`
Dự án: 🔐 `GET /courses/{id}/project` · `GET /documents/{id}/download` (trả URL tải có chữ ký) · `GET|POST /courses/{id}/submissions` (multipart: `title`, `description`, `file`)

### Giỏ hàng, mã giảm giá, đơn hàng
🔐 `GET /cart` · `POST /cart/items {courseId}` (khóa miễn phí → ghi danh ngay) · `DELETE /cart/items/{courseId}`
🔐 `POST /coupons/validate {code, courseIds}` · `POST /orders {courseIds, couponCode, method: MOCK|VNPAY}` · `GET /orders` · `GET /orders/{orderCode}`
Đơn 0đ → phương thức `FREE`, ghi danh ngay. Đơn PENDING quá hạn tự hủy (job mỗi phút). `MOMO` chưa hỗ trợ.

### Thanh toán (`/payments`, công khai – xác thực bằng chữ ký/đơn hàng)
`GET /payments/mock/{orderCode}` (trang giả lập) · `POST /payments/mock/{orderCode}/complete?result=SUCCESS|FAILED` · `GET /payments/vnpay/return` · `GET /payments/vnpay/ipn` · `POST /payments/momo/ipn` (chưa hỗ trợ).
`POST /orders` trả `paymentUrl`; app mở URL này trong WebView/trình duyệt, sau đó gọi `GET /orders/{orderCode}` để lấy trạng thái.

### Học tập
🔐 `GET /me/enrollments` · `GET /learn/courses/{courseId}` · `GET /learn/lessons/{lessonId}/stream` · `PUT /learn/lessons/{lessonId}/progress {positionSeconds}`
Hoàn thành bài khi xem ≥ 90% thời lượng; tiến độ 100% → khóa COMPLETED và cấp chứng chỉ `ELN-yyyyMM-xxxxxxxx`.
Chứng chỉ: 🔐 `GET /me/certificates` · 🌐 `GET /certificates/{code}` · `GET /certificates/{code}/pdf`
Hoàn tiền: 🔐 `POST /me/enrollments/{id}/refund {reason}` (đang học, đã thanh toán, ≤ 7 ngày, tiến độ < 20%).

### Thông báo (🔐)
`GET /notifications` · `GET /notifications/unread-count` · `PATCH /notifications/{id}/read` · `PATCH /notifications/read-all`

### Tệp (`/files`)
🔐 `POST /files` (multipart: `file`, `type=IMAGE|VIDEO|DOCUMENT|PROJECT`) · 🌐 `GET /files/public/{key}` · `GET /files/content/{key}?exp=&sig=` (URL có chữ ký, có hạn)

### Giảng viên – quản lý khóa học (👨‍🏫 `/instructor`)
`GET|POST /instructor/courses` · `GET|PUT|DELETE /instructor/courses/{id}` · `POST /instructor/courses/{id}/submit-review` · `POST /instructor/courses/{id}/unpublish`
`POST /instructor/courses/{id}/sections` · `PUT|DELETE /instructor/sections/{id}` · `POST /instructor/sections/{id}/lessons` · `PUT|DELETE /instructor/lessons/{id}`
`POST /instructor/courses/{id}/documents` · `DELETE /instructor/courses/{id}/documents/{documentId}` · `PUT /instructor/courses/{id}/project`
`GET /instructor/courses/{id}/qa-posts` · `POST /instructor/qa-posts/{id}/answers` · `PATCH /instructor/qa-posts/{id}/hide` · `PUT /instructor/submissions/{id}/feedback`
Điều kiện gửi duyệt: có ảnh bìa, ≥ 1 chương, ≥ 3 bài có video, giá hợp lệ. Thời lượng video do giảng viên khai báo (`durationSeconds`).
Khóa đã PUBLISHED đổi tiêu đề/giá sẽ quay lại `PENDING_REVIEW`.

### Quản trị (🛡️ `/admin`)
* Khóa học: `GET /admin/courses` · `GET /admin/courses/{id}` · `POST /admin/courses/{id}/approve` · `POST /admin/courses/{id}/reject {reason}`
* Danh mục/chủ đề: `GET|POST /admin/categories` · `PUT|DELETE /admin/categories/{id}` · `GET|POST /admin/topics` · `PUT|DELETE /admin/topics/{id}`
* Người dùng: `GET /admin/users` · `PATCH /admin/users/{id}/lock|unlock` · `PATCH /admin/users/{id}/role` · `DELETE /admin/users/{id}`
* Khuyến mãi: `GET|POST /admin/coupons` · `GET|PUT /admin/coupons/{id}` · `PATCH /admin/coupons/{id}/deactivate` · `GET|POST /admin/banners` · `PUT|DELETE /admin/banners/{id}`; banner công khai: 🌐 `GET /banners?position=`
* Thống kê: `GET /admin/statistics/{revenue|students|courses|instructors}?from=&to=&groupBy=day|week|month` · `.../export?format=xlsx|pdf`

## 6. Quy tắc nghiệp vụ chính (SDS 7.8)

* **Tổng đơn**: tạm tính theo giá bán hiện tại; mã giảm giá `PERCENTAGE` = min(tạm tính × % / 100, giảm tối đa), `FIXED_AMOUNT` = min(giá trị, tạm tính). Lượt dùng mã được tính khi thanh toán thành công.
* **Tiến độ** = số bài hoàn thành / tổng số bài × 100 (2 chữ số thập phân); bài đã hoàn thành không bị bỏ đánh dấu.
* **Hoàn tiền** = giá khóa − phần giảm giá phân bổ theo tỷ lệ giá; VNPay gọi API refund của cổng, MOCK hoàn giả lập thành công.
* **Best-seller**: top 10% theo số học viên trong cùng danh mục.
