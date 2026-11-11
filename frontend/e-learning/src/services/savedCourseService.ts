import { apiClient, ENDPOINTS } from '@/api';
import type { SavedCourse } from '@/types/course';

const PASTEL_COLORS = ['#c9bcff', '#ffd6a3', '#b2eedf', '#ffbfd2', '#ffe8a3', '#d0e1fd'];

export const savedCourseService = {
  async getSavedCourses(): Promise<SavedCourse[]> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.SAVED_COURSES.LIST);
      const items = data?.items || data;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((item: any, idx: number) => {
          const c = item.course || item;
          const durationMin = Math.round((c.totalDurationSeconds || 0) / 60);
          return {
            id: c.id,
            title: c.title || 'Khóa học',
            author: c.instructor?.fullName || 'Giảng viên',
            meta: `${durationMin > 0 ? durationMin + ' phút' : '24 bài học'}`,
            coverColor: PASTEL_COLORS[idx % PASTEL_COLORS.length],
            image: c.thumbnailUrl || undefined,
          };
        });
      }
      return [];
    } catch (err) {
      console.warn('savedCourseService.getSavedCourses error:', err);
      return [];
    }
  },

  async saveCourse(courseId: string): Promise<any> {
    return apiClient.post(ENDPOINTS.SAVED_COURSES.TOGGLE(courseId));
  },

  async unsaveCourse(courseId: string): Promise<any> {
    return apiClient.delete(ENDPOINTS.SAVED_COURSES.TOGGLE(courseId));
  },
};
