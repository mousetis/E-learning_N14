import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';

import { CourseMiniCard } from '@/components/course/course-mini-card';
import { ReviewCard } from '@/components/course/review-card';
import { Avatar } from '@/components/ui/avatar';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import { SegmentedTabs, type SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import { courseOverviewHref } from '@/lib/routes';
import { courseService } from '@/services/courseService';
import { apiClient, ENDPOINTS } from '@/api';
import type { InstructorProfile } from '@/types/course';

const TABS: SegmentItem[] = [
  { key: 'overview', label: 'Tổng quan' },
  { key: 'courses', label: 'Khóa học' },
  { key: 'reviews', label: 'Đánh giá' },
];

const EMPTY_PROFILE: InstructorProfile = {
  id: '',
  name: 'Giảng viên',
  role: 'Chuyên gia đào tạo',
  location: 'Việt Nam',
  localTime: '',
  tone: 'pink',
  bio: '',
  stats: { rating: 5.0, students: 0, courses: 0 },
  designCourses: [],
  graphicCourses: [],
  reviews: [],
};

export default function InstructorProfileScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const [profile, setProfile] = useState<InstructorProfile>(EMPTY_PROFILE);
  const instructorId = typeof id === 'string' && id.length > 0 ? id : '';
  const theme = useAppTheme();
  const router = useRouter();
  const [tab, setTab] = useState<string>('courses');

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const detail = await courseService.getInstructorDetail(instructorId);
        if (mounted && detail) {
          let coursesGroup: any[] = [];
          try {
            coursesGroup = await apiClient.get<any[]>(ENDPOINTS.INSTRUCTORS.COURSES(instructorId));
          } catch {
            // ignore
          }

          let designCourses: any[] = [];
          if (Array.isArray(coursesGroup) && coursesGroup.length > 0) {
            const firstGroup = coursesGroup[0]?.courses || coursesGroup;
            if (firstGroup.length > 0) {
              designCourses = firstGroup.map((c: any) => ({
                id: c.id,
                title: c.title,
                author: detail.fullName || 'Giảng viên',
                meta: `${Math.round((c.totalDurationSeconds || 0) / 60)} phút · ★ ${c.avgRating || 4.8}`,
                tone: 'peach',
                badge: c.bestSeller ? 'Bán chạy' : undefined,
                image: c.thumbnailUrl || undefined,
              }));
            }
          }

          setProfile((prev) => ({
            ...prev,
            id: detail.id,
            name: detail.fullName,
            role: detail.jobTitle || detail.expertise || 'Giảng viên',
            location: detail.location || 'Việt Nam',
            bio: detail.introduction || prev.bio,
            avatarUri: detail.avatarUrl || prev.avatarUri,
            stats: {
              rating: Number(detail.avgRating || 4.8),
              students: detail.studentCount || prev.stats.students,
              courses: detail.courseCount || prev.stats.courses,
            },
            designCourses,
          }));
        }
      } catch (err) {
        console.warn('Instructor profile load error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [instructorId]);

  return (
    <Screen>
      <ScreenHeader title="Hồ sơ giảng viên" />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <View
          style={[
            styles.profile,
            { backgroundColor: theme.backgroundElement, borderColor: theme.border },
          ]}
        >
          <Avatar name={profile.name} size={72} tone={profile.tone} uri={profile.avatarUri} />
          <View style={styles.profileBody}>
            <View style={styles.nameRow}>
              <Text style={[styles.name, { color: theme.text }]}>{profile.name}</Text>
              <View style={[styles.badge, { backgroundColor: theme.gold }]}>
                <Text style={[styles.badgeText, { color: theme.onPastel }]}>Giảng viên</Text>
              </View>
            </View>
            <Text style={[styles.meta, { color: theme.textSecondary }]}>
              {`${profile.role} · ${profile.location}, ${profile.localTime}`}
            </Text>
          </View>
        </View>

        <SegmentedTabs activeKey={tab} items={TABS} onSelect={setTab} />

        {tab === 'overview' ? (
          <View style={styles.section}>
            <SectionHeader title="Giới thiệu" />
            <Text style={[styles.bio, { color: theme.textSecondary }]}>{profile.bio}</Text>
            <View style={styles.stats}>
              <View style={styles.stat}>
                <Text style={[styles.statValue, { color: theme.text }]}>
                  {profile.stats.rating.toFixed(1)}
                </Text>
                <Text style={[styles.statLabel, { color: theme.textSecondary }]}>Đánh giá</Text>
              </View>
              <View style={styles.stat}>
                <Text style={[styles.statValue, { color: theme.text }]}>
                  {profile.stats.students.toLocaleString('vi-VN')}
                </Text>
                <Text style={[styles.statLabel, { color: theme.textSecondary }]}>Học viên</Text>
              </View>
              <View style={styles.stat}>
                <Text style={[styles.statValue, { color: theme.text }]}>
                  {profile.stats.courses}
                </Text>
                <Text style={[styles.statLabel, { color: theme.textSecondary }]}>Khóa học</Text>
              </View>
            </View>
          </View>
        ) : null}

        {tab === 'courses' ? (
          <View style={styles.sectionGroup}>
            <View style={styles.section}>
              <SectionHeader title="Khóa học đang giảng dạy" />
              {profile.designCourses.map((item) => (
                <CourseMiniCard
                  key={item.id}
                  course={item}
                  onPress={() => router.push(courseOverviewHref(item.id))}
                />
              ))}
            </View>
          </View>
        ) : null}

        {tab === 'reviews' ? (
          <View style={styles.section}>
            <SectionHeader title="Đánh giá từ học viên" />
            {profile.reviews.map((item) => (
              <ReviewCard key={item.id} review={item} />
            ))}
          </View>
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
  profile: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: Spacing.three,
    borderRadius: Radius.lg,
    borderWidth: 1,
    gap: Spacing.three,
  },
  profileBody: {
    flex: 1,
    gap: 2,
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  name: {
    fontSize: 16,
    fontWeight: '700',
  },
  badge: {
    paddingHorizontal: Spacing.two,
    paddingVertical: 2,
    borderRadius: Radius.pill,
  },
  badgeText: {
    fontSize: 10,
    fontWeight: '700',
  },
  meta: {
    fontSize: 12,
  },
  sectionGroup: {
    gap: Spacing.three,
  },
  section: {
    gap: Spacing.two,
  },
  bio: {
    fontSize: 13,
    lineHeight: 20,
  },
  stats: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: Spacing.two,
  },
  stat: {
    alignItems: 'center',
    flex: 1,
  },
  statValue: {
    fontSize: 16,
    fontWeight: '700',
  },
  statLabel: {
    fontSize: 11,
  },
});
