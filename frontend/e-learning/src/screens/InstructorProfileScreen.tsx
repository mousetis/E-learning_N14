import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';

import { CourseMiniCard } from '@/components/course/course-mini-card';
import { ReviewCard } from '@/components/course/review-card';
import { Avatar } from '@/components/ui/avatar';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import { SegmentedTabs, type SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { instructorProfile } from '@/data/instructor';
import { useAppTheme } from '@/hooks/use-app-theme';
import { courseOverviewHref } from '@/lib/routes';

const TABS: SegmentItem[] = [
  { key: 'overview', label: 'Tổng quan' },
  { key: 'courses', label: 'Khóa học' },
  { key: 'reviews', label: 'Đánh giá' },
];

export default function InstructorProfileScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const profile = instructorProfile;
  const instructorId = typeof id === 'string' && id.length > 0 ? id : profile.id;
  const theme = useAppTheme();
  const router = useRouter();
  const [tab, setTab] = useState<string>('courses');

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
              <SectionHeader title="UI/UX Design" />
              {profile.designCourses.map((item) => (
                <CourseMiniCard
                  key={item.id}
                  course={item}
                  onPress={() => router.push(courseOverviewHref(item.id))}
                />
              ))}
            </View>
            <View style={styles.section}>
              <SectionHeader title="Thiết kế đồ họa" />
              {profile.graphicCourses.map((item) => (
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
            <SectionHeader title="Top đánh giá" />
            {profile.reviews.map((review) => (
              <ReviewCard key={`${instructorId}-${review.id}`} review={review} />
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
    gap: Spacing.three,
    borderRadius: Radius.xl,
    borderWidth: 1,
    padding: Spacing.three,
  },
  profileBody: {
    flex: 1,
    gap: Spacing.one,
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  name: {
    fontSize: 19,
    fontWeight: '700',
  },
  badge: {
    paddingHorizontal: Spacing.two,
    paddingVertical: Spacing.half,
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
    gap: Spacing.four,
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
    gap: Spacing.three,
  },
  stat: {
    flex: 1,
    gap: Spacing.half,
  },
  statValue: {
    fontSize: 16,
    fontWeight: '700',
  },
  statLabel: {
    fontSize: 12,
  },
});
