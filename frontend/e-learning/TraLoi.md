# Báo Cáo & Trả Lời Câu Hỏi Dự Án E-Learning (LTTBDĐ)

## 1. Thông Tin Nhóm & Đề Tài
- **Tên đề tài**: Ứng dụng E-Learning Học Trực Tuyến
- **Môn học**: Lập trình Thiết bị Di động (LTTBDĐ)
- **Học kỳ**: Học kỳ 7

## 2. Kiến Trúc Dự Án (Layered Architecture)
- `src/screens`: Các màn hình của ứng dụng (Home, Search, Courses, Profile, Detail, Cart, Notifications...).
- `src/navigation`: Cấu hình điều hướng React Navigation (AppNavigator, BottomTabNavigator).
- `src/components`: UI Components tái sử dụng (Button, Input, Card, Header, Icon...).
- `src/api`: Kết nối API REST (Axios/fetch client, API Endpoints).
- `src/services`: Xử lý nghiệp vụ dữ liệu (courseService, authService).
- `src/storage`: Quản lý lưu trữ cục bộ (Local Storage).
- `src/store`: Quản lý State toàn cục (AppContext, Global Store).
- `src/hooks`: Custom Hooks tái sử dụng (`useTheme`, `useAppStore`).
- `src/data`: Mock data phục vụ hiển thị.
- `src/theme`: Bảng màu (`Colors`), typography (`Fonts`), khoảng cách (`Spacing`).
- `src/utils`: Các hàm tiện ích format tiền tệ, thời gian, validate dữ liệu.
- `src/types.ts`: Định nghĩa Type và Interface TypeScript.
- `App.tsx`: Root Component khởi chạy ứng dụng.
- `db.json`: Cơ sở dữ liệu mẫu cho json-server.
