import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { Alert, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

import { BenefitRow } from '@/components/course/benefit-row';
import { ChapterSection } from '@/components/course/chapter-section';
import { CourseMiniCard } from '@/components/course/course-mini-card';
import { InstructorRow } from '@/components/course/instructor-row';
import { PriceBar } from '@/components/course/price-bar';
import { ReviewCard } from '@/components/course/review-card';
import { CourseHero } from '@/components/ui/course-hero';
import { RatingLine } from '@/components/ui/rating-line';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import {
  courseCurriculumHref,
  courseOverviewHref,
  coursePlayerHref,
  courseReviewsHref,
  instructorHref,
} from '@/lib/routes';
import { courseService } from '@/services/courseService';
import { useAppStore } from '@/store';
import type { CourseDetail } from '@/types';
import type { Chapter, Review } from '@/types/course';

type DetailTab = 'overview' | 'curriculum' | 'reviews';
type ReviewFilter = 'all' | '5' | '4' | '3';

const REVIEW_FILTERS: { key: ReviewFilter; label: string }[] = [
  { key: 'all', label: 'Tất cả' },
  { key: '5', label: '★ 5' },
  { key: '4', label: '★ 4' },
  { key: '3', label: '★ 3' },
];

export default function CourseOverviewScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : '';
  const theme = useAppTheme();
  const router = useRouter();
  const { addToCart } = useAppStore();

  const [activeTab, setActiveTab] = useState<DetailTab>('overview');
  const [courseData, setCourseData] = useState<CourseDetail | null>(null);
  const [chaptersList, setChaptersList] = useState<Chapter[]>([]);
  const [reviewsList, setReviewsList] = useState<Review[]>([]);
  const [reviewFilter, setReviewFilter] = useState<ReviewFilter>('all');
  const [expanded, setExpanded] = useState(false);
  const [following, setFollowing] = useState(false);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [detail, chaps, revs] = await Promise.all([
          courseService.getCourseDetail(courseId),
          courseService.getCurriculum(courseId),
          courseService.getReviews(courseId),
        ]);
        if (mounted) {
          if (detail) setCourseData(detail);
          if (chaps && chaps.length > 0) setChaptersList(chaps);
          if (revs && revs.length > 0) setReviewsList(revs);
        }
      } catch (err) {
        console.warn('Load course detail error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [courseId]);

  const tabs: SegmentItem[] = [
    { key: 'overview', label: 'Tổng quan' },
    { key: 'curriculum', label: 'Bài học' },
    { key: 'reviews', label: 'Đánh giá' },
  ];

  const filteredReviews =
    reviewFilter === 'all'
      ? reviewsList
      : reviewsList.filter((review) => String(Math.round(review.rating)) === reviewFilter);

  const handleAddToCart = async () => {
    if (!courseData) return;
    try {
      await addToCart({
        id: courseData.id,
        courseId: courseData.id,
        title: courseData.title,
        author: courseData.instructor.name,
        price: courseData.priceNumber ?? 0,
        thumbnailColor: '#c9bcff',
        thumbnailLabel: courseData.category.slice(0, 3).toUpperCase(),
        image: courseData.coverImage,
      });
      Alert.alert('Thành công', 'Đã thêm khóa học vào giỏ hàng!', [
        { text: 'Xem giỏ hàng', onPress: () => router.push('/cart') },
        { text: 'Ở lại trang', style: 'cancel' },
      ]);
    } catch {
      router.push('/cart');
    }
  };

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
            onPress={handleAddToCart}
          />
        )
      }
    >
      <ScreenHeader title="Chi tiết khóa học" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab={activeTab}
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
          onPlayPress={() => router.push(coursePlayerHref(courseId))}
          tabs={tabs}
          onTabChange={(key) => setActiveTab(key as DetailTab)}
          title={courseData?.title || 'Đang tải khóa học...'}
        />

        {activeTab === 'overview' && courseData && (
          <>
            <InstructorRow
              following={following}
              instructor={courseData.instructor}
              onFollow={() => setFollowing((prev) => !prev)}
              onPress={() => router.push(instructorHref(courseData.instructor.id))}
            />

            <View style={styles.section}>
              <SectionHeader title="Mô tả" />
              <Text style={[styles.paragraph, { color: theme.textSecondary }]}>
                {expanded ? courseData.descriptionMore : courseData.description}
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
                {(courseData.benefits || []).map((benefit, idx) => (
                  <BenefitRow key={`${benefit}-${idx}`} label={benefit} />
                ))}
              </View>
            </View>

            <View style={styles.section}>
              <SectionHeader title="Khóa học tương tự" />
              {(courseData.related || []).map((item) => (
                <CourseMiniCard
                  key={item.id}
                  course={item}
                  onPress={() => router.push(courseOverviewHref(item.id))}
                />
              ))}
            </View>
          </>
        )}

        {activeTab === 'curriculum' && (
          <View style={{ gap: Spacing.three }}>
            {chaptersList.map((chapter) => (
              <ChapterSection
                key={chapter.id}
                chapter={chapter}
                onLessonPress={() => router.push(coursePlayerHref(courseId))}
              />
            ))}
          </View>
        )}

        {activeTab === 'reviews' && (
          <View style={{ gap: Spacing.three }}>
            <View style={styles.filterRow}>
              {REVIEW_FILTERS.map((item) => {
                const active = item.key === reviewFilter;
                return (
                  <Pressable
                    key={item.key}
                    onPress={() => setReviewFilter(item.key)}
                    style={[
                      styles.filterChip,
                      {
                        backgroundColor: active ? theme.primary : theme.backgroundElement,
                        borderColor: active ? theme.primary : theme.border,
                      },
                    ]}
                  >
                    <Text style={{ color: active ? '#ffffff' : theme.text, fontWeight: '600', fontSize: 13 }}>
                      {item.label}
                    </Text>
                  </Pressable>
                );
              })}
            </View>

            <View style={{ gap: Spacing.three }}>
              {filteredReviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))}
            </View>
          </View>
        )}
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
  filterRow: {
    flexDirection: 'row',
    gap: Spacing.two,
    marginVertical: Spacing.one,
  },
  filterChip: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.one,
    borderRadius: Radius.pill,
    borderWidth: 1,
  },
});
