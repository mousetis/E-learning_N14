import type { NotificationItem } from '@/types/notification';

export const notifications: NotificationItem[] = [
  { id: 'n1', title: 'Khóa học mới', body: 'Sara Weise vừa đăng "Mobile App Design".', time: '2 giờ trước', iconColor: '#ffc5d9', unread: true },
  { id: 'n2', title: 'Ưu đãi giảm 20%', body: 'Khóa học Quản lý dự án đang giảm giá đến hết tuần.', time: 'Hôm qua', iconColor: '#ffd9a8', unread: true },
  { id: 'n3', title: 'Sắp hoàn thành!', body: 'Bạn đã học 70% "Creative Art Design".', time: '2 ngày trước', iconColor: '#c7c2fe', unread: false },
  { id: 'n4', title: 'Có trả lời mới', body: 'Jane Berry đã trả lời câu hỏi của bạn.', time: '3 ngày trước', iconColor: '#b8f1e0', unread: false },
];
