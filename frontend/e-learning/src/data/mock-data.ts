import type {
  CartItem,
  Category,
  Course,
  Instructor,
  NotificationItem,
  Order,
  PaymentMethod,
  User,
} from '@/types';

export const currentUser: User = {
  id: 'u1',
  name: 'Martha Rosie',
  email: 'rosie@email.com',
  avatarColor: '#FFEB8D',
  role: 'student',
};

export const categories: Category[] = [
  { id: 'business', label: 'Kinh doanh', color: '#C9BCFF', icon: 'briefcase' },
  { id: 'design', label: 'Thiết kế', color: '#FFD6A3', icon: 'palette' },
  { id: 'programming', label: 'Lập trình', color: '#B2EEDF', icon: 'code' },
  { id: 'writing', label: 'Viết lách', color: '#FFBFD2', icon: 'pencil' },
  { id: 'film', label: 'Phim', color: '#B9DCFF', icon: 'film' },
  { id: 'language', label: 'Ngôn ngữ', color: '#FFE58A', icon: 'globe' },
];

export const popularCourses: Course[] = [
  {
    id: 'c1',
    title: 'PHP in One Click',
    instructor: 'Ramono Wultschner',
    price: 59,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#C9BCFF',
  },
  {
    id: 'c2',
    title: 'Python Introduction',
    instructor: 'Ramono Wultschner',
    price: 39,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#B2EEDF',
    isBestseller: true,
  },
  {
    id: 'c3',
    title: 'Khóa học',
    instructor: 'Olivia Wang',
    price: 29,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#B9DCFF',
  },
];

export const forYouCourses: Course[] = [
  {
    id: 'c4',
    title: 'Website Design',
    instructor: 'Ramono Wultschner',
    price: 59,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#FFD6A3',
  },
  {
    id: 'c5',
    title: 'UX Research For Beginners',
    instructor: 'Olivia Wang',
    price: 29,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#FFBFD2',
  },
];

export const inspirationCourses: Course[] = [
  {
    id: 'c6',
    title: 'Digital Portrait',
    instructor: 'Ramono Wultschner',
    price: 67,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#FFE58A',
  },
  {
    id: 'c7',
    title: 'Workspace Decor',
    instructor: 'Ruth Dominguez',
    price: 19,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#B2EEDF',
  },
  {
    id: 'c8',
    title: 'Packaging Design',
    instructor: 'Hui Anderson',
    price: 89,
    rating: 4.5,
    reviewCount: 1233,
    lessonCount: 12,
    coverColor: '#C9BCFF',
  },
];

export const topInstructors: Instructor[] = [
  { id: 'i1', name: 'Christian Hayes', avatarColor: '#FFD6A3', rating: 4.5, studentCount: 1233 },
  { id: 'i2', name: 'Dennis Sweeney', avatarColor: '#B2EEDF', rating: 4.5, studentCount: 1233 },
  { id: 'i3', name: 'Olivia Wang', avatarColor: '#FFBFD2', rating: 4.5, studentCount: 1233 },
];

export const promoBanner = {
  kicker: 'KHÓA HỌC QUẢN LÝ DỰ ÁN',
  title: 'Giảm 20%',
  cta: 'Tham gia ngay',
};

export const notifications: NotificationItem[] = [];

export const cartItems: CartItem[] = [];

export const paymentMethods: PaymentMethod[] = [
  {
    id: 'pm1',
    label: 'Visa •••• 4242',
    type: 'card',
    last4: '4242',
    isDefault: true,
  },
];

export const orders: Order[] = [
  {
    id: '#ELN-20481',
    date: '2025-03-10',
    total: 51,
    status: 'paid',
    courseIds: ['c1'],
  },
];

export const featuredCourse = {
  title: 'UX Foundation: Nhập môn thiết kế trải nghiệm người dùng',
  instructor: 'Martha Rosie',
  price: 51,
  originalPrice: 59,
  totalPrice: 160,
  orderId: '#ELN-20481',
};
