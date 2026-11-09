import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import { BenefitRow } from '@/components/course/benefit-row';
import { CourseMiniCard } from '@/components/course/course-mini-card';
import { InstructorRow } from '@/components/course/instructor-row';
import { PriceBar } from '@/components/course/price-bar';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import { course } from '@/data/course-detail';
import { useAppTheme } from '@/hooks/use-app-theme';
import {
  courseCurriculumHref,
  courseOverviewHref,
  coursePlayerHref,
  courseReviewsHref,
  instructorHref,
} from '@/lib/routes';

export default function CourseOverviewScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : course.id;
  const theme = useAppTheme();
  const router = useRouter();
  const [expanded, setExpanded] = useState(false);
  const [following, setFollowing] = useState(false);

  const tabs: SegmentItem[] = [
    { key: 'overview', label: 'Tổng quan', href: courseOverviewHref(courseId) },
    { key: 'curriculum', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'reviews', label: 'Đánh giá', href: courseReviewsHref(courseId) },
  ];

  return (
    <Screen footer={<PriceBar price={course.price} discount={course.discount} onPress={() => router.push('/cart')} />}>
      <ScreenHeader title="Chi tiết khóa học" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="overview"
          category={course.category}
          coverImage={course.coverImage}
          footer={
            <RatingLine
              lessonCount={course.lessonCount}
              rating={course.rating}
              reviewCount={course.reviewCount}
            />
          }
          headline={course.headline}
          onPlayPress={() => router.push(coursePlayerHref(courseId))}
          tabs={tabs}
          title={course.title}
        />

        <InstructorRow
          following={following}
          instructor={course.instructor}
          onFollow={() => setFollowing((prev) => !prev)}
          onPress={() => router.push(instructorHref(course.instructor.id))}
        />

        <View style={styles.section}>
          <SectionHeader title="Mô tả" />
          <Text style={[styles.paragraph, { color: theme.textSecondary }]}>
            {expanded ? course.descriptionMore : course.description}
          </Text>
          <Pressable accessibilityRole="button" onPress={() => setExpanded((prev) => !prev)}>
            <Text style={[styles.link, { color: theme.primary }]}>
              {expanded ? 'Thu gọn' : 'Xem thêm'}
            </Text>
          </Pressable>
        </View>

        <View style={styles.section}>
          <SectionHeader title="Lợi ích" />
          <View style={styles.benefits}>
            {course.benefits.map((benefit) => (
              <BenefitRow key={benefit} label={benefit} />
            ))}
          </View>
        </View>

        <View style={styles.section}>
          <SectionHeader title="Khóa học tương tự" />
          {course.related.map((item) => (
            <CourseMiniCard
              key={item.id}
              course={item}
              onPress={() => router.push(courseOverviewHref(item.id))}
            />
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
  section: {
    gap: Spacing.two,
  },
  paragraph: {
    fontSize: 13,
    lineHeight: 20,
  },
  link: {
    fontSize: 13,
    fontWeight: '600',
  },
  benefits: {
    gap: Spacing.two,
  },
});
