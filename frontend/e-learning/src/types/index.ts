export * from './user';
export * from './cart';
export * from './notification';
export * from './category';
export * from './instructor';

export interface Lesson {
  id: string;
  title: string;
  durationSeconds?: number;
  duration?: string;
  index?: string;
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
