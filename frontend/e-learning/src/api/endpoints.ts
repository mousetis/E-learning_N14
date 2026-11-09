export const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:3000';

export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    FORGOT_PASSWORD: '/auth/forgot-password',
    PROFILE: '/auth/profile',
  },
  COURSES: {
    LIST: '/courses',
    POPULAR: '/courses/popular',
    FOR_YOU: '/courses/for-you',
    DETAIL: (id: string) => `/courses/${id}`,
    CURRICULUM: (id: string) => `/courses/${id}/curriculum`,
    REVIEWS: (id: string) => `/courses/${id}/reviews`,
  },
  CATEGORIES: {
    LIST: '/categories',
  },
  INSTRUCTORS: {
    LIST: '/instructors',
    DETAIL: (id: string) => `/instructors/${id}`,
  },
  CART: {
    GET: '/cart',
    ADD: '/cart/add',
    REMOVE: (id: string) => `/cart/${id}`,
    CHECKOUT: '/cart/checkout',
  },
  NOTIFICATIONS: {
    LIST: '/notifications',
    MARK_READ: (id: string) => `/notifications/${id}/read`,
  },
};
