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
