-- =====================================================================
--  HỆ THỐNG E-LEARNING - BỘ DỮ LIỆU SEED ĐẦY ĐỦ VÀ CHUẨN XÁC
--  Tất cả tài khoản có mật khẩu: 123456 (BCrypt)
-- =====================================================================

-- Xóa dữ liệu cũ theo đúng thứ tự ràng buộc
TRUNCATE TABLE 
    certificates,
    lesson_progress,
    refund_requests,
    qa_comments,
    qa_post_likes,
    qa_posts,
    reviews,
    project_submissions,
    course_projects,
    course_documents,
    course_benefits,
    course_topics,
    lessons,
    sections,
    saved_courses,
    course_likes,
    order_items,
    orders,
    cart_items,
    carts,
    enrollments,
    courses,
    topics,
    categories,
    instructor_profiles,
    notifications,
    coupon_usages,
    coupons,
    banners,
    users
CASCADE;

-- =====================================================================
-- 1. NGƯỜI DÙNG (USERS)
-- Bao gồm: 1 Admin, 6 Giảng viên chuyên gia, 4 Học viên
-- Mật khẩu: 123456
-- =====================================================================
INSERT INTO users (id, full_name, email, password_hash, role, status, job_title, avatar_url, bio, email_verified_at) VALUES
-- Admin
('00000000-0000-0000-0000-000000000001', 'Quản trị viên', 'admin@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'ADMIN', 'ACTIVE', 'Hệ thống Quản trị',
 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&auto=format&fit=crop&q=80',
 'Quản trị viên hệ thống E-Learning.', now()),

-- 6 Giảng viên
('00000000-0000-0000-0000-000000000002', 'Sara Weise', 'sara@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Lead UI/UX Designer',
 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80',
 'Chuyên gia thiết kế sản phẩm số với hơn 10 năm kinh nghiệm tại Thung lũng Silicon.', now()),

('00000000-0000-0000-0000-000000000003', 'Jenny Barry', 'jenny@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Senior Fullstack Engineer',
 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&auto=format&fit=crop&q=80',
 'Kỹ sư công nghệ cấp cao chuyên về React Native, Spring Boot và Kiến trúc Microservices.', now()),

('00000000-0000-0000-0000-000000000005', 'Alex Morgan', 'alex@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Head of Growth Marketing',
 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80',
 'Cố vấn chiến lược tăng trưởng & Performance Marketing cho hơn 50 startup công nghệ.', now()),

('00000000-0000-0000-0000-000000000006', 'David Miller', 'david@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Filmmaker & Motion Artist',
 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80',
 'Đạo diễn hình ảnh và chuyên gia hậu kỳ Premiere Pro, After Effects từng đạt nhiều giải thưởng điện ảnh.', now()),

('00000000-0000-0000-0000-000000000007', 'Emily Watson', 'emily@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Creative Director & Author',
 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80',
 'Tác giả sách bán chạy và chuyên gia cố vấn chiến lược nội dung, copywriting thương hiệu.', now()),

('00000000-0000-0000-0000-000000000011', 'Michael Chen', 'michael@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'INSTRUCTOR', 'ACTIVE', 'Language Coach & Speaker',
 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80',
 'Thạc sĩ Giảng dạy Ngôn ngữ từ Đại học California, hơn 12 năm đào tạo tiếng Anh giao tiếp doanh nghiệp.', now()),

-- 4 Học viên
('00000000-0000-0000-0000-000000000004', 'Nguyễn Văn Học', 'student@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Sinh viên CNTT',
 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
 'Học viên đam mê phát triển ứng dụng di động và thiết kế UI/UX.', now()),

('00000000-0000-0000-0000-000000000008', 'Trần Thị Mai', 'mai.tran@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Chuyên viên Marketing',
 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
 'Tìm hiểu về phân tích dữ liệu và tự động hóa marketing.', now()),

('00000000-0000-0000-0000-000000000009', 'Lê Hoàng Nam', 'nam.le@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Lập trình viên Backend',
 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&auto=format&fit=crop&q=80',
 'Yêu thích Java, Spring Boot và xây dựng hệ thống phân tán.', now()),

('00000000-0000-0000-0000-000000000010', 'Phạm Thu Hà', 'ha.pham@elearning.local',
 '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Content Creator',
 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&auto=format&fit=crop&q=80',
 'Học làm video ngắn và nâng cao kỹ năng viết lách thu hút.', now());

-- =====================================================================
-- 2. HỒ SƠ GIẢNG VIÊN (INSTRUCTOR_PROFILES)
-- =====================================================================
INSERT INTO instructor_profiles (user_id, expertise, introduction, location, timezone, course_count) VALUES
('00000000-0000-0000-0000-000000000002', 'UI/UX Design & Prototyping', 'Chuyên gia thiết kế UI/UX hàng đầu với triết lý thiết kế lấy người dùng làm trung tâm.', 'San Francisco, USA', 'America/Los_Angeles', 2),
('00000000-0000-0000-0000-000000000003', 'React Native, Flutter & Spring Boot', 'Hơn 9 năm xây dựng các ứng dụng phục vụ hàng triệu người dùng tại Đông Nam Á.', 'TP. Hồ Chí Minh, VN', 'Asia/Ho_Chi_Minh', 3),
('00000000-0000-0000-0000-000000000005', 'Growth Hacking, Ads & Agile Project', 'Chuyên gia tối ưu hoá chuyển đổi và quản trị dự án phần mềm theo chuẩn Agile Scrum.', 'Singapore', 'Asia/Singapore', 2),
('00000000-0000-0000-0000-000000000006', 'Video Editing, Color Grading & VFX', 'Đạo diễn video và nghệ sĩ dựng phim với hơn 8 năm làm việc cho các thương hiệu lớn.', 'London, UK', 'Europe/London', 2),
('00000000-0000-0000-0000-000000000007', 'Copywriting & Content Strategy', 'Giảng dạy kỹ năng viết truyền cảm hứng và sáng tạo nội dung đa kênh bán hàng.', 'Hà Nội, VN', 'Asia/Ho_Chi_Minh', 2),
('00000000-0000-0000-0000-000000000011', 'Business English & Public Speaking', 'Huấn luyện viên tiếng Anh thương mại giúp học viên tự tin giao tiếp quốc tế.', 'California, USA', 'America/Los_Angeles', 2);

-- =====================================================================
-- 3. DANH MỤC (CATEGORIES) & CHỦ ĐỀ (TOPICS)
-- Đảm bảo đủ 6 danh mục chính
-- =====================================================================
INSERT INTO categories (id, name, slug, color, sort_order) VALUES
('10000000-0000-0000-0000-000000000001', 'Business', 'business', '#4F46E5', 1),
('10000000-0000-0000-0000-000000000002', 'Design',   'design',   '#EC4899', 2),
('10000000-0000-0000-0000-000000000003', 'Code',     'code',     '#10B981', 3),
('10000000-0000-0000-0000-000000000004', 'Writing',  'writing',  '#F59E0B', 4),
('10000000-0000-0000-0000-000000000005', 'Movie',    'movie',    '#8B5CF6', 5),
('10000000-0000-0000-0000-000000000006', 'Language', 'language', '#06B6D4', 6);

INSERT INTO topics (id, name, slug, is_hot) VALUES
('20000000-0000-0000-0000-000000000001', 'Java',             'java',             TRUE),
('20000000-0000-0000-0000-000000000002', 'React Native',     'react-native',     TRUE),
('20000000-0000-0000-0000-000000000003', 'UX/UI',            'ux-ui',            TRUE),
('20000000-0000-0000-0000-000000000004', 'Figma',            'figma',            TRUE),
('20000000-0000-0000-0000-000000000005', 'Growth Hacking',   'growth-hacking',   TRUE),
('20000000-0000-0000-0000-000000000006', 'Spring Boot',      'spring-boot',      TRUE),
('20000000-0000-0000-0000-000000000007', 'Premiere Pro',     'premiere-pro',     TRUE),
('20000000-0000-0000-0000-000000000008', 'Copywriting',      'copywriting',      TRUE),
('20000000-0000-0000-0000-000000000009', 'Business English', 'business-english', TRUE),
('20000000-0000-0000-0000-000000000010', 'Fullstack Web',    'fullstack-web',    FALSE);

-- =====================================================================
-- 4. KHÓA HỌC (COURSES) - 13 KHÓA ĐẦY ĐỦ CÁC DANH MỤC VÀ GIẢNG VIÊN
-- Hình ảnh Unsplash cao cấp & video giới thiệu chuẩn
-- =====================================================================
INSERT INTO courses (id, instructor_id, category_id, title, slug, description, level,
                     original_price, sale_price, total_duration_seconds, total_lessons, avg_rating, review_count, student_count, status, published_at, thumbnail_url, intro_video_url) VALUES

-- 1. UX Foundation (Sara Weise - Design)
('30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002',
 'UX Foundation: Nhập môn Thiết kế Trải nghiệm Người dùng', 'ux-foundation',
 'Nắm vững tư duy nghiên cứu người dùng, thiết kế wireframe, luồng tương tác và kiểm thử usability thực tế.', 'BEGINNER',
 799000, 499000, 1080, 4, 4.85, 48, 310, 'PUBLISHED', now() - interval '60 days',
 'https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4'),

-- 2. Figma Zero to Hero (Sara Weise - Design)
('30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002',
 'UI Design với Figma từ Zero đến Hero', 'ui-design-figma-zero-to-hero',
 'Học trọn bộ kỹ năng thiết kế UI bằng Figma: Auto-layout, Variants, Design System và Prototype chuyển động mượt mà.', 'INTERMEDIATE',
 850000, 450000, 1020, 4, 4.90, 62, 420, 'PUBLISHED', now() - interval '50 days',
 'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4'),

-- 3. React Native cơ bản đến nâng cao (Jenny Barry - Code)
('30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003',
 'React Native cơ bản đến nâng cao: Lập trình App iOS & Android', 'react-native-co-ban-den-nang-cao',
 'Xây dựng ứng dụng di động đa nền tảng hoàn chỉnh với Expo SDK, TypeScript, React Navigation và tích hợp REST API.', 'INTERMEDIATE',
 1200000, 899000, 1140, 4, 4.92, 75, 520, 'PUBLISHED', now() - interval '45 days',
 'https://images.unsplash.com/photo-1551650975-87deedd944c3?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4'),

-- 4. Spring Boot & PostgreSQL (Jenny Barry - Code)
('30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003',
 'Lập trình Backend chuyên sâu với Spring Boot 3 & PostgreSQL', 'spring-boot-backend-chuyen-sau',
 'Thiết kế RESTful API chuẩn quốc tế, bảo mật JWT, JPA Hibernate, Transaction Management và tối ưu hiệu năng cơ sở dữ liệu.', 'ADVANCED',
 1500000, 990000, 1110, 4, 4.88, 54, 340, 'PUBLISHED', now() - interval '40 days',
 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4'),

-- 5. Fullstack Next.js & Node.js (Jenny Barry - Code)
('30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003',
 'Xây dựng Web App Hiện đại với Next.js & Node.js', 'fullstack-nextjs-nodejs',
 'Trang bị kiến thức xây dựng web app chuẩn sản xuất: Server Components, SSR/SSG, Prisma ORM và triển khai đám mây.', 'INTERMEDIATE',
 1100000, 699000, 960, 4, 4.80, 39, 280, 'PUBLISHED', now() - interval '25 days',
 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4'),

-- 6. Digital Marketing & Growth Hacking (Alex Morgan - Business)
('30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000001',
 'Digital Marketing & Growth Hacking 2026', 'digital-marketing-growth-hacking',
 'Chiến lược tăng trưởng khách hàng đột phá: Facebook Ads, Google Ads, SEO Onpage/Offpage và phân tích chỉ số GA4.', 'BEGINNER',
 990000, 590000, 1050, 4, 4.82, 42, 360, 'PUBLISHED', now() - interval '35 days',
 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4'),

-- 7. Quản trị Dự án Agile Scrum (Alex Morgan - Business)
('30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000001',
 'Quản trị Dự án Phần mềm Chuẩn Agile & Scrum', 'quan-tri-du-an-agile-scrum',
 'Làm chủ quy trình phát triển Agile: Sprint Planning, Daily Standup, Retrospective và sử dụng Jira chuyên nghiệp.', 'INTERMEDIATE',
 890000, 490000, 930, 4, 4.78, 31, 215, 'PUBLISHED', now() - interval '20 days',
 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4'),

-- 8. Dựng phim Premiere Pro (David Miller - Movie)
('30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000005',
 'Dựng phim và Kỹ xảo Video với Premiere Pro', 'dung-phim-premiere-pro',
 'Kỹ thuật dựng phim chuẩn điện ảnh, biên tập âm thanh, chỉnh màu Lumetri Color và xuất file chuẩn đa nền tảng.', 'BEGINNER',
 1100000, 690000, 1080, 4, 4.89, 58, 410, 'PUBLISHED', now() - interval '30 days',
 'https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4'),

-- 9. Motion Graphics After Effects (David Miller - Movie)
('30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000005',
 'Motion Graphics & Hoạt họa 2D với After Effects', 'motion-graphics-after-effects',
 'Sáng tạo đồ họa động bắt mắt: Typography Animation, Logo Intro, Keyframing và kỹ thuật Tracking chân thực.', 'INTERMEDIATE',
 1250000, 790000, 1020, 4, 4.86, 35, 230, 'PUBLISHED', now() - interval '18 days',
 'https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4'),

-- 10. Kỹ năng Viết lách (Emily Watson - Writing)
('30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000004',
 'Kỹ năng Viết lách & Sáng tạo Nội dung Thu hút', 'ky-nang-viet-lach-sang-tao-noi-dung',
 'Nghệ thuật kể chuyện (Storytelling), xây dựng văn phong cá nhân và sáng tạo bài viết chạm tới cảm xúc độc giả.', 'BEGINNER',
 650000, 350000, 960, 4, 4.75, 46, 380, 'PUBLISHED', now() - interval '28 days',
 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4'),

-- 11. Copywriting Thuyết phục (Emily Watson - Writing)
('30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000004',
 'Copywriting Đỉnh cao: Nghệ thuật Thuyết phục Khách hàng', 'copywriting-dinh-cao',
 'Học công thức viết tiêu đề gây tò mò, lời kêu gọi hành động CTA sắc sảo và viết trang đích (Landing Page) ra đơn.', 'INTERMEDIATE',
 800000, 450000, 990, 4, 4.83, 29, 210, 'PUBLISHED', now() - interval '15 days',
 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4'),

-- 12. Tiếng Anh Giao tiếp Công sở (Michael Chen - Language)
('30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000006',
 'Tiếng Anh Giao tiếp Công sở Tự tin trong 30 Ngày', 'tieng-anh-giao-tiep-cong-so',
 'Phản xạ tiếng Anh tự nhiên trong họp hành, viết email đàm phán, thuyết trình và thảo luận công việc cùng đồng nghiệp nước ngoài.', 'BEGINNER',
 800000, 420000, 1020, 4, 4.81, 52, 450, 'PUBLISHED', now() - interval '22 days',
 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4'),

-- 13. Luyện Phát âm Chuẩn Mỹ & Thuyết trình (Michael Chen - Language)
('30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000006',
 'Luyện Phát âm Chuẩn Mỹ & Thuyết trình Chuyên nghiệp', 'phat-am-chuan-my-thuyet-trinh',
 'Sửa dứt điểm lỗi ngữ âm, nối âm, nhấn trọng âm và ngữ điệu giúp bài nói tự tin, chuẩn xác như người bản xứ.', 'INTERMEDIATE',
 890000, 490000, 960, 4, 4.87, 38, 290, 'PUBLISHED', now() - interval '12 days',
 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800&auto=format&fit=crop&q=80',
 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4');

-- =====================================================================
-- 5. CHỦ ĐỀ KHÓA HỌC (COURSE_TOPICS)
-- =====================================================================
INSERT INTO course_topics (course_id, topic_id) VALUES
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000004'),
('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004'),
('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000003'),
('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002'),
('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000001'),
('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000006'),
('30000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000010'),
('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000005'),
('30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000007'),
('30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000008'),
('30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000009');

-- =====================================================================
-- 6. LỢI ÍCH KHÓA HỌC (COURSE_BENEFITS)
-- =====================================================================
INSERT INTO course_benefits (course_id, content, sort_order) VALUES
('30000000-0000-0000-0000-000000000001', '14 giờ video bài giảng chất lượng cao', 1),
('30000000-0000-0000-0000-000000000001', 'Tài liệu thực hành Figma & UI Kit miễn phí', 2),
('30000000-0000-0000-0000-000000000001', 'Cấp chứng chỉ tốt nghiệp từ chuyên gia', 3),

('30000000-0000-0000-0000-000000000004', 'Thiết kế trực tiếp 3 dự án App & Web thực tế', 1),
('30000000-0000-0000-0000-000000000004', 'Truy cập trọn đời kho tài nguyên Design System', 2),

('30000000-0000-0000-0000-000000000002', 'Xây dựng 2 ứng dụng di động hoàn chỉnh đưa lên Store', 1),
('30000000-0000-0000-0000-000000000002', 'Source code GitHub cập nhật liên tục', 2),
('30000000-0000-0000-0000-000000000002', 'Hỗ trợ giải đáp thắc mắc lập trình 24/7', 3),

('30000000-0000-0000-0000-000000000005', 'Kiến trúc chuẩn Microservices thực chiến doanh nghiệp', 1),
('30000000-0000-0000-0000-000000000005', 'Kỹ thuật tối ưu hóa Database hàng triệu bản ghi', 2),

('30000000-0000-0000-0000-000000000003', 'Chiến lược chạy Ads thực tế ngân sách lớn', 1),
('30000000-0000-0000-0000-000000000003', 'File mẫu kế hoạch Marketing và bảng theo dõi KPI', 2),

('30000000-0000-0000-0000-000000000007', 'Bộ Preset màu điện ảnh độc quyền trị giá 50$', 1),
('30000000-0000-0000-0000-000000000007', 'Tài liệu âm thanh hiệu ứng SFX bản quyền', 2),

('30000000-0000-0000-0000-000000000008', 'Tự tin đàm phán và thuyết trình với đối tác quốc tế', 1),
('30000000-0000-0000-0000-000000000008', 'File mẫu 100+ email công việc chuyên nghiệp', 2);

-- =====================================================================
-- 7. CHƯƠNG MỤC (SECTIONS)
-- =====================================================================
INSERT INTO sections (id, course_id, title, order_index) VALUES
-- UX Foundation
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'Chương 1: Tổng quan về Thiết kế UX & Tâm lý học Người dùng', 1),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'Chương 2: Quy trình Nghiên cứu & Phỏng vấn Người dùng', 2),

-- Figma
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000004', 'Chương 1: Làm chủ Giao diện & Auto-Layout trong Figma', 1),
('40000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000004', 'Chương 2: Thiết kế Hệ thống Components & Prototype', 2),

-- React Native
('40000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000002', 'Chương 1: Cài đặt Môi trường Expo & Flexbox Layout', 1),
('40000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000002', 'Chương 2: Xử lý State, Context API & Tích hợp REST API', 2),

-- Spring Boot
('40000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000005', 'Chương 1: Kiến trúc Spring Boot 3 & Thiết kế RESTful API', 1),
('40000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000005', 'Chương 2: Spring Data JPA, PostgreSQL & Bảo mật JWT', 2),

-- Fullstack Web
('40000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000012', 'Chương 1: Khởi tạo Project Next.js & Server Components', 1),
('40000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000012', 'Chương 2: Xây dựng Backend API & Tích hợp Prisma ORM', 2),

-- Digital Marketing
('40000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000003', 'Chương 1: Tư duy Phễu Chuyển đổi & Xây dựng Chân dung Khách hàng', 1),
('40000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000003', 'Chương 2: Tối ưu Quảng cáo Đa kênh & Phân tích Chỉ số ROI', 2),

-- Agile Scrum
('40000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000013', 'Chương 1: Nguyên lý Cốt lõi của Khung làm việc Scrum', 1),
('40000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000013', 'Chương 2: Vận hành Sprint và Quản lý Backlog trên Jira', 2),

-- Premiere Pro
('40000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000007', 'Chương 1: Giao diện Làm việc & Kỹ thuật Cắt ghép Nhịp điệu', 1),
('40000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000007', 'Chương 2: Chỉnh màu Lumetri Color & Xử lý Âm thanh', 2),

-- After Effects
('40000000-0000-0000-0000-000000000017', '30000000-0000-0000-0000-000000000014', 'Chương 1: Cơ bản về Keyframe & Chuyển động Đồ họa', 1),
('40000000-0000-0000-0000-000000000018', '30000000-0000-0000-0000-000000000014', 'Chương 2: Hiệu ứng Visual Effects & Render Chuẩn HD', 2),

-- Viết lách
('40000000-0000-0000-0000-000000000019', '30000000-0000-0000-0000-000000000006', 'Chương 1: Tìm kiếm Ý tưởng & Khơi nguồn Cảm hứng Viết', 1),
('40000000-0000-0000-0000-000000000020', '30000000-0000-0000-0000-000000000006', 'Chương 2: Kỹ thuật Kể chuyện Storytelling Chạm Cảm xúc', 2),

-- Copywriting
('40000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000015', 'Chương 1: Công thức Giật tít Tiêu đề & Thu hút Chú ý', 1),
('40000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000015', 'Chương 2: Viết Bài Bán hàng Thuyết phục & Lời kêu gọi CTA', 2),

-- Tiếng Anh Công sở
('40000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000008', 'Chương 1: Giao tiếp Tự tin trong Phỏng vấn & Họp Doanh nghiệp', 1),
('40000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000008', 'Chương 2: Viết Email Thương mại & Đàm phán Hợp đồng', 2),

-- Phát âm Chuẩn Mỹ
('40000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000016', 'Chương 1: Khẩu hình Miệng & Bảng Ký hiệu Ngữ âm Quốc tế IPA', 1),
('40000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000016', 'Chương 2: Quy tắc Nhấn Trọng âm, Nối âm & Ngữ điệu Tự nhiên', 2);

-- =====================================================================
-- 8. BÀI GIẢNG (LESSONS)
-- Thời lượng từ 180 đến 300 giây (3 đến 5 phút)
-- Video URLs: Google Cloud Storage CDN streams chạy thật 100%
-- is_preview = TRUE cho bài 1 (Học thử miễn phí)
-- =====================================================================
INSERT INTO lessons (section_id, title, video_url, duration_seconds, order_index, is_preview) VALUES
-- UX Foundation (4 bài, mỗi bài 3-5 phút)
('40000000-0000-0000-0000-000000000001', '1. Giới thiệu tổng quan về UX và vai trò của UX Designer', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 240, 1, TRUE),
('40000000-0000-0000-0000-000000000001', '2. Phân biệt UI và UX: Điểm chạm và trải nghiệm cảm xúc', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 280, 2, FALSE),
('40000000-0000-0000-0000-000000000002', '3. Phương pháp phỏng vấn người dùng và lập User Persona', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 270, 1, FALSE),
('40000000-0000-0000-0000-000000000002', '4. Xây dựng sơ đồ hành trình người dùng (User Journey Map)', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 290, 2, FALSE),

-- Figma Zero to Hero
('40000000-0000-0000-0000-000000000003', '1. Làm quen giao diện Figma, Canvas và phím tắt thông dụng', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 210, 1, TRUE),
('40000000-0000-0000-0000-000000000003', '2. Tinh chỉnh bố cục linh hoạt với Auto Layout chuyên sâu', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 270, 2, FALSE),
('40000000-0000-0000-0000-000000000004', '3. Quản lý Components, Properties và Variants hệ thống', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4', 255, 1, FALSE),
('40000000-0000-0000-0000-000000000004', '4. Tạo Prototype tương tác chuyển động Smart Animate', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 285, 2, FALSE),

-- React Native
('40000000-0000-0000-0000-000000000005', '1. Cài đặt môi trường phát triển Expo & TypeScript chuẩn', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 240, 1, TRUE),
('40000000-0000-0000-0000-000000000005', '2. Thiết kế giao diện di động với Flexbox và StyleSheet', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 300, 2, FALSE),
('40000000-0000-0000-0000-000000000006', '3. Quản lý State với useState, useEffect và Context API', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 290, 1, FALSE),
('40000000-0000-0000-0000-000000000006', '4. Kết nối REST API Backend và xử lý dữ liệu bất đồng bộ', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4', 310, 2, FALSE),

-- Spring Boot
('40000000-0000-0000-0000-000000000007', '1. Tổng quan cấu trúc dự án Spring Boot 3 & Maven', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 250, 1, TRUE),
('40000000-0000-0000-0000-000000000007', '2. Thiết kế tầng Controller, Service và DTO Pattern chuẩn', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 280, 2, FALSE),
('40000000-0000-0000-0000-000000000008', '3. Thao tác Cơ sở dữ liệu với Spring Data JPA & PostgreSQL', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4', 300, 1, FALSE),
('40000000-0000-0000-0000-000000000008', '4. Xác thực và phân quyền người dùng với Spring Security & JWT', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 280, 2, FALSE),

-- Fullstack Web Next.js
('40000000-0000-0000-0000-000000000009', '1. Khởi tạo ứng dụng Next.js với App Router hiện đại', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 210, 1, TRUE),
('40000000-0000-0000-0000-000000000009', '2. Khai thác sức mạnh của React Server Components', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 250, 2, FALSE),
('40000000-0000-0000-0000-000000000010', '3. Tạo Route Handlers và kết nối Database qua Prisma', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 240, 1, FALSE),
('40000000-0000-0000-0000-000000000010', '4. Tối ưu SEO, hiệu năng Web Vitals và Deploy Vercel', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 260, 2, FALSE),

-- Digital Marketing
('40000000-0000-0000-0000-000000000011', '1. Thiết lập tư duy Growth Hacking và phễu chuyển đổi AARRR', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 240, 1, TRUE),
('40000000-0000-0000-0000-000000000011', '2. Nghiên cứu từ khóa SEO và xây dựng kế hoạch nội dung', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4', 260, 2, FALSE),
('40000000-0000-0000-0000-000000000012', '3. Thiết lập chiến dịch quảng cáo Facebook & Google hiệu quả', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 270, 1, FALSE),
('40000000-0000-0000-0000-000000000012', '4. Đo lường tỷ lệ chuyển đổi và đọc báo cáo Google Analytics 4', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 280, 2, FALSE),

-- Agile Scrum
('40000000-0000-0000-0000-000000000013', '1. Giới thiệu các vai trò trong Scrum: PO, Scrum Master, Dev', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4', 210, 1, TRUE),
('40000000-0000-0000-0000-000000000013', '2. Cách viết User Story chuẩn INVEST và ước lượng Story Points', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 230, 2, FALSE),
('40000000-0000-0000-0000-000000000014', '3. Tổ chức các buổi họp Sprint Planning và Daily Scrum', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4', 240, 1, FALSE),
('40000000-0000-0000-0000-000000000014', '4. Đánh giá chất lượng với Sprint Review và Retrospective', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 250, 2, FALSE),

-- Premiere Pro
('40000000-0000-0000-0000-000000000015', '1. Khởi tạo Project, quản lý Timeline và các công cụ cắt ghép', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4', 240, 1, TRUE),
('40000000-0000-0000-0000-000000000015', '2. Kỹ thuật chuyển cảnh (Transitions) mượt mà và cuốn hút', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 280, 2, FALSE),
('40000000-0000-0000-0000-000000000016', '3. Chỉnh màu Lumetri Color và áp dụng LUT điện ảnh', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 270, 1, FALSE),
('40000000-0000-0000-0000-000000000016', '4. Xử lý âm thanh, lọc tạp âm và xuất video 4K chất lượng', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 290, 2, FALSE),

-- After Effects
('40000000-0000-0000-0000-000000000017', '1. Nguyên lý diễn hoạt chuyển động với đồ thị Graph Editor', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 230, 1, TRUE),
('40000000-0000-0000-0000-000000000017', '2. Tạo chuyển động Kinetic Typography cho tiêu đề video', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 270, 2, FALSE),
('40000000-0000-0000-0000-000000000018', '3. Kỹ thuật Camera Tracking và tách nền phông xanh', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 250, 1, FALSE),
('40000000-0000-0000-0000-000000000018', '4. Render video tối ưu dung lượng với Adobe Media Encoder', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 270, 2, FALSE),

-- Kỹ năng Viết lách
('40000000-0000-0000-0000-000000000019', '1. Xóa bỏ rào cản sợ viết và xây dựng thói quen viết mỗi ngày', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4', 210, 1, TRUE),
('40000000-0000-0000-0000-000000000019', '2. Nghệ thuật sử dụng ngôn từ gợi hình và gợi cảm xúc', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 240, 2, FALSE),
('40000000-0000-0000-0000-000000000020', '3. Cấu trúc bài viết mạch lạc theo quy tắc tam giác ngược', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 250, 1, FALSE),
('40000000-0000-0000-0000-000000000020', '4. Chỉnh sửa và trau chuốt bài viết trước khi xuất bản', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 260, 2, FALSE),

-- Copywriting Thuyết phục
('40000000-0000-0000-0000-000000000021', '1. Phân biệt người viết nội dung và chuyên gia Copywriter', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 210, 1, TRUE),
('40000000-0000-0000-0000-000000000021', '2. 10 công thức viết tiêu đề thôi miên người đọc lập tức', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 260, 2, FALSE),
('40000000-0000-0000-0000-000000000022', '3. Kỹ thuật đào sâu "nỗi đau" khách hàng bằng mô hình PAS', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 250, 1, FALSE),
('40000000-0000-0000-0000-000000000022', '4. Tạo lời kêu gọi hành động Call-to-Action thúc đẩy chốt đơn', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 270, 2, FALSE),

-- Tiếng Anh Công sở
('40000000-0000-0000-0000-000000000023', '1. Giới thiệu bản thân và gây ấn tượng mạnh trong buổi phỏng vấn', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 240, 1, TRUE),
('40000000-0000-0000-0000-000000000023', '2. Các mẫu câu thông dụng khi tham gia cuộc họp quốc tế', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 260, 2, FALSE),
('40000000-0000-0000-0000-000000000024', '3. Quy chuẩn viết Email chuyên nghiệp và phản hồi lịch sự', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4', 250, 1, FALSE),
('40000000-0000-0000-0000-000000000024', '4. Kỹ năng đàm phán giải quyết bất đồng quan điểm công việc', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackSeeTheWorld.mp4', 270, 2, FALSE),

-- Phát âm Chuẩn Mỹ
('40000000-0000-0000-0000-000000000025', '1. Cách đặt khẩu hình miệng cho các nguyên âm cơ bản', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 220, 1, TRUE),
('40000000-0000-0000-0000-000000000025', '2. Phân biệt các cặp phụ âm dễ nhầm lẫn như /s/, /ʃ/, /θ/, /ð/', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 240, 2, FALSE),
('40000000-0000-0000-0000-000000000026', '3. Bí quyết nối âm và nuốt âm tự nhiên như người bản xứ', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 250, 1, FALSE),
('40000000-0000-0000-0000-000000000026', '4. Ngữ điệu lên xuống và nhịp điệu khi thuyết trình trước đám đông', 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 250, 2, FALSE);

-- =====================================================================
-- 9. TÀI LIỆU KHÓA HỌC (COURSE_DOCUMENTS)
-- Đúng chuẩn tài liệu thực tế của từng môn
-- =====================================================================
INSERT INTO course_documents (course_id, file_name, file_url, file_size, file_type) VALUES
('30000000-0000-0000-0000-000000000001', 'Giao_trinh_UX_Research_Co_ban.pdf', 'https://example.com/docs/ux-research-guide.pdf', 2450000, 'pdf'),
('30000000-0000-0000-0000-000000000001', 'Bieu_mau_Khao_sat_Nguoi_dung.docx', 'https://example.com/docs/user-survey-template.docx', 850000, 'docx'),

('30000000-0000-0000-0000-000000000004', 'File_Figma_Design_System_Template.fig', 'https://example.com/docs/design-system.fig', 8900000, 'fig'),
('30000000-0000-0000-0000-000000000004', 'Bo_Icon_Vectors_UI_Kit_2026.zip', 'https://example.com/docs/ui-kit-icons.zip', 12500000, 'zip'),

('30000000-0000-0000-0000-000000000002', 'Source_Code_React_Native_Elearning.zip', 'https://example.com/docs/rn-elearning-src.zip', 15800000, 'zip'),
('30000000-0000-0000-0000-000000000002', 'Huong_dan_Setup_Expo_TypeScript.pdf', 'https://example.com/docs/setup-expo-ts.pdf', 1900000, 'pdf'),

('30000000-0000-0000-0000-000000000005', 'Cheatsheet_Spring_Boot_Postgres.pdf', 'https://example.com/docs/spring-boot-cheatsheet.pdf', 1850000, 'pdf'),
('30000000-0000-0000-0000-000000000005', 'Kien_truc_Microservices_Clean_Arch.pdf', 'https://example.com/docs/microservices-arch.pdf', 3200000, 'pdf'),

('30000000-0000-0000-0000-000000000003', 'Ke_hoach_Marketing_Plan_2026.xlsx', 'https://example.com/docs/marketing-plan-template.xlsx', 3150000, 'xlsx'),
('30000000-0000-0000-0000-000000000003', 'Checklist_Chay_Ads_Khong_Bi_Khoa.pdf', 'https://example.com/docs/ads-checklist.pdf', 1400000, 'pdf'),

('30000000-0000-0000-0000-000000000007', 'Preset_Mau_Lumetri_Premiere_Pro.prfpset', 'https://example.com/docs/lumetri-cinematic.prfpset', 4600000, 'prfpset'),
('30000000-0000-0000-0000-000000000007', 'Bo_Hieu_ung_Am_thanh_SFX_Ban_quyen.zip', 'https://example.com/docs/sfx-pack.zip', 28500000, 'zip'),

('30000000-0000-0000-0000-000000000006', 'So_tay_Viet_Lach_Sang_Tao_Content.pdf', 'https://example.com/docs/content-handbook.pdf', 2100000, 'pdf'),
('30000000-0000-0000-0000-000000000008', 'Tong_hop_300_Cum_tu_Tieng_Anh_Cong_so.pdf', 'https://example.com/docs/business-english-300.pdf', 5200000, 'pdf'),
('30000000-0000-0000-0000-000000000008', 'Mau_Email_Giao_dich_Quoc_te.docx', 'https://example.com/docs/email-templates.docx', 1200000, 'docx');

-- =====================================================================
-- 10. DỰ ÁN KHÓA HỌC (COURSE_PROJECTS) & BÀI NỘP (SUBMISSIONS)
-- =====================================================================
INSERT INTO course_projects (course_id, description) VALUES
('30000000-0000-0000-0000-000000000001', 'Thiết kế lại luồng Onboarding và trang chủ của một ứng dụng bạn yêu thích trên thiết bị di động.'),
('30000000-0000-0000-0000-000000000004', 'Xây dựng hoàn chỉnh một ứng dụng Food Delivery với đầy đủ UI Kit và Component Variant trên Figma.'),
('30000000-0000-0000-0000-000000000002', 'Lập trình ứng dụng E-Commerce bằng React Native Expo, kết nối API giỏ hàng và danh mục sản phẩm.'),
('30000000-0000-0000-0000-000000000005', 'Xây dựng hệ thống Backend Quản lý Khóa học hoàn chỉnh với Spring Boot 3, Spring Security JWT và PostgreSQL.'),
('30000000-0000-0000-0000-000000000003', 'Lập kế hoạch Digital Marketing ra mắt sản phẩm mới với ngân sách 50 triệu và mục tiêu 500 khách hàng đầu tiên.'),
('30000000-0000-0000-0000-000000000007', 'Dựng một video giới thiệu sản phẩm 60 giây hoàn chỉnh với chuyển cảnh nhịp điệu và chỉnh màu Lumetri.'),
('30000000-0000-0000-0000-000000000008', 'Quay video 3 phút thuyết trình bằng tiếng Anh về một giải pháp kinh doanh bạn đề xuất cho công ty.');

-- Bài nộp dự án của học viên
INSERT INTO project_submissions (id, course_id, user_id, title, description, file_url, file_name, file_type, file_size, thumbnail_url, feedback, feedback_at) VALUES
('60000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004',
 'Thiết kế Onboarding App Du Lịch Wanderlust', 'Em đã áp dụng các bước nghiên cứu Persona và thiết kế giao diện đơn giản, tinh tế.',
 'https://example.com/submissions/wanderlust-ux.fig', 'wanderlust-ux.fig', 'fig', 4500000,
 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400&auto=format&fit=crop&q=80',
 'Bài làm rất tốt, phân tích hành trình người dùng rõ ràng. Điểm cộng cho phần Wireframe chi tiết!', now() - interval '10 days'),

('60000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
 'Ứng dụng Đọc Sách BookWorm với React Native', 'Ứng dụng hỗ trợ đọc sách ban đêm, lưu trang yêu thích và kết nối API sách.',
 'https://example.com/submissions/bookworm-app.zip', 'bookworm-app.zip', 'zip', 12300000,
 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&auto=format&fit=crop&q=80',
 'Code sạch, phân chia component rõ ràng và mượt mà trên cả iOS lẫn Android!', now() - interval '5 days');

-- =====================================================================
-- 11. ĐĂNG KÝ KHÓA HỌC (ENROLLMENTS) & CHỨNG CHỈ (CERTIFICATES)
-- Cho 3-4 học viên mẫu
-- =====================================================================
INSERT INTO enrollments (id, user_id, course_id, progress_percentage, status, enrolled_at, completed_at, last_accessed_at) VALUES
-- Nguyễn Văn Học (Đang học và hoàn thành)
('70000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001',
 100.00, 'COMPLETED', now() - interval '30 days', now() - interval '12 days', now() - interval '1 day'),

('70000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002',
 75.00, 'ONGOING', now() - interval '20 days', NULL, now() - interval '2 hours'),

('70000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000004',
 40.00, 'ONGOING', now() - interval '10 days', NULL, now() - interval '1 day'),

-- Trần Thị Mai
('70000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000003',
 100.00, 'COMPLETED', now() - interval '25 days', now() - interval '5 days', now() - interval '3 days'),

-- Lê Hoàng Nam
('70000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000005',
 85.00, 'ONGOING', now() - interval '15 days', NULL, now() - interval '4 hours'),

-- Phạm Thu Hà
('70000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000007',
 60.00, 'ONGOING', now() - interval '12 days', NULL, now() - interval '1 day');

-- Chứng chỉ hoàn thành cấp cho học viên đạt 100%
INSERT INTO certificates (enrollment_id, certificate_code, file_url, issued_at) VALUES
('70000000-0000-0000-0000-000000000001', 'CERT-UX-2026-9041', 'https://example.com/certs/cert-ux-9041.pdf', now() - interval '12 days'),
('70000000-0000-0000-0000-000000000004', 'CERT-MKT-2026-8812', 'https://example.com/certs/cert-mkt-8812.pdf', now() - interval '5 days');

-- =====================================================================
-- 12. ĐÁNH GIÁ (REVIEWS)
-- Ràng buộc với enrollment_id thực tế
-- =====================================================================
INSERT INTO reviews (enrollment_id, course_id, user_id, rating, comment, created_at) VALUES
('70000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004',
 5, 'Khóa học tuyệt vời! Cô Sara Weise giảng giải rất dễ hiểu, giúp em định hình được tư duy làm sản phẩm từ đầu.', now() - interval '10 days'),

('70000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000008',
 5, 'Kiến thức Growth Hacking rất thực chiến, học xong áp dụng được ngay vào các chiến dịch quảng cáo công ty.', now() - interval '4 days'),

('70000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000009',
 5, 'Thầy giảng rất sâu về Transaction và Clean Architecture, giải quyết được nhiều vướng mắc trong công việc của mình.', now() - interval '2 days'),

('70000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000010',
 4, 'Bài tập thực hành rất hay, video hướng dẫn chi tiết từng thao tác cắt ghép và chỉnh màu điện ảnh.', now() - interval '1 day');

-- =====================================================================
-- 13. HỎI ĐÁP (QA_POSTS) & TRẢ LỜI CỦA GIẢNG VIÊN (QA_COMMENTS)
-- =====================================================================
INSERT INTO qa_posts (id, course_id, user_id, content, like_count, comment_count, is_answered, created_at) VALUES
('80000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004',
 'Cô Sara ơi, khi làm khảo sát người dùng thì số lượng mẫu tối thiểu khoảng bao nhiêu là đạt độ tin cậy ạ?', 8, 1, TRUE, now() - interval '8 days'),

('80000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
 'Khi build Expo lên điện thoại Android thật thì cần lưu ý những cấu hình nào về mạng LAN vậy cô?', 12, 1, TRUE, now() - interval '3 days'),

('80000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000009',
 'Nên dùng UUID hay Long ID làm khóa chính cho bảng đơn hàng để tối ưu tốc độ đánh index vậy cô?', 6, 1, TRUE, now() - interval '2 days');

INSERT INTO qa_comments (post_id, user_id, content, is_instructor_answer, created_at) VALUES
('80000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002',
 'Chào em! Với nghiên cứu định tính (Usability Test), chỉ cần phỏng vấn sâu 5-8 người là có thể phát hiện được 85% lỗi trải nghiệm. Còn với khảo sát số liệu định lượng, em nên có từ 50-100 phản hồi nhé!', TRUE, now() - interval '7 days'),

('80000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003',
 'Chào Học! Em chỉ cần đảm bảo máy tính và điện thoại cùng kết nối vào một mạng Wi-Fi và trỏ API_URL vào IP LAN của máy tính (ví dụ http://192.168.1.x:8080/api/v1) là chạy ngon lành nhé!', TRUE, now() - interval '2 days'),

('80000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003',
 'Với hệ thống phân tán microservices hoặc bảo mật không để lộ số lượng đơn, UUID v7 hoặc v4 là lựa chọn chuẩn. Nếu cần tốc độ chèn cực nhanh theo thứ tự thời gian, em có thể dùng BIGSERIAL hoặc UUID v7 nhé!', TRUE, now() - interval '1 day');

-- =====================================================================
-- 14. GIỎ HÀNG (CARTS, CART_ITEMS) & KHÓA HỌC ĐÃ LƯU (SAVED_COURSES)
-- =====================================================================
INSERT INTO carts (user_id) VALUES
('00000000-0000-0000-0000-000000000004'),
('00000000-0000-0000-0000-000000000008'),
('00000000-0000-0000-0000-000000000009');

-- Giỏ hàng của học viên Nguyễn Văn Học có sẵn khóa học chưa mua
INSERT INTO cart_items (cart_id, course_id, price_at_added)
SELECT c.id, '30000000-0000-0000-0000-000000000005', 990000
FROM carts c WHERE c.user_id = '00000000-0000-0000-0000-000000000004';

INSERT INTO cart_items (cart_id, course_id, price_at_added)
SELECT c.id, '30000000-0000-0000-0000-000000000007', 690000
FROM carts c WHERE c.user_id = '00000000-0000-0000-0000-000000000004';

-- Khóa học yêu thích đã lưu
INSERT INTO saved_courses (user_id, course_id) VALUES
('00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000005'),
('00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000007'),
('00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000001');

-- Yêu thích (Likes)
INSERT INTO course_likes (user_id, course_id) VALUES
('00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001'),
('00000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000002'),
('00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000003');

-- =====================================================================
-- 15. MÃ GIẢM GIÁ (COUPONS) & BANNER QUẢNG CÁO
-- =====================================================================
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_value, max_discount_amount, usage_limit, start_at, end_at) VALUES
('WELCOME20', 'Giảm ngay 20% cho học viên mới đăng ký', 'PERCENTAGE', 20, 200000, 300000, 500, now(), now() + interval '180 days'),
('FLASH50',   'Giảm 50% đặc biệt trong tuần lễ khai giảng', 'PERCENTAGE', 50, 500000, 500000, 200, now(), now() + interval '30 days'),
('GIAM50K',   'Giảm trực tiếp 50.000đ cho đơn hàng từ 300k', 'FIXED_AMOUNT', 50000, 300000, NULL, NULL, now(), now() + interval '90 days');

INSERT INTO banners (title, subtitle, image_url, action_label, link_type, link_value, position, sort_order) VALUES
('Tuần lễ Khởi nghiệp & Kỹ năng số 2026', 'Ưu đãi lên đến 50% tất cả khóa học', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&auto=format&fit=crop&q=80', 'KHÁM PHÁ NGAY', 'CATEGORY', 'code', 'HOME', 1),
('Học tập mọi lúc, mọi nơi trên thiết bị di động', 'Cấp chứng chỉ tốt nghiệp chuẩn quốc tế', 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=800&auto=format&fit=crop&q=80', 'BẮT ĐẦU HỌC', 'NONE', NULL, 'MY_COURSES', 1);

-- =====================================================================
-- 16. THÔNG BÁO (NOTIFICATIONS)
-- =====================================================================
INSERT INTO notifications (user_id, title, content, type, is_read, created_at) VALUES
('00000000-0000-0000-0000-000000000004', 'Chào mừng bạn đến với E-Learning! 🎉', 'Khám phá hơn 13 khóa học chất lượng cao từ các chuyên gia hàng đầu.', 'SYSTEM', FALSE, now() - interval '2 days'),
('00000000-0000-0000-0000-000000000004', 'Mã ưu đãi WELCOME20 dành cho bạn', 'Sử dụng mã WELCOME20 để được giảm 20% cho đơn hàng khóa học đầu tiên.', 'PROMOTION', FALSE, now() - interval '1 day'),
('00000000-0000-0000-0000-000000000004', 'Chúc mừng bạn đã hoàn thành khóa học UX Foundation', 'Chứng chỉ tốt nghiệp của bạn đã sẵn sàng trong mục Hồ sơ.', 'CERTIFICATE', TRUE, now() - interval '12 days');


-- =====================================================================
-- BỔ SUNG NGƯỜI DÙNG HỌC VIÊN MỚI
-- =====================================================================
INSERT INTO users (id, full_name, email, password_hash, role, status, job_title, avatar_url, bio, email_verified_at) VALUES
('00000000-0000-0000-0000-000000000021', 'Hoàng Đức Anh', 'duc.anh@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Sinh viên CNTT', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80', 'Đam mê lập trình di động và web fullstack.', now()),
('00000000-0000-0000-0000-000000000022', 'Vũ Thị Linh', 'linh.vu@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Graphic Designer', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80', 'Yêu thích thiết kế đồ họa, UI/UX và animation.', now()),
('00000000-0000-0000-0000-000000000023', 'Bùi Minh Quân', 'quan.bui@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Marketing Executive', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80', 'Chuyên viên Digital Marketing và tối ưu phễu chuyển đổi.', now()),
('00000000-0000-0000-0000-000000000024', 'Đặng Thảo My', 'my.dang@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Product Manager', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80', 'Quản lý phát triển sản phẩm công nghệ theo chuẩn Scrum.', now()),
('00000000-0000-0000-0000-000000000025', 'Đỗ Gia Huy', 'huy.do@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Frontend Developer', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=400&auto=format&fit=crop&q=80', 'Lập trình viên React, Next.js và ứng dụng đa nền tảng.', now()),
('00000000-0000-0000-0000-000000000026', 'Nguyễn Hải Yến', 'yen.nguyen@elearning.local', '$2a$10$KPlEvdcJ54ioRmxJfPPvyu4qqXZp94fvvwyFzA.BV6GikdIOO7t32', 'STUDENT', 'ACTIVE', 'Content Creator & Copywriter', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80', 'Sáng tạo nội dung video ngắn, viết bài truyền thông thương hiệu.', now()) ON CONFLICT (id) DO NOTHING;

-- =====================================================================
-- BỔ SUNG ĐĂNG KÝ HỌC (ENROLLMENTS) CHO CÁC HỌC VIÊN ĐÁNH GIÁ
-- =====================================================================
INSERT INTO enrollments (id, user_id, course_id, progress_percentage, status, enrolled_at, completed_at, last_accessed_at) VALUES
('71000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000001', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000001', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000001', 100.00, 'COMPLETED', now() - interval '14 days', now() - interval '5 days', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000001', 100.00, 'COMPLETED', now() - interval '12 days', now() - interval '3 days', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000002', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000002', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000002', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000002', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000009', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000003', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000003', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000003', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000003', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000004', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000004', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000004', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000004', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000017', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000005', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000018', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000005', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000019', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000005', 100.00, 'COMPLETED', now() - interval '14 days', now() - interval '5 days', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000020', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000005', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000021', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000006', 100.00, 'COMPLETED', now() - interval '19 days', now() - interval '10 days', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000022', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000006', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000023', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000006', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000024', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000006', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000025', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000006', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000026', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000007', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000027', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000007', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000028', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000007', 100.00, 'COMPLETED', now() - interval '14 days', now() - interval '5 days', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000029', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000007', 100.00, 'COMPLETED', now() - interval '12 days', now() - interval '3 days', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000030', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000008', 100.00, 'COMPLETED', now() - interval '19 days', now() - interval '10 days', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000008', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000032', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000008', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000033', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000008', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000034', '00000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000008', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000035', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000012', 100.00, 'COMPLETED', now() - interval '20 days', now() - interval '11 days', now() - interval '10 days'),
('71000000-0000-0000-0000-000000000036', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000012', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000037', '00000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000012', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000038', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000012', 100.00, 'COMPLETED', now() - interval '14 days', now() - interval '5 days', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000039', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000012', 100.00, 'COMPLETED', now() - interval '12 days', now() - interval '3 days', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000040', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000013', 100.00, 'COMPLETED', now() - interval '19 days', now() - interval '10 days', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000041', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000013', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000042', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000013', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000043', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000013', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000044', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000013', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000045', '00000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000014', 100.00, 'COMPLETED', now() - interval '20 days', now() - interval '11 days', now() - interval '10 days'),
('71000000-0000-0000-0000-000000000046', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000014', 100.00, 'COMPLETED', now() - interval '18 days', now() - interval '9 days', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000047', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000014', 100.00, 'COMPLETED', now() - interval '16 days', now() - interval '7 days', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000048', '00000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000014', 100.00, 'COMPLETED', now() - interval '14 days', now() - interval '5 days', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000049', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000014', 100.00, 'COMPLETED', now() - interval '12 days', now() - interval '3 days', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000050', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000015', 100.00, 'COMPLETED', now() - interval '19 days', now() - interval '10 days', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000051', '00000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000015', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000052', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000015', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000053', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000015', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000054', '00000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000015', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000055', '00000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000016', 100.00, 'COMPLETED', now() - interval '19 days', now() - interval '10 days', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000056', '00000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000016', 100.00, 'COMPLETED', now() - interval '17 days', now() - interval '8 days', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000057', '00000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000016', 100.00, 'COMPLETED', now() - interval '15 days', now() - interval '6 days', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000058', '00000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000016', 100.00, 'COMPLETED', now() - interval '13 days', now() - interval '4 days', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000059', '00000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000016', 100.00, 'COMPLETED', now() - interval '11 days', now() - interval '2 days', now() - interval '1 days') ON CONFLICT (user_id, course_id) DO NOTHING;

-- =====================================================================
-- BỔ SUNG ĐÁNH GIÁ (REVIEWS) ĐẦY ĐỦ 3-5 SAO CHO 13 KHÓA HỌC
-- =====================================================================
INSERT INTO reviews (enrollment_id, course_id, user_id, rating, comment, created_at) VALUES
('71000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021', 5, 'Kiến thức về User Persona và Journey Mapping được truyền tải rất trực quan và có ví dụ thực tế.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000022', 4, 'Nội dung bài bản, logic. Nếu có thêm bài tập về thiết kế luồng thanh toán phức tạp thì sẽ còn tuyệt hơn.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000024', 4, 'Rất phù hợp cho người mới bắt đầu chuyển ngành sang UX. Video bài giảng chất lượng tốt.', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000025', 3, 'Khóa học tốt nhưng tốc độ giảng ở chương đầu hơi chậm một chút đối với người đã có nền tảng.', now() - interval '2 days'),
('70000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', 5, 'Khóa học quá chất lượng! Thầy Jenny Barry hướng dẫn từ cấu hình Expo đến xử lý State, Navigation cực kỳ chi tiết.', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000009', 5, 'Nhờ khóa học mà mình đã tự tay hoàn thành app thương mại điện tử đầu tiên và deploy lên thiết bị thật.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000021', 4, 'Giải thích rõ ràng về Hooks và Reanimated. Khóa học cập nhật phiên bản mới rất kịp thời.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000025', 4, 'Rất hữu ích cho ai muốn làm app đa nền tảng. Code mẫu sạch và chuẩn quy ước đặt tên.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000023', 3, 'Kiến thức nâng cao về tích hợp Native Modules hơi nhanh, mong thầy bổ sung thêm tài liệu đọc thêm.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000023', 5, 'Phần phân tích phễu AARRR và tối ưu chi phí chuyển đổi (CAC) quá hay, mở mang rất nhiều góc nhìn mới.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000024', 4, 'Chiến dịch mẫu chạy Ads rất thực tế. Thầy chia sẻ cả các case-study thất bại để tránh.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000026', 4, 'Giáo trình cập nhật xu hướng AI marketing mới nhất. Hỗ trợ Q&A rất nhiệt tình.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000010', 3, 'Phần Google Analytics 4 giải thích hơi dồn dập, người chưa từng dùng GA4 cần xem đi xem lại vài lần.', now() - interval '1 days'),
('70000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004', 5, 'Khóa học hay nhất về Figma mà mình từng học! Auto Layout và Component Variants được giải thích cực dễ hiểu.', now() - interval '10 days'),
('71000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000022', 5, 'Thiết kế Design System từ con số 0 rất chuyên nghiệp, giúp ích trực tiếp cho công việc tại công ty.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000008', 4, 'Giảng viên chia sẻ nhiều tips phím tắt và plugin cực hay giúp tiết kiệm 50% thời gian thiết kế.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000024', 4, 'Các bài thực hành bám sát sản phẩm mobile app thực tế. Rất đáng đồng tiền bát gạo.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000026', 3, 'Phần prototype animation nâng cao hơi ngắn, mong có thêm buổi workshop chuyên sâu về Smart Animate.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000017', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000021', 5, 'Cực kỳ ấn tượng với phần thiết kế database tối ưu indexing và xử lý concurrency trong PostgreSQL.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000018', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000025', 4, 'Hướng dẫn triển khai Docker và viết Unit Test bài bản với JUnit 5 và Mockito.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000019', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000008', 4, 'Kiến thức bảo mật Spring Security và JWT chuẩn chỉ, giải thích bản chất không chỉ copy code.', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000020', '30000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000023', 3, 'Nội dung hơi nặng với người mới, đòi hỏi phải có nền tảng Java Core và SQL khá vững trước khi học.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000010', 5, 'Cách cô Alex hướng dẫn đặt tiêu đề giật tít và xây dựng mạch bài viết đọc là bị cuốn ngay!', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000026', 5, 'Khóa học giúp mình vượt qua nỗi sợ viết và xây dựng được kênh blog cá nhân có hơn 10k độc giả.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000008', 4, 'Nhiều công thức viết bài kinh điển như PAS, AIDA được hệ thống hóa rất bài bản.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000022', 4, 'Bài tập rèn luyện viết hàng ngày rất thú vị, có nhận xét chi tiết từ giảng viên.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000024', 3, 'Phần viết kịch bản video TikTok hơi ngắn, mong tác giả đào sâu thêm về storytelling cho video.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000022', 5, 'Khóa học đỉnh cao! Từ cắt ghép nhịp điệu âm thanh đến chỉnh màu Lumetri Color đều quá xịn.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000027', '30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000026', 5, 'Kho tài nguyên hiệu ứng chuyển cảnh và âm thanh đi kèm khóa học quá phong phú, dùng được luôn.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000028', '30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000023', 4, 'Giúp mình dựng được video giới thiệu sản phẩm cho công ty và nhận được rất nhiều lời khen.', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000029', '30000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000025', 3, 'Khâu tối ưu render và proxy trên máy cấu hình yếu nên được giải thích kỹ hơn một chút.', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000030', '30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000024', 5, 'Phương pháp Shadowing giúp mình cải thiện ngữ điệu rõ rệt chỉ sau 2 tuần học!', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000031', '30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000008', 5, 'Các mẫu câu đàm phán, viết email và thuyết trình trong cuộc họp cực kỳ sát với môi trường làm việc quốc tế.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000032', '30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000021', 4, 'Cô Emily Watson phát âm rất chuẩn và truyền cảm hứng. Học không hề bị áp lực.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000033', '30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000026', 4, 'Bài tập phản xạ tình huống hàng ngày rất hiệu quả, tự tin hơn hẳn khi họp với sếp Tây.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000034', '30000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000009', 3, 'Mong có thêm nhóm thực hành nói 1-1 giữa các học viên để tăng cơ hội cọ xát thực tế.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000035', '30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000025', 5, 'Khóa học Fullstack toàn diện nhất mình từng học! App Router, Server Actions và SSR được giải thích ngọn ngành.', now() - interval '10 days'),
('71000000-0000-0000-0000-000000000036', '30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000021', 5, 'Dự án cuối khóa xây dựng nền tảng E-commerce hoàn chỉnh từ giỏ hàng đến cổng thanh toán.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000037', '30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000009', 4, 'Codebase mẫu rất chuẩn mực, cấu trúc thư mục module hóa sạch sẽ dễ maintain.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000038', '30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000023', 4, 'Giảng viên giải thích kỹ về tối ưu SEO và Core Web Vitals trên Next.js.', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000039', '30000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000022', 3, 'Phần phân quyền Role-Based Access Control cần có thêm sơ đồ minh họa luồng xử lý token.', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000040', '30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000024', 5, 'Khóa học giải phóng tư duy làm việc của cả team mình! Phân định vai trò PO, Scrum Master rất rõ.', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000041', '30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000023', 5, 'Các ví dụ về ước lượng Story Point và lập kế hoạch Sprint bám sát quy trình thực tế tại các công ty IT.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000042', '30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000008', 4, 'Biểu đồ Burndown và cách vận hành Daily Standup hiệu quả giúp dự án của mình không còn bị trễ deadline.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000043', '30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000025', 4, 'Tài liệu mẫu về Product Backlog và User Story rất hữu dụng, tải về áp dụng được ngay.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000044', '30000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000021', 3, 'Nên bổ sung thêm phần so sánh giữa Scrum và Kanban cho các dự án bảo trì/support.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000045', '30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000022', 5, 'Thầy David Miller hướng dẫn Curve Editor và Graph Editor đỉnh thực sự! Chuyển động siêu mượt mà.', now() - interval '10 days'),
('71000000-0000-0000-0000-000000000046', '30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000010', 5, 'Từ một người chưa biết gì về After Effects, sau khóa học mình đã tự làm được intro hoạt hình cho kênh YouTube.', now() - interval '8 days'),
('71000000-0000-0000-0000-000000000047', '30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000026', 4, 'Các nguyên lý Animation 12 nguyên tắc Disney được lồng ghép cực kỳ khéo léo vào bài giảng.', now() - interval '6 days'),
('71000000-0000-0000-0000-000000000048', '30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000025', 4, 'Kỹ thuật rigging nhân vật 2D với Duik Bassel rất chi tiết và dễ làm theo.', now() - interval '4 days'),
('71000000-0000-0000-0000-000000000049', '30000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000023', 3, 'Các bài tập cuối khóa khá nặng về thời gian render, cần máy tính có card đồ họa tốt.', now() - interval '2 days'),
('71000000-0000-0000-0000-000000000050', '30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000026', 5, 'Khóa học đã thay đổi hoàn toàn cách mình viết bài bán hàng! Tỷ lệ chuyển đổi trang Landing Page tăng gấp đôi.', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000051', '30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000023', 5, 'Nghệ thuật khơi gợi tử huyệt cảm xúc và xử lý từ chối của khách hàng thông qua ngôn từ quá xuất sắc.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000052', '30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000010', 4, 'Rất nhiều mẫu headline thu hút khách hàng không thể cưỡng lại được. Rất bổ ích.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000053', '30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000024', 4, 'Cô Alex Morgan hướng dẫn cách nghiên cứu thấu hiểu tâm lý khách hàng cực kỳ sâu sắc.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000054', '30000000-0000-0000-0000-000000000015', '00000000-0000-0000-0000-000000000008', 3, 'Nội dung chủ yếu tập trung vào B2C, mong có thêm một chương chuyên biệt về Copywriting cho B2B.', now() - interval '1 days'),
('71000000-0000-0000-0000-000000000055', '30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000024', 5, 'Khóa học sửa phát âm chuẩn từng âm vị IPA, cách nối âm và ngữ điệu câu cực kỳ chi tiết.', now() - interval '9 days'),
('71000000-0000-0000-0000-000000000056', '30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000026', 5, 'Nhờ khóa học mà mình đã tự tin thuyết trình trước 50 khách hàng quốc tế mà không còn ngập ngừng.', now() - interval '7 days'),
('71000000-0000-0000-0000-000000000057', '30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000021', 4, 'Cô Emily có cách giảng rất lôi cuốn, chỉ ra đúng các lỗi sai phổ biến của người Việt khi nói tiếng Anh.', now() - interval '5 days'),
('71000000-0000-0000-0000-000000000058', '30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000009', 4, 'Các mẹo kiểm soát hơi thở và ngôn ngữ cơ thể trên sân khấu rất thực tế và hữu dụng.', now() - interval '3 days'),
('71000000-0000-0000-0000-000000000059', '30000000-0000-0000-0000-000000000016', '00000000-0000-0000-0000-000000000010', 3, 'Tốc độ bài tập phản xạ ở các bài cuối hơi nhanh đối với người có phát âm cơ bản.', now() - interval '1 days') ON CONFLICT (course_id, user_id) DO NOTHING;

-- =====================================================================
-- CẬP NHẬT LẠI AVG_RATING VÀ REVIEW_COUNT TRONG BẢNG COURSES
-- =====================================================================

UPDATE courses c
SET avg_rating = COALESCE(sub.avg_r, 5.0),
    review_count = COALESCE(sub.cnt_r, 0)
FROM (
    SELECT course_id, ROUND(AVG(rating)::numeric, 2) AS avg_r, COUNT(*) AS cnt_r
    FROM reviews
    GROUP BY course_id
) sub
WHERE c.id = sub.course_id;

UPDATE instructor_profiles ip
SET avg_rating = COALESCE(sub.avg_r, 5.0),
    student_count = COALESCE(sub.cnt_s, 0)
FROM (
    SELECT c.instructor_id, ROUND(AVG(r.rating)::numeric, 2) AS avg_r, COUNT(DISTINCT e.user_id) AS cnt_s
    FROM courses c
    LEFT JOIN reviews r ON r.course_id = c.id
    LEFT JOIN enrollments e ON e.course_id = c.id
    GROUP BY c.instructor_id
) sub
WHERE ip.user_id = sub.instructor_id;
