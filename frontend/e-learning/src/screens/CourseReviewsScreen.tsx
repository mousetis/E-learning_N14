import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import { PriceBar } from '@/components/course/price-bar';
import { ReviewCard } from '@/components/course/review-card';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import {
  courseCurriculumHref,
  courseOverviewHref,
  coursePlayerHref,
  courseReviewsHref,
} from '@/lib/routes';
import { courseService } from '@/services/courseService';
import type { Review, CourseDetail } from '@/types/course';

type ReviewFilter = 'all' | '5' | '4' | '3';

const FILTERS: { key: ReviewFilter; label: string }[] = [
  { key: 'all', label: 'Tất cả' },
  { key: '5', label: '★ 5' },
  { key: '4', label: '★ 4' },
  { key: '3', label: '★ 3' },
];

export default function CourseReviewsScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : '';
  const router = useRouter();
  const theme = useAppTheme();
  const [filter, setFilter] = useState<ReviewFilter>('all');
  const [courseData, setCourseData] = useState<CourseDetail | null>(null);
  const [reviewsList, setReviewsList] = useState<Review[]>([]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [detail, revs] = await Promise.all([
          courseService.getCourseDetail(courseId),
          courseService.getReviews(courseId),
        ]);
        if (mounted) {
          if (detail) setCourseData(detail);
          if (revs && revs.length > 0) setReviewsList(revs);
        }
      } catch (err) {
        console.warn('Load reviews error:', err);
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

  const filteredReviews =
    filter === 'all'
      ? reviewsList
      : reviewsList.filter((review) => String(Math.round(review.rating)) === filter);

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
          activeTab="reviews"
          category={courseData?.category || 'Khóa học'}
          coverImage={courseData?.coverImage}
          footer={
            <RatingLine
              lessonCount={courseData?.lessonCount || 0}
              rating={courseData?.rating || 5}
              reviewCount={courseData?.reviewCount || 0}
            />
          }
          headline={courseData?.headline || ['Khóa học', 'Chuyên sâu']}
          tabs={tabs}
          onTabChange={(key) => {
            if (key === 'overview') router.push(courseOverviewHref(courseId));
            if (key === 'curriculum') router.push(courseCurriculumHref(courseId));
          }}
          title={courseData?.title || 'Đang tải khóa học...'}
        />

        <View
          style={[
            styles.summary,
            { backgroundColor: theme.backgroundElement, borderColor: theme.border },
          ]}
        >
          <View style={styles.summaryRow}>
            <Text style={[styles.summaryTitle, { color: theme.text }]}>
              {(courseData?.rating || 5).toFixed(1)}/5 ({courseData?.reviewCount || 0}+ đánh giá)
            </Text>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Xem tất cả đánh giá"
              onPress={() => setFilter('all')}
            >
              <Text style={[styles.summaryAction, { color: theme.primary }]}>Xem tất cả</Text>
            </Pressable>
          </View>

          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View style={styles.chips}>
              {FILTERS.map((item) => {
                const active = item.key === filter;
                return (
                  <Pressable
                    key={item.key}
                    accessibilityRole="button"
                    accessibilityLabel={`Lọc đánh giá ${item.label}`}
                    accessibilityState={{ selected: active }}
                    onPress={() => setFilter(item.key)}
                    style={[
                      styles.chip,
                      {
                        backgroundColor: active ? theme.primary : theme.backgroundElement,
                        borderColor: active ? theme.primary : theme.border,
                      },
                    ]}
                  >
                    <Text
                      style={[
                        styles.chipText,
                        { color: active ? theme.onPrimary : theme.textSecondary },
                      ]}
                    >
                      {item.label}
                    </Text>
                  </Pressable>
                );
              })}
            </View>
          </ScrollView>
        </View>

        <View style={styles.reviews}>
          {filteredReviews.map((review) => (
            <ReviewCard key={review.id} review={review} />
          ))}
        </View>
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
  summary: {
    padding: Spacing.three,
    borderRadius: Radius.lg,
    borderWidth: 1,
    gap: Spacing.two,
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  summaryTitle: {
    fontSize: 14,
    fontWeight: '700',
  },
  summaryAction: {
    fontSize: 12,
    fontWeight: '600',
  },
  chips: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  chip: {
    paddingHorizontal: Spacing.three,
    paddingVertical: 6,
    borderRadius: Radius.pill,
    borderWidth: 1,
  },
  chipText: {
    fontSize: 12,
    fontWeight: '600',
  },
  reviews: {
    gap: Spacing.three,
  },
});
