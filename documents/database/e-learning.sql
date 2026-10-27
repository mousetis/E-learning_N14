-- =====================================================================
--  E-LEARNING DATABASE (PostgreSQL 13+)
--  Nguồn chuẩn: 03_SRS_ELearning.docx (mục 2.5 quy tắc nghiệp vụ, mục 4 biểu đồ lớp)
--  Quy ước:
--    - Khóa chính UUID (gen_random_uuid()).
--    - Tên bảng/cột snake_case, khớp với naming strategy mặc định của Spring Boot.
--    - Các "enum" lưu dạng VARCHAR + CHECK để map trực tiếp với @Enumerated(EnumType.STRING).
--    - Thời gian dùng TIMESTAMPTZ (map với java.time.Instant).
--    - Tiền tệ dùng NUMERIC(12,2) (map với java.math.BigDecimal).
--    - OTP lưu tạm (Redis / bộ nhớ giả lập), KHÔNG lưu trong CSDL.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0. Dọn schema cũ (chạy lại script nhiều lần không lỗi)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS
    notifications, banners, follows, course_likes, reviews,
    qa_post_likes, qa_comments, qa_posts, project_submissions,
    refund_requests, certificates, lesson_progress, enrollments,
    coupon_usages, payments, order_items, orders, coupons,
    cart_items, carts, saved_courses,
    course_projects, course_documents, lessons, sections,
    course_benefits, course_topics, courses, topics, categories,
    refresh_tokens, instructor_profiles, users,
    -- bảng của schema cũ
    qna, projects, project_resources
CASCADE;
DROP TYPE IF EXISTS user_role, enrollment_status CASCADE;

-- =====================================================================
-- 1. TÀI KHOẢN & XÁC THỰC
-- =====================================================================
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name           VARCHAR(150) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    avatar_url          TEXT,
    cover_url           TEXT,
    job_title           VARCHAR(150),
    bio                 TEXT,
    role                VARCHAR(20)  NOT NULL DEFAULT 'STUDENT'
                        CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')),
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING', 'ACTIVE', 'LOCKED', 'DELETED')),
    email_verified_at   TIMESTAMPTZ,
    last_login_at       TIMESTAMPTZ,
    failed_login_count  INTEGER      NOT NULL DEFAULT 0,   -- UC002: sai 5 lần -> khóa 15 phút
    login_locked_until  TIMESTAMPTZ,
    deleted_at          TIMESTAMPTZ,                       -- UC023: xóa mềm
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Hồ sơ giảng viên (tạo khi admin cấp quyền INSTRUCTOR) – UC007, UC016, UC018
CREATE TABLE instructor_profiles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    expertise       VARCHAR(255),
    introduction    TEXT,
    location        VARCHAR(100),
    timezone        VARCHAR(50),
    follower_count  INTEGER       NOT NULL DEFAULT 0,
    student_count   INTEGER       NOT NULL DEFAULT 0,
    course_count    INTEGER       NOT NULL DEFAULT 0,
    avg_rating      NUMERIC(3,2)  NOT NULL DEFAULT 0,
    review_count    INTEGER       NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Refresh token theo phiên đăng nhập (mỗi thiết bị một bản ghi) – UC002, UC003, UC023
CREATE TABLE refresh_tokens (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash   VARCHAR(255) NOT NULL UNIQUE,
    fcm_token    TEXT,                                   -- token thông báo đẩy của thiết bị
    device_info  VARCHAR(255),
    expires_at   TIMESTAMPTZ  NOT NULL,
    revoked_at   TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- =====================================================================
-- 2. DANH MỤC & CHỦ ĐỀ
-- =====================================================================
CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(120) NOT NULL UNIQUE,
    icon_url    TEXT,
    color       VARCHAR(20),
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Chủ đề (Hot topics) – nhiều-nhiều với khóa học
CREATE TABLE topics (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(100) NOT NULL UNIQUE,
    slug          VARCHAR(120) NOT NULL UNIQUE,
    search_count  INTEGER      NOT NULL DEFAULT 0,
    is_hot        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- =====================================================================
-- 3. KHÓA HỌC & NỘI DUNG
-- =====================================================================
CREATE TABLE courses (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    instructor_id           UUID          NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    category_id             UUID          NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    title                   VARCHAR(255)  NOT NULL,
    slug                    VARCHAR(280)  NOT NULL UNIQUE,
    description             TEXT,
    thumbnail_url           TEXT,
    intro_video_url         TEXT,
    level                   VARCHAR(20)   NOT NULL DEFAULT 'ALL_LEVELS'
                            CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS')),
    original_price          NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (original_price >= 0),
    sale_price              NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (sale_price >= 0),
    total_duration_seconds  INTEGER       NOT NULL DEFAULT 0,
    total_lessons           INTEGER       NOT NULL DEFAULT 0,
    avg_rating              NUMERIC(3,2)  NOT NULL DEFAULT 0,
    review_count            INTEGER       NOT NULL DEFAULT 0,
    student_count           INTEGER       NOT NULL DEFAULT 0,
    like_count              INTEGER       NOT NULL DEFAULT 0,
    status                  VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'
                            CHECK (status IN ('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'REJECTED', 'UNPUBLISHED')),
    rejection_reason        TEXT,
    published_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT chk_courses_price CHECK (sale_price <= original_price)  -- giá bán không lớn hơn giá gốc
);
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_courses_category   ON courses(category_id);
CREATE INDEX idx_courses_status     ON courses(status);

CREATE TABLE course_topics (
    course_id  UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    topic_id   UUID NOT NULL REFERENCES topics(id)  ON DELETE CASCADE,
    PRIMARY KEY (course_id, topic_id)
);

-- Lợi ích khóa học (LoiIchKhoaHoc)
CREATE TABLE course_benefits (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id   UUID         NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    content     VARCHAR(500) NOT NULL,
    icon        VARCHAR(100),
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Chương (ChuongHoc)
CREATE TABLE sections (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id    UUID         NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title        VARCHAR(255) NOT NULL,
    order_index  INTEGER      NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (course_id, order_index)
);

-- Bài học (BaiHoc)
CREATE TABLE lessons (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    section_id        UUID         NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    title             VARCHAR(255) NOT NULL,
    video_url         TEXT,
    duration_seconds  INTEGER      NOT NULL DEFAULT 0,
    order_index       INTEGER      NOT NULL,
    is_preview        BOOLEAN      NOT NULL DEFAULT FALSE,   -- bài học thử
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (section_id, order_index)
);

-- Tài liệu khóa học (TaiLieu)
CREATE TABLE course_documents (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id   UUID         NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    file_name   VARCHAR(255) NOT NULL,
    file_url    TEXT         NOT NULL,
    file_size   BIGINT,
    file_type   VARCHAR(50),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Mô tả dự án của khóa học (DuAnKhoaHoc) – 0..1 mỗi khóa học
CREATE TABLE course_projects (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id    UUID        NOT NULL UNIQUE REFERENCES courses(id) ON DELETE CASCADE,
    description  TEXT        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =====================================================================
-- 4. LƯU KHÓA HỌC, GIỎ HÀNG
-- =====================================================================
CREATE TABLE saved_courses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    course_id   UUID        NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, course_id)
);

CREATE TABLE carts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id         UUID          NOT NULL REFERENCES carts(id)   ON DELETE CASCADE,
    course_id       UUID          NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    price_at_added  NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    UNIQUE (cart_id, course_id)
);

-- =====================================================================
-- 5. KHUYẾN MÃI, ĐƠN HÀNG, THANH TOÁN
-- =====================================================================
CREATE TABLE coupons (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                 VARCHAR(50)   NOT NULL UNIQUE,
    description          VARCHAR(255),
    discount_type        VARCHAR(20)   NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value       NUMERIC(12,2) NOT NULL CHECK (discount_value > 0),
    min_order_value      NUMERIC(12,2) NOT NULL DEFAULT 0,
    max_discount_amount  NUMERIC(12,2),
    usage_limit          INTEGER,                         -- NULL = không giới hạn
    used_count           INTEGER       NOT NULL DEFAULT 0,
    start_at             TIMESTAMPTZ   NOT NULL,
    end_at               TIMESTAMPTZ   NOT NULL,
    is_active            BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT chk_coupons_time    CHECK (start_at < end_at),
    CONSTRAINT chk_coupons_percent CHECK (discount_type <> 'PERCENTAGE' OR discount_value <= 100),
    CONSTRAINT chk_coupons_usage   CHECK (usage_limit IS NULL OR used_count <= usage_limit)
);

CREATE TABLE orders (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_code       VARCHAR(30)   NOT NULL UNIQUE,
    user_id          UUID          NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    coupon_id        UUID          REFERENCES coupons(id) ON DELETE SET NULL,  -- mỗi đơn tối đa 1 mã
    subtotal         NUMERIC(12,2) NOT NULL,
    discount_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount     NUMERIC(12,2) NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'CANCELLED')),
    paid_at          TIMESTAMPTZ,
    expires_at       TIMESTAMPTZ,                       -- PENDING quá 15 phút -> FAILED
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_user   ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

-- Chi tiết đơn hàng: snapshot tên & giá khóa học tại thời điểm mua
CREATE TABLE order_items (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id      UUID          NOT NULL REFERENCES orders(id)  ON DELETE CASCADE,
    course_id     UUID          NOT NULL REFERENCES courses(id) ON DELETE RESTRICT,
    course_title  VARCHAR(255)  NOT NULL,
    price         NUMERIC(12,2) NOT NULL,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    UNIQUE (order_id, course_id)
);

CREATE TABLE payments (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id          UUID          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    method            VARCHAR(20)   NOT NULL CHECK (method IN ('VNPAY', 'MOMO', 'MOCK', 'FREE')),
    amount            NUMERIC(12,2) NOT NULL,
    transaction_code  VARCHAR(100),                     -- mã giao dịch từ cổng thanh toán
    status            VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                      CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    raw_response      TEXT,                             -- log IPN / phản hồi cổng thanh toán
    paid_at           TIMESTAMPTZ,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT now()
);
CREATE INDEX idx_payments_order ON payments(order_id);

CREATE TABLE coupon_usages (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id        UUID          NOT NULL REFERENCES coupons(id) ON DELETE CASCADE,
    user_id          UUID          NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    order_id         UUID          NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    discount_amount  NUMERIC(12,2) NOT NULL,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- =====================================================================
-- 6. HỌC TẬP: GHI DANH, TIẾN ĐỘ, CHỨNG CHỈ, HOÀN TIỀN
-- =====================================================================
CREATE TABLE enrollments (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID         NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    course_id            UUID         NOT NULL REFERENCES courses(id) ON DELETE RESTRICT,
    order_item_id        UUID         UNIQUE REFERENCES order_items(id) ON DELETE SET NULL, -- NULL nếu khóa miễn phí
    progress_percentage  NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (progress_percentage BETWEEN 0 AND 100),
    status               VARCHAR(20)  NOT NULL DEFAULT 'ONGOING'
                         CHECK (status IN ('ONGOING', 'COMPLETED', 'REFUNDED')),
    enrolled_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    completed_at         TIMESTAMPTZ,
    last_accessed_at     TIMESTAMPTZ,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (user_id, course_id)
);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);

CREATE TABLE lesson_progress (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id          UUID        NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    lesson_id              UUID        NOT NULL REFERENCES lessons(id)     ON DELETE CASCADE,
    last_position_seconds  INTEGER     NOT NULL DEFAULT 0,
    is_completed           BOOLEAN     NOT NULL DEFAULT FALSE,   -- xem >= 90% thời lượng
    completed_at           TIMESTAMPTZ,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (enrollment_id, lesson_id)
);

CREATE TABLE certificates (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id     UUID        NOT NULL UNIQUE REFERENCES enrollments(id) ON DELETE CASCADE, -- 1 chứng chỉ / khóa
    certificate_code  VARCHAR(50) NOT NULL UNIQUE,
    file_url          TEXT,
    issued_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Yêu cầu hoàn tiền (<= 7 ngày, tiến độ < 20%) – UC012
CREATE TABLE refund_requests (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id  UUID          NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    payment_id     UUID          REFERENCES payments(id) ON DELETE SET NULL,
    amount         NUMERIC(12,2) NOT NULL,
    reason         TEXT,
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                   CHECK (status IN ('PENDING', 'COMPLETED', 'REJECTED')),
    reject_reason  TEXT,
    processed_at   TIMESTAMPTZ,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- =====================================================================
-- 7. TƯƠNG TÁC: DỰ ÁN, Q&A, ĐÁNH GIÁ, THEO DÕI, THÍCH
-- =====================================================================
-- Bài nộp dự án (BaiNopDuAn) – mỗi học viên 1 bài / khóa học
CREATE TABLE project_submissions (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id      UUID         NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id        UUID         NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    file_url       TEXT         NOT NULL,
    file_name      VARCHAR(255),
    file_type      VARCHAR(50),
    file_size      BIGINT,
    thumbnail_url  TEXT,
    feedback       TEXT,                                -- nhận xét của giảng viên
    feedback_at    TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (course_id, user_id)
);

-- Câu hỏi Q&A (CauHoi)
CREATE TABLE qa_posts (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id      UUID          NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id        UUID          NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    content        VARCHAR(1000) NOT NULL,
    like_count     INTEGER       NOT NULL DEFAULT 0,
    comment_count  INTEGER       NOT NULL DEFAULT 0,
    is_answered    BOOLEAN       NOT NULL DEFAULT FALSE,
    is_hidden      BOOLEAN       NOT NULL DEFAULT FALSE,   -- giảng viên ẩn câu hỏi vi phạm
    deleted_at     TIMESTAMPTZ,                            -- xóa mềm
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);
CREATE INDEX idx_qa_posts_course ON qa_posts(course_id);

-- Bình luận / trả lời (BinhLuan)
CREATE TABLE qa_comments (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id                UUID          NOT NULL REFERENCES qa_posts(id) ON DELETE CASCADE,
    user_id                UUID          NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    content                VARCHAR(1000) NOT NULL,
    is_instructor_answer   BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at             TIMESTAMPTZ,
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT now()
);
CREATE INDEX idx_qa_comments_post ON qa_comments(post_id);

CREATE TABLE qa_post_likes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id     UUID        NOT NULL REFERENCES qa_posts(id) ON DELETE CASCADE,
    user_id     UUID        NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (post_id, user_id)
);

-- Đánh giá (DanhGia) – 1 học viên / 1 khóa, gắn với ghi danh
CREATE TABLE reviews (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id  UUID        NOT NULL UNIQUE REFERENCES enrollments(id) ON DELETE CASCADE,
    course_id      UUID        NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id        UUID        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    rating         INTEGER     NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment        VARCHAR(1000),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (course_id, user_id)
);
CREATE INDEX idx_reviews_course ON reviews(course_id);

-- Theo dõi giảng viên (TheoDoi)
CREATE TABLE follows (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    instructor_id  UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (follower_id, instructor_id),
    CONSTRAINT chk_follows_self CHECK (follower_id <> instructor_id)
);

-- Lượt thích khóa học (màn hình học – UC011)
CREATE TABLE course_likes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id   UUID        NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id     UUID        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (course_id, user_id)
);

-- =====================================================================
-- 8. THÔNG BÁO & BANNER
-- =====================================================================
CREATE TABLE notifications (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type          VARCHAR(30)  NOT NULL CHECK (type IN (
                      'PAYMENT', 'QA_REPLY', 'QA_NEW_QUESTION', 'CERTIFICATE', 'NEW_COURSE',
                      'PROMOTION', 'COURSE_REVIEW', 'PROJECT_FEEDBACK', 'REFUND', 'SYSTEM')),
    title         VARCHAR(255) NOT NULL,
    content       TEXT,
    reference_id  UUID,                                 -- id khóa học / câu hỏi / chứng chỉ / mã giảm giá...
    is_read       BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);

CREATE TABLE banners (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(255) NOT NULL,
    subtitle      VARCHAR(255),
    image_url     TEXT         NOT NULL,
    action_label  VARCHAR(50),
    link_type     VARCHAR(20)  NOT NULL DEFAULT 'NONE'
                  CHECK (link_type IN ('NONE', 'COURSE', 'CATEGORY', 'URL')),
    link_value    TEXT,
    position      VARCHAR(20)  NOT NULL DEFAULT 'HOME' CHECK (position IN ('HOME', 'MY_COURSES')),
    sort_order    INTEGER      NOT NULL DEFAULT 0,
    start_at      TIMESTAMPTZ,
    end_at        TIMESTAMPTZ,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
