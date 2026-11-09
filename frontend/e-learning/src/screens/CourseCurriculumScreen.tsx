import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { ScrollView, StyleSheet } from 'react-native';

import { ChapterSection } from '@/components/course/chapter-section';
import { PriceBar } from '@/components/course/price-bar';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import { chapters, course } from '@/data/course-detail';
import {
  courseCurriculumHref,
  courseOverviewHref,
  coursePlayerHref,
  courseReviewsHref,
} from '@/lib/routes';

export default function CourseCurriculumScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : course.id;
  const router = useRouter();

  const tabs: SegmentItem[] = [
    { key: 'overview', label: 'Tổng quan', href: courseOverviewHref(courseId) },
    { key: 'curriculum', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'reviews', label: 'Đánh giá', href: courseReviewsHref(courseId) },
  ];

  return (
    <Screen footer={<PriceBar price={course.price} discount={course.discount} />}>
      <ScreenHeader title="Chi tiết khóa học" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="curriculum"
          category={course.category}
          footer={
            <RatingLine
              lessonCount={course.lessonCount}
              rating={course.rating}
              reviewCount={course.reviewCount}
            />
          }
          headline={course.headline}
          tabs={tabs}
          title={course.title}
        />

        {chapters.map((chapter) => (
          <ChapterSection
            key={chapter.id}
            chapter={chapter}
            onLessonPress={() => router.push(coursePlayerHref(courseId))}
          />
        ))}
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    padding: Spacing.three,
    paddingBottom: Spacing.five,
    gap: Spacing.three,
  },
});
