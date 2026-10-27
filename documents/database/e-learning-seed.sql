-- =====================================================================
--  DỮ LIỆU MẪU (chạy SAU e-learning.sql)
--  Mật khẩu của tất cả tài khoản mẫu: 123456 (BCrypt)
-- =====================================================================

-- ---------- Người dùng ----------
INSERT INTO users (id, full_name, email, password_hash, role, status, job_title, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000001', 'Quản trị viên',   'admin@elearning.local',    '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'ADMIN',      'ACTIVE', 'Administrator', now()),
('00000000-0000-0000-0000-000000000002', 'Sara Weise',      'sara@elearning.local',     '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'UI/UX Designer', now()),
('00000000-0000-0000-0000-000000000003', 'Jenny Barry',     'jenny@elearning.local',    '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Senior Developer', now()),
('00000000-0000-0000-0000-000000000004', 'Nguyễn Văn Học',  'student@elearning.local',  '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT',    'ACTIVE', 'Sinh viên', now());

INSERT INTO instructor_profiles (user_id, expertise, introduction, location, timezone, course_count) VALUES
('00000000-0000-0000-0000-000000000002', 'UI/UX Design', 'Hơn 8 năm kinh nghiệm thiết kế sản phẩm số.', 'New York', 'America/New_York', 1),
('00000000-0000-0000-0000-000000000003', 'Web & Mobile Development', 'Giảng viên lập trình React Native và Spring Boot.', 'Ho Chi Minh City', 'Asia/Ho_Chi_Minh', 1);

-- ---------- Danh mục & chủ đề ----------
INSERT INTO categories (id, name, slug, color, sort_order) VALUES
('10000000-0000-0000-0000-000000000001', 'Business', 'business', '#4F46E5', 1),
('10000000-0000-0000-0000-000000000002', 'Design',   'design',   '#EC4899', 2),
('10000000-0000-0000-0000-000000000003', 'Code',     'code',     '#10B981', 3),
('10000000-0000-0000-0000-000000000004', 'Writing',  'writing',  '#F59E0B', 4),
('10000000-0000-0000-0000-000000000005', 'Movie',    'movie',    '#8B5CF6', 5),
('10000000-0000-0000-0000-000000000006', 'Language', 'language', '#06B6D4', 6);

INSERT INTO topics (id, name, slug, is_hot) VALUES
('20000000-0000-0000-0000-000000000001', 'Java',         'java',         TRUE),
('20000000-0000-0000-0000-000000000002', 'React Native', 'react-native', TRUE),
('20000000-0000-0000-0000-000000000003', 'UX/UI',        'ux-ui',        TRUE),
('20000000-0000-0000-0000-000000000004', 'Figma',        'figma',        FALSE);

-- ---------- Khóa học ----------
INSERT INTO courses (id, instructor_id, category_id, title, slug, description, level,
                     original_price, sale_price, total_duration_seconds, total_lessons, status, published_at) VALUES
('30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002',
 'UX Foundation: Introduction to User Experience', 'ux-foundation',
 'Khóa học nền tảng về trải nghiệm người dùng.', 'BEGINNER', 799000, 499000, 2700, 3, 'PUBLISHED', now()),
('30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003',
 'React Native cơ bản đến nâng cao', 'react-native-co-ban-den-nang-cao',
 'Xây dựng ứng dụng di động đa nền tảng với React Native và Expo.', 'INTERMEDIATE', 1200000, 899000, 3600, 3, 'PUBLISHED', now());

INSERT INTO course_topics (course_id, topic_id) VALUES
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000004'),
('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002');

INSERT INTO course_benefits (course_id, content, sort_order) VALUES
('30000000-0000-0000-0000-000000000001', '14 giờ video theo yêu cầu', 1),
('30000000-0000-0000-0000-000000000001', 'Chứng chỉ hoàn thành', 2),
('30000000-0000-0000-0000-000000000002', 'Dự án thực tế cuối khóa', 1);

INSERT INTO sections (id, course_id, title, order_index) VALUES
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'I - Introduction', 1),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'II - Plan for your UX Research', 2),
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000002', 'I - Làm quen với React Native', 1);

INSERT INTO lessons (section_id, title, video_url, duration_seconds, order_index, is_preview) VALUES
('40000000-0000-0000-0000-000000000001', 'Amet adipisicing consectetur', 'https://example.com/videos/ux-1.mp4', 720,  1, TRUE),
('40000000-0000-0000-0000-000000000001', 'Culpa est incididunt enim id', 'https://example.com/videos/ux-2.mp4', 900,  2, FALSE),
('40000000-0000-0000-0000-000000000002', 'Exercitation elit incididunt', 'https://example.com/videos/ux-3.mp4', 1080, 1, FALSE),
('40000000-0000-0000-0000-000000000003', 'Cài đặt môi trường Expo',      'https://example.com/videos/rn-1.mp4', 1200, 1, TRUE),
('40000000-0000-0000-0000-000000000003', 'Component và Props',           'https://example.com/videos/rn-2.mp4', 1200, 2, FALSE),
('40000000-0000-0000-0000-000000000003', 'State và Hooks',               'https://example.com/videos/rn-3.mp4', 1200, 3, FALSE);

INSERT INTO course_projects (course_id, description) VALUES
('30000000-0000-0000-0000-000000000001', 'Thiết kế lại luồng onboarding của một ứng dụng bạn yêu thích.');

INSERT INTO course_documents (course_id, file_name, file_url, file_size, file_type) VALUES
('30000000-0000-0000-0000-000000000001', 'Document 1.pdf', 'https://example.com/docs/ux-doc-1.pdf', 645120, 'pdf');

-- ---------- Khuyến mãi & banner ----------
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_value, max_discount_amount, usage_limit, start_at, end_at) VALUES
('WELCOME20', 'Giảm 20% cho đơn đầu tiên', 'PERCENTAGE', 20, 200000, 200000, 100, now(), now() + interval '90 days'),
('GIAM50K',   'Giảm 50.000đ',              'FIXED_AMOUNT', 50000, 300000, NULL, NULL, now(), now() + interval '30 days');

INSERT INTO banners (title, subtitle, image_url, action_label, link_type, link_value, position, sort_order) VALUES
('Project Management', '20% OFF', 'https://example.com/banners/pm.png', 'JOIN NOW', 'CATEGORY', 'business', 'HOME', 1),
('Học mọi lúc, mọi nơi', NULL,    'https://example.com/banners/mc.png', 'KHÁM PHÁ', 'NONE', NULL, 'MY_COURSES', 1);

-- ---------- Giỏ hàng & khóa học đã lưu của học viên mẫu ----------
INSERT INTO carts (user_id) VALUES ('00000000-0000-0000-0000-000000000004');
INSERT INTO saved_courses (user_id, course_id) VALUES
('00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002');
