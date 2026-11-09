export type AvatarTone = 'pink' | 'peach' | 'mint' | 'blue' | 'yellow' | 'gold' | 'purple';

export type LessonStatus = 'completed' | 'in-progress' | 'locked' | 'available';

export type Lesson = {
  id: string;
  index: string;
  title: string;
  duration: string;
  status: LessonStatus;
};

export type Chapter = {
  id: string;
  title: string;
  lessons: Lesson[];
};

export type Review = {
  id: string;
  author: string;
  tone: AvatarTone;
  timeAgo: string;
  rating: number;
  body: string;
};

export type Project = {
  id: string;
  title: string;
  author: string;
  tone: AvatarTone;
};

export type ProjectFile = {
  id: string;
  name: string;
  size: string;
};

export type QaThread = {
  id: string;
  author: string;
  tone: AvatarTone;
  timeAgo: string;
  body: string;
  likes: number;
  comments: number;
};

export type InstructorSummary = {
  id: string;
  name: string;
  role: string;
  tone: AvatarTone;
  avatarUri?: string;
};

export type RelatedCourse = {
  id: string;
  title: string;
  author: string;
  meta: string;
  tone: AvatarTone;
  badge?: string;
  image?: string;
};

export type CourseDetail = {
  id: string;
  shortTitle: string;
  category: string;
  headline: string[];
  title: string;
  rating: number;
  reviewCount: number;
  lessonCount: number;
  price: string;
  discount: string;
  description: string;
  descriptionMore: string;
  benefits: string[];
  instructor: InstructorSummary;
  related: RelatedCourse[];
  stats: { likes: number; shares: number };
  coverImage?: string;
};

export type InstructorProfile = {
  id: string;
  name: string;
  role: string;
  location: string;
  localTime: string;
  tone: AvatarTone;
  avatarUri?: string;
  bio: string;
  stats: { rating: number; students: number; courses: number };
  designCourses: RelatedCourse[];
  graphicCourses: RelatedCourse[];
  reviews: Review[];
};

export interface PopularCourse {
  id: string;
  title: string;
  author: string;
  price: string;
  coverColor: string;
  image?: string;
  badge?: string;
}

export interface FeaturedCourse {
  id: string;
  title: string;
  author: string;
  meta: string;
  coverColor: string;
  image?: string;
}

export interface MyCourse {
  id: string;
  title: string;
  duration: string;
  progress: number;
  coverColor: string;
  image?: string;
}

export interface SavedCourse {
  id: string;
  title: string;
  author: string;
  meta: string;
  coverColor: string;
  image?: string;
}

export interface Course {
  id: string;
  title: string;
  author: string;
  instructor?: string;
  instructorId?: string;
  categoryId?: string;
  description?: string;
  price: number;
  originalPrice?: number;
  rating: number;
  reviewCount?: number;
  lessons: number;
  lessonCount?: number;
  tint: string;
  coverColor?: string;
  coverUri?: string;
  image?: string;
  isBestseller?: boolean;
  isBookmarked?: boolean;
  progress?: number;
  badge?: { label: string; tint: string };
}

export interface Category {
  id: string;
  title: string;
  tint: string;
  icon: string;
  glyph: string;
  label?: string;
  color?: string;
  image?: string;
  emoji?: string;
}

export interface Topic {
  id: string;
  label: string;
}

export interface SortOption {
  id: string;
  label: string;
}

export interface RatingOption {
  id: string;
  stars: number;
  label: string;
}
