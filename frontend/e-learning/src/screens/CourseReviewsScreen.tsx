import { useLocalSearchParams } from '@/navigation/routerHook';
import { useState } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import { PriceBar } from '@/components/course/price-bar';
import { ReviewCard } from '@/components/course/review-card';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { course, courseReviews } from '@/data/course-detail';
import { useAppTheme } from '@/hooks/use-app-theme';
import {
  courseCurriculumHref,
  courseOverviewHref,
  courseReviewsHref,
} from '@/lib/routes';

type ReviewFilter = 'all' | '5' | '4' | '3';

const FILTERS: { key: ReviewFilter; label: string }[] = [
  { key: 'all', label: 'Tất cả' },
  { key: '5', label: '★ 5' },
  { key: '4', label: '★ 4' },
  { key: '3', label: '★ 3' },
];

export default function CourseReviewsScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : course.id;
  const theme = useAppTheme();
  const [filter, setFilter] = useState<ReviewFilter>('all');

  const tabs: SegmentItem[] = [
    { key: 'overview', label: 'Tổng quan', href: courseOverviewHref(courseId) },
    { key: 'curriculum', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'reviews', label: 'Đánh giá', href: courseReviewsHref(courseId) },
  ];

  const reviews =
    filter === 'all'
      ? courseReviews
      : courseReviews.filter((review) => String(Math.round(review.rating)) === filter);

  return (
    <Screen footer={<PriceBar price={course.price} discount={course.discount} />}>
      <ScreenHeader title="Chi tiết khóa học" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="reviews"
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

        <View
          style={[
            styles.summary,
            { backgroundColor: theme.backgroundElement, borderColor: theme.border },
          ]}
        >
          <View style={styles.summaryRow}>
            <Text style={[styles.summaryTitle, { color: theme.text }]}>
              4.5/5 (1233+ đánh giá)
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
                    <Text style={[styles.chipLabel, { color: active ? theme.onPrimary : theme.primary }]}>
                      {item.label}
                    </Text>
                  </Pressable>
                );
              })}
            </View>
          </ScrollView>
        </View>

        {reviews.map((review) => (
          <ReviewCard key={review.id} review={review} />
        ))}

        {reviews.length === 0 ? (
          <Text style={[styles.empty, { color: theme.textSecondary }]}>
            Chưa có đánh giá nào cho mức sao này.
          </Text>
        ) : null}
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
    gap: Spacing.two,
    borderRadius: Radius.xl,
    borderWidth: 1,
    padding: Spacing.three,
  },
  summaryRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: Spacing.two,
  },
  summaryTitle: {
    fontSize: 15,
    fontWeight: '700',
  },
  summaryAction: {
    fontSize: 13,
    fontWeight: '600',
  },
  chips: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  chip: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.one + 2,
    borderRadius: Radius.pill,
    borderWidth: 1,
  },
  chipLabel: {
    fontSize: 12,
    fontWeight: '600',
  },
  empty: {
    fontSize: 13,
    textAlign: 'center',
  },
});
