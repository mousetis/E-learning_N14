// Export base and extended types
export * from './types/index';
export type {
  AvatarTone,
  LessonStatus,
  Chapter,
  Project,
  ProjectFile,
  QaThread,
  InstructorSummary,
  RelatedCourse,
  CourseDetail,
  InstructorProfile,
  PopularCourse,
  FeaturedCourse,
  MyCourse,
  SavedCourse,
  Topic,
  SortOption,
  RatingOption,
} from './types/course';

// Common helper types
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}
