import { apiClient, ENDPOINTS } from '@/api';
import { popularCourses } from '@/data/popular-courses';
import { forYouCourses } from '@/data/for-you';
import { categories } from '@/data/categories';
import { instructors } from '@/data/instructors';
import { course as courseDetailData } from '@/data/course-detail';
import type { CourseDetail, PopularCourse, FeaturedCourse, Instructor } from '@/types';
import type { Category } from '@/types/category';

export const courseService = {
  // Fetch popular courses with mock fallback
  async getPopularCourses(): Promise<PopularCourse[]> {
    try {
      return await apiClient.get<PopularCourse[]>(ENDPOINTS.COURSES.POPULAR);
    } catch {
      return popularCourses;
    }
  },

  // Fetch for-you courses
  async getForYouCourses(): Promise<FeaturedCourse[]> {
    try {
      return await apiClient.get<FeaturedCourse[]>(ENDPOINTS.COURSES.FOR_YOU);
    } catch {
      return forYouCourses;
    }
  },

  // Fetch categories
  async getCategories(): Promise<Category[]> {
    try {
      return await apiClient.get<Category[]>(ENDPOINTS.CATEGORIES.LIST);
    } catch {
      return categories;
    }
  },

  // Fetch instructors
  async getInstructors(): Promise<Instructor[]> {
    try {
      return await apiClient.get<Instructor[]>(ENDPOINTS.INSTRUCTORS.LIST);
    } catch {
      return instructors;
    }
  },

  // Fetch course detail by id
  async getCourseDetail(id: string): Promise<CourseDetail> {
    try {
      return await apiClient.get<CourseDetail>(ENDPOINTS.COURSES.DETAIL(id));
    } catch {
      return courseDetailData;
    }
  },
};
