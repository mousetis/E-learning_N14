import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { ScrollView, StyleSheet } from 'react-native';

import { ChapterSection } from '@/components/course/chapter-section';
import { PriceBar } from '@/components/course/price-bar';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import {
  courseCurriculumHref,
  courseOverviewHref,
  coursePlayerHref,
  courseReviewsHref,
} from '@/lib/routes';
import { courseService } from '@/services/courseService';
import type { Chapter, CourseDetail } from '@/types';

export default function CourseCurriculumScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : '';
  const router = useRouter();

  const [courseData, setCourseData] = useState<CourseDetail | null>(null);
  const [chaptersList, setChaptersList] = useState<Chapter[]>([]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [detail, chaps] = await Promise.all([
          courseService.getCourseDetail(courseId),
          courseService.getCurriculum(courseId),
        ]);
        if (mounted) {
          if (detail) setCourseData(detail);
          if (chaps && chaps.length > 0) setChaptersList(chaps);
        }
      } catch (err) {
        console.warn('Load curriculum error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [courseId]);

  const tabs: SegmentItem[] = [
    { key: 'overview', label: 'Tổng quan', href: courseOverviewHref(courseId) },
    { key: 'curriculum', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'reviews', label: 'Đánh giá', href: courseReviewsHref(courseId) },
  ];

  return (
    <Screen
      footer={
        courseData?.owned ? (
          <PriceBar
            price="Đã sở hữu"
            discount=""
            actionLabel="Vào học ngay"
            onPress={() => router.push(coursePlayerHref(courseId))}
          />
        ) : (
          <PriceBar
            price={courseData?.price || '0 đ'}
            discount={courseData?.discount || ''}
            actionLabel="Thêm vào giỏ"
            onPress={() => router.push('/cart')}
          />
        )
      }
    >
      <ScreenHeader title="Chi tiết khóa học" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="curriculum"
          category={courseData?.category || 'Khóa học'}
          footer={
            <RatingLine
              lessonCount={courseData?.lessonCount || 0}
              rating={courseData?.rating || 5}
              reviewCount={courseData?.reviewCount || 0}
            />
          }
          headline={courseData?.headline || ['Khóa học', 'Chuyên sâu']}
          coverImage={courseData?.coverImage}
          tabs={tabs}
          onTabChange={(key) => {
            if (key === 'overview') router.push(courseOverviewHref(courseId));
            if (key === 'reviews') router.push(courseReviewsHref(courseId));
          }}
          title={courseData?.title || 'Đang tải khóa học...'}
        />

        {chaptersList.map((chapter) => (
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
