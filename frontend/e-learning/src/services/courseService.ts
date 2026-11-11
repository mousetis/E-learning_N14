import { apiClient, ENDPOINTS } from '@/api';
import type {
  CourseDetail,
  PopularCourse,
  FeaturedCourse,
  Chapter,
  Lesson,
  Review as CourseReview,
  QaThread,
  InstructorSummary,
  RelatedCourse,
  AvatarTone,
} from '@/types/course';
import type { Category } from '@/types/category';
import type { Instructor } from '@/types/instructor';

const PASTEL_COLORS = ['#c9bcff', '#ffd6a3', '#b2eedf', '#ffbfd2', '#ffe8a3', '#d0e1fd'];
const AVATAR_TONES: AvatarTone[] = ['pink', 'peach', 'mint', 'blue', 'yellow', 'gold', 'purple'];

const CATEGORY_ICONS: Record<string, { icon: string; emoji: string }> = {
  business: { icon: 'briefcase.fill', emoji: '💼' },
  design: { icon: 'paintbrush.fill', emoji: '🎨' },
  code: { icon: 'chevron.left.slash.chevron.right', emoji: '💻' },
  writing: { icon: 'pencil', emoji: '✏️' },
  movie: { icon: 'film', emoji: '🎬' },
  language: { icon: 'globe', emoji: '🌐' },
};

function formatVnd(amount?: number | null): string {
  if (amount == null || amount === 0) return 'Miễn phí';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function formatDuration(seconds?: number): string {
  if (!seconds) return '00:00';
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m < 10 ? '0' : ''}${m}:${s < 10 ? '0' : ''}${s}`;
}

function timeAgo(dateString?: string): string {
  if (!dateString) return 'Vừa xong';
  const diff = Date.now() - new Date(dateString).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${Math.max(1, mins)} phút trước`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours} giờ trước`;
  const days = Math.floor(hours / 24);
  if (days < 30) return `${days} ngày trước`;
  return `${Math.floor(days / 30)} tháng trước`;
}

function getToneForIndex(index: number): AvatarTone {
  return AVATAR_TONES[index % AVATAR_TONES.length];
}

function getColorForIndex(index: number): string {
  return PASTEL_COLORS[index % PASTEL_COLORS.length];
}

export const courseService = {
  // Fetch popular courses
  async getPopularCourses(): Promise<PopularCourse[]> {
    try {
      const res = await apiClient.get<any>(ENDPOINTS.COURSES.POPULAR);
      const items = res.items || res;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((c: any, index: number) => ({
          id: c.id,
          title: c.title,
          author: c.instructor?.fullName || 'Giảng viên',
          price: formatVnd(c.salePrice ?? c.originalPrice),
          coverColor: getColorForIndex(index),
          image: c.thumbnailUrl || undefined,
          badge: c.bestSeller ? 'Bán chạy' : undefined,
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getPopularCourses error:', err);
      return [];
    }
  },

  // Fetch for-you courses
  async getForYouCourses(): Promise<FeaturedCourse[]> {
    try {
      const res = await apiClient.get<any>(ENDPOINTS.COURSES.FOR_YOU);
      const items = res.items || res;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((c: any, index: number) => {
          const durationMin = Math.round((c.totalDurationSeconds || 0) / 60);
          return {
            id: c.id,
            title: c.title,
            author: c.instructor?.fullName || 'Giảng viên',
            meta: `${durationMin > 0 ? durationMin + ' phút' : ''} · ${c.level === 'BEGINNER' ? 'Cơ bản' : 'Nâng cao'}`,
            coverColor: getColorForIndex(index + 2),
            image: c.thumbnailUrl || undefined,
          };
        });
      }
      return [];
    } catch (err) {
      console.warn('courseService.getForYouCourses error:', err);
      return [];
    }
  },

  // Fetch categories
  async getCategories(): Promise<Category[]> {
    try {
      const items = await apiClient.get<any[]>(ENDPOINTS.CATEGORIES.LIST);
      if (Array.isArray(items) && items.length > 0) {
        return items.map((cat: any, index: number) => {
          const slug = (cat.slug || '').toLowerCase();
          const iconMeta = CATEGORY_ICONS[slug] || { icon: 'book.fill', emoji: '📚' };
          return {
            id: cat.id,
            title: cat.name,
            label: cat.name,
            color: cat.color || getColorForIndex(index),
            tint: cat.color || getColorForIndex(index),
            icon: iconMeta.icon,
            glyph: iconMeta.emoji,
            emoji: iconMeta.emoji,
            image: undefined,
          };
        });
      }
      return [];
    } catch (err) {
      console.warn('courseService.getCategories error:', err);
      return [];
    }
  },

  // Fetch hot topics
  async getHotTopics(): Promise<{ id: string; label: string }[]> {
    try {
      const items = await apiClient.get<any[]>(ENDPOINTS.TOPICS.HOT);
      if (Array.isArray(items) && items.length > 0) {
        return items.map((t: any) => ({
          id: t.id,
          label: t.name,
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getHotTopics error:', err);
      return [];
    }
  },

  // Fetch instructors
  async getInstructors(): Promise<Instructor[]> {
    try {
      const res = await apiClient.get<any>(ENDPOINTS.INSTRUCTORS.LIST);
      const items = res.items || res;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((ins: any, index: number) => ({
          id: ins.id,
          name: ins.fullName,
          role: ins.jobTitle || ins.expertise || 'Giảng viên',
          rating: (ins.avgRating || 4.8).toFixed(1),
          avatarColor: getColorForIndex(index),
          avatarUri: ins.avatarUrl || undefined,
          studentCount: ins.studentCount || 0,
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getInstructors error:', err);
      return [];
    }
  },

  // Fetch instructor detail
  async getInstructorDetail(id: string): Promise<any> {
    try {
      return await apiClient.get(ENDPOINTS.INSTRUCTORS.DETAIL(id));
    } catch {
      return null;
    }
  },

  // Fetch course detail by id or slug
  async getCourseDetail(idOrSlug: string): Promise<CourseDetail | null> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.COURSES.DETAIL(idOrSlug));
      if (!data) return null;

      let related: RelatedCourse[] = [];
      try {
        const sim = await apiClient.get<any[]>(ENDPOINTS.COURSES.SIMILAR(data.id || idOrSlug));
        if (Array.isArray(sim)) {
          related = sim.slice(0, 3).map((item, idx) => ({
            id: item.id,
            title: item.title,
            author: item.instructor?.fullName || 'Giảng viên',
            meta: `${Math.round((item.totalDurationSeconds || 0) / 60)} phút · ${item.level === 'BEGINNER' ? 'Cơ bản' : 'Nâng cao'}`,
            tone: getToneForIndex(idx),
            badge: item.bestSeller ? 'Bán chạy' : undefined,
            image: item.thumbnailUrl,
          }));
        }
      } catch {
        // ignore similar error
      }

      const instructor: InstructorSummary = {
        id: data.instructor?.id || '',
        name: data.instructor?.fullName || 'Giảng viên',
        role: data.instructor?.jobTitle || 'Chuyên gia đào tạo',
        tone: 'purple',
        avatarUri: data.instructor?.avatarUrl || undefined,
      };

      const benefits: string[] = Array.isArray(data.benefits)
        ? data.benefits.map((b: any) => (typeof b === 'string' ? b : b.content))
        : ['Chứng chỉ hoàn thành được công nhận', 'Truy cập tài liệu và mã nguồn trọn đời', 'Hỗ trợ giải đáp thắc mắc 1-1 cùng giảng viên'];

      return {
        id: data.id,
        shortTitle: data.title,
        category: data.category?.name || 'Chung',
        headline: [data.category?.name || 'Khóa học', data.level === 'BEGINNER' ? 'Cơ bản' : 'Chuyên sâu'],
        title: data.title,
        rating: data.avgRating || 5.0,
        reviewCount: data.reviewCount || 0,
        lessonCount: data.totalLessons || 0,
        price: formatVnd(data.salePrice ?? data.originalPrice),
        discount: data.discountPercent > 0 ? `Giảm ${data.discountPercent}%` : '',
        description: data.description || 'Khóa học chất lượng cao, cung cấp đầy đủ kiến thức từ lý thuyết đến thực hành thực tế.',
        descriptionMore: data.description || '',
        benefits,
        instructor,
        related,
        stats: {
          likes: data.likeCount || 0,
          shares: 0,
        },
        coverImage: data.thumbnailUrl || undefined,
        owned: Boolean(data.owned),
        priceNumber: data.salePrice ?? data.originalPrice ?? 0,
      };
    } catch (err) {
      console.warn('courseService.getCourseDetail error:', err);
      return null;
    }
  },

  // Fetch curriculum
  async getCurriculum(courseId: string): Promise<Chapter[]> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.COURSES.CURRICULUM(courseId));
      if (data && Array.isArray(data.sections) && data.sections.length > 0) {
        // Kiểm tra xem khóa học này có bài học nào bị khóa không
        const hasLockedLessons = data.sections.some((sec: any) =>
          (sec.lessons || []).some((les: any) => les.locked === true)
        );

        return data.sections.map((sec: any) => ({
          id: sec.id,
          title: sec.title,
          lessons: (sec.lessons || []).map((les: any, lIdx: number) => {
            let status: Lesson['status'] = 'locked';
            const isPreviewLesson = hasLockedLessons && Boolean(les.preview);

            if (les.locked === false || les.preview) {
              if (les.completed) {
                status = 'completed';
              } else if (les.lastPositionSeconds && les.lastPositionSeconds > 0) {
                status = 'in-progress';
              } else {
                status = 'available';
              }
            }

            return {
              id: les.id,
              index: String(lIdx + 1).padStart(2, '0'),
              title: les.title,
              duration: formatDuration(les.durationSeconds),
              status,
              isPreview: isPreviewLesson,
              videoUrl: les.videoUrl,
            };
          }),
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getCurriculum error:', err);
      return [];
    }
  },

  // Fetch reviews
  async getReviews(courseId: string): Promise<CourseReview[]> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.COURSES.REVIEWS(courseId));
      const items = data.reviews?.items || data.items || data;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((r: any, idx: number) => ({
          id: r.id,
          author: r.author?.fullName || 'Học viên',
          tone: getToneForIndex(idx),
          timeAgo: timeAgo(r.createdAt),
          rating: r.rating || 5,
          body: r.comment || '',
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getReviews error:', err);
      return [];
    }
  },

  // Submit a review
  async addReview(courseId: string, rating: number, comment: string): Promise<any> {
    return apiClient.post(ENDPOINTS.COURSES.ADD_REVIEW(courseId), { rating, comment });
  },

  // Fetch QA posts
  async getQaPosts(courseId: string): Promise<QaThread[]> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.COURSES.QA_POSTS(courseId));
      const items = data.items || data;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((p: any, idx: number) => ({
          id: p.id,
          author: p.author?.fullName || 'Học viên',
          tone: getToneForIndex(idx),
          timeAgo: timeAgo(p.createdAt),
          body: p.content,
          likes: p.likeCount || 0,
          comments: p.commentCount || 0,
        }));
      }
      return [];
    } catch (err) {
      console.warn('courseService.getQaPosts error:', err);
      return [];
    }
  },

  // Submit a QA post
  async addQaPost(courseId: string, content: string): Promise<any> {
    return apiClient.post(ENDPOINTS.COURSES.ADD_QA_POST(courseId), { content });
  },

  // Search courses
  async searchCourses(params: {
    keyword?: string;
    categoryId?: string;
    minPrice?: number;
    maxPrice?: number;
    sort?: string;
    level?: string;
    page?: number;
    size?: number;
  }): Promise<any> {
    const query = new URLSearchParams();
    if (params.keyword) query.append('keyword', params.keyword);
    if (params.categoryId) query.append('categoryId', params.categoryId);
    if (params.minPrice != null) query.append('minPrice', String(params.minPrice));
    if (params.maxPrice != null) query.append('maxPrice', String(params.maxPrice));
    if (params.sort) query.append('sort', params.sort);
    if (params.level) query.append('level', params.level);
    if (params.page != null) query.append('page', String(params.page));
    if (params.size != null) query.append('size', String(params.size));

    const endpoint = `${ENDPOINTS.COURSES.LIST}?${query.toString()}`;
    return apiClient.get(endpoint);
  },

  // Like / Unlike course
  async likeCourse(courseId: string): Promise<any> {
    return apiClient.post(ENDPOINTS.COURSES.LIKE(courseId));
  },

  async unlikeCourse(courseId: string): Promise<any> {
    return apiClient.delete(ENDPOINTS.COURSES.LIKE(courseId));
  },
};
