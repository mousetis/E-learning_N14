import { apiClient, ENDPOINTS } from '@/api';
import type { MyCourse } from '@/types/course';

const PASTEL_COLORS = ['#c9bcff', '#ffd6a3', '#b2eedf', '#ffbfd2', '#ffe8a3', '#d0e1fd'];

export const enrollmentService = {
  async getMyEnrollments(): Promise<{
    all: MyCourse[];
    inProgress: MyCourse[];
    completed: MyCourse[];
  }> {
    try {
      const data = await apiClient.get<any[]>(ENDPOINTS.LEARNING.ENROLLMENTS);
      if (Array.isArray(data) && data.length > 0) {
        const all: MyCourse[] = data.map((enr: any, idx: number) => {
          const c = enr.course || {};
          const durationMin = Math.round((c.totalDurationSeconds || 0) / 60);
          const progress = Math.round(Number(enr.progressPercentage || 0));
          return {
            id: c.id,
            title: c.title || 'Khóa học',
            duration: `${durationMin > 0 ? durationMin + ' phút' : '30 bài học'}`,
            progress,
            coverColor: PASTEL_COLORS[idx % PASTEL_COLORS.length],
            image: c.thumbnailUrl || undefined,
          };
        });

        const inProgress = all.filter((c) => c.progress < 100);
        const completed = all.filter((c) => c.progress >= 100);

        return { all, inProgress, completed };
      }
      return { all: [], inProgress: [], completed: [] };
    } catch (err) {
      console.warn('enrollmentService.getMyEnrollments error:', err);
      return { all: [], inProgress: [], completed: [] };
    }
  },

  async getCertificates(): Promise<any[]> {
    try {
      const data = await apiClient.get<any[]>(ENDPOINTS.LEARNING.CERTIFICATES);
      return Array.isArray(data) ? data : [];
    } catch {
      return [];
    }
  },

  async updateLessonProgress(lessonId: string, positionSeconds: number): Promise<any> {
    return apiClient.put(ENDPOINTS.LEARNING.LESSON_PROGRESS(lessonId), { positionSeconds });
  },
};
