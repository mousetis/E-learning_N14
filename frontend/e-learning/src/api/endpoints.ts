import Constants from 'expo-constants';
import { Platform } from 'react-native';

function resolveApiBaseUrl(): string {
  // 1. Tự động lấy IP của máy tính host từ Metro Bundler (chính xác tuyệt đối khi test trên Expo Go điện thoại)
  const expoHost =
    Constants.expoConfig?.hostUri ||
    (Constants as any).expoGoConfig?.debuggerHost ||
    (Constants as any).manifest2?.extra?.expoGo?.debuggerHost ||
    (Constants as any).manifest?.debuggerHost;

  if (expoHost) {
    const ip = expoHost.split(':')[0];
    if (ip && ip !== 'localhost' && ip !== '127.0.0.1') {
      return `http://${ip}:8080/api/v1`;
    }
  }

  const envUrl = process.env.EXPO_PUBLIC_API_URL;
  if (envUrl) {
    return envUrl;
  }

  // Trình duyệt web chạy trên chính máy tính
  if (Platform.OS === 'web') {
    return 'http://localhost:8080/api/v1';
  }

  // Fallback IP máy tính trên mạng LAN cho thiết bị di động
  return 'http://192.168.50.108:8080/api/v1';
}

export const API_BASE_URL = resolveApiBaseUrl();

export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    VERIFY_OTP: '/auth/verify-otp',
    RESEND_OTP: '/auth/resend-otp',
    FORGOT_PASSWORD: '/auth/forgot-password',
    RESET_PASSWORD: '/auth/reset-password',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
  },
  USER: {
    ME: '/users/me',
    UPDATE_PROFILE: '/users/me',
    CHANGE_PASSWORD: '/users/me/password',
  },
  COURSES: {
    LIST: '/courses',
    POPULAR: '/courses/popular',
    FOR_YOU: '/courses/for-you',
    DETAIL: (id: string) => `/courses/${id}`,
    CURRICULUM: (id: string) => `/courses/${id}/curriculum`,
    SIMILAR: (id: string) => `/courses/${id}/similar`,
    REVIEWS: (id: string) => `/courses/${id}/reviews?size=50`,
    MY_REVIEW: (id: string) => `/courses/${id}/reviews/me`,
    ADD_REVIEW: (id: string) => `/courses/${id}/reviews`,
    QA_POSTS: (id: string) => `/courses/${id}/qa-posts`,
    ADD_QA_POST: (id: string) => `/courses/${id}/qa-posts`,
    PROJECT: (id: string) => `/courses/${id}/project`,
    SUBMISSIONS: (id: string) => `/courses/${id}/submissions`,
    LIKE: (id: string) => `/courses/${id}/like`,
  },
  QA: {
    COMMENTS: (postId: string) => `/qa-posts/${postId}/comments`,
    LIKE: (postId: string) => `/qa-posts/${postId}/like`,
  },
  CATEGORIES: {
    LIST: '/categories',
    COURSES: (slug: string) => `/categories/${slug}/courses`,
  },
  TOPICS: {
    HOT: '/topics/hot',
  },
  INSTRUCTORS: {
    LIST: '/instructors',
    TOP: '/instructors/top',
    DETAIL: (id: string) => `/instructors/${id}`,
    COURSES: (id: string) => `/instructors/${id}/courses`,
    REVIEWS: (id: string) => `/instructors/${id}/reviews`,
    FOLLOW: (id: string) => `/instructors/${id}/follow`,
  },
  CART: {
    GET: '/cart',
    ADD_ITEM: '/cart/items',
    REMOVE_ITEM: (courseId: string) => `/cart/items/${courseId}`,
  },
  ORDERS: {
    CREATE: '/orders',
    LIST: '/orders',
    DETAIL: (orderCode: string) => `/orders/${orderCode}`,
  },
  COUPONS: {
    VALIDATE: '/coupons/validate',
  },
  PAYMENTS: {
    MOCK_COMPLETE: (orderCode: string) => `/payments/mock/${orderCode}/complete`,
  },
  LEARNING: {
    ENROLLMENTS: '/me/enrollments',
    COURSE: (courseId: string) => `/learn/courses/${courseId}`,
    LESSON_STREAM: (lessonId: string) => `/learn/lessons/${lessonId}/stream`,
    LESSON_PROGRESS: (lessonId: string) => `/learn/lessons/${lessonId}/progress`,
    CERTIFICATES: '/me/certificates',
    CERTIFICATE_DETAIL: (code: string) => `/certificates/${code}`,
  },
  SAVED_COURSES: {
    LIST: '/saved-courses',
    TOGGLE: (courseId: string) => `/saved-courses/${courseId}`,
  },
  NOTIFICATIONS: {
    LIST: '/notifications',
    UNREAD_COUNT: '/notifications/unread-count',
    MARK_READ: (id: string) => `/notifications/${id}/read`,
    READ_ALL: '/notifications/read-all',
  },
};
