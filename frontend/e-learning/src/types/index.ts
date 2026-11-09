export interface Category {
  id: string;
  label?: string;
  title?: string;
  color?: string;
  tint?: string;
  icon?: string;
  glyph?: string;
  emoji?: string;
  image?: string;
}

export interface Instructor {
  id: string;
  name: string;
  avatarColor: string;
  avatarUri?: string;
  rating: number | string;
  studentCount?: number;
}

export interface Lesson {
  id: string;
  title: string;
  durationSeconds: number;
  completed?: boolean;
}

export interface Course {
  id: string;
  title: string;
  instructor?: string;
  author?: string;
  instructorId?: string;
  categoryId?: string;
  description?: string;
  price: number;
  originalPrice?: number;
  rating: number;
  reviewCount?: number;
  lessonCount?: number;
  lessons?: number | Lesson[];
  coverColor?: string;
  coverUri?: string;
  image?: string;
  tint?: string;
  isBestseller?: boolean;
  isBookmarked?: boolean;
  progress?: number;
  badge?: { label: string; tint: string };
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatarUri?: string;
  avatarColor?: string;
  avatar?: string;
  role?: string;
  token?: string;
}

export interface Review {
  id: string;
  userId: string;
  userName: string;
  avatarColor?: string;
  avatarUri?: string;
  courseId: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  body: string;
  createdAt: string;
  read: boolean;
  type: 'course' | 'promotion' | 'system';
}

export interface CartItem {
  id: string;
  courseId: string;
  quantity: number;
}

export interface PaymentMethod {
  id: string;
  label: string;
  type: 'card' | 'bank' | 'wallet';
  last4?: string;
  isDefault?: boolean;
}

export interface Order {
  id: string;
  date: string;
  total: number;
  status: 'pending' | 'paid' | 'cancelled';
  courseIds: string[];
}

export interface QuizQuestion {
  id: string;
  question: string;
  options: string[];
  correctIndex: number;
}
