import { courseReviews } from '@/data/course-detail';
import type { InstructorProfile } from '@/types/course';

export const instructorProfile: InstructorProfile = {
  id: 'sara-weise',
  name: 'Sara Weise',
  role: 'UI/UX Designer & Design Lead',
  location: 'New York, USA',
  localTime: '09:30',
  tone: 'pink',
  avatarUri: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500&auto=format&fit=crop&q=80',
  bio: 'Sara Weise là chuyên gia thiết kế UI/UX với hơn 10 năm kinh nghiệm thiết kế sản phẩm cho các tập đoàn công nghệ hàng đầu thế giới. Cô chuyên sâu về nghiên cứu người dùng, Design Systems và Prototype tương tác cao.',
  stats: { rating: 4.9, students: 12480, courses: 12 },
  designCourses: [
    {
      id: 'ux-foundation',
      title: 'UX Foundation: Nhập môn UX',
      author: 'Sara Weise',
      meta: '$51 · ★ 4.9',
      tone: 'peach',
      badge: 'Bán chạy',
      image: 'https://images.unsplash.com/photo-1586717791821-3f44a563fa4c?w=600&auto=format&fit=crop&q=80',
    },
    {
      id: 'mobile-app-design',
      title: 'Mobile App Design with Figma Pro',
      author: 'Sara Weise',
      meta: '$59 · ★ 4.8',
      tone: 'pink',
      image: 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=600&auto=format&fit=crop&q=80',
    },
  ],
  graphicCourses: [
    {
      id: 'graphic-design',
      title: 'Thiết kế đồ họa & Visual Branding',
      author: 'Sara Weise',
      meta: '$45 · ★ 4.7',
      tone: 'yellow',
      image: 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop&q=80',
    },
  ],
  reviews: courseReviews,
};
