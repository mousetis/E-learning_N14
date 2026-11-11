import { useEffect, useMemo, useState } from 'react';
import { useRouter } from '@/navigation/routerHook';
import { FlatList, Pressable, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { MyCourseCard } from '@/components/my-course-card';
import { BottomNavBar } from '@/components/bottom-bar';
import { useTheme } from '@/hooks/use-theme';
import { BottomTabInset, FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { useAppStore } from '@/store';
import { enrollmentService } from '@/services/enrollmentService';
import type { MyCourse } from '@/types/course';

type TabKey = 'all' | 'in-progress' | 'completed';

const tabLabels: { key: TabKey; label: string }[] = [
  { key: 'all', label: 'Tất cả' },
  { key: 'in-progress', label: 'Đang học' },
  { key: 'completed', label: 'Hoàn thành' },
];

export default function MyCoursesScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { user } = useAppStore();
  const [tab, setTab] = useState<TabKey>('all');
  const [allCourses, setAllCourses] = useState<MyCourse[]>([]);

  useEffect(() => {
    if (!user) {
      setAllCourses([]);
      return;
    }
    let mounted = true;
    (async () => {
      try {
        const res = await enrollmentService.getMyEnrollments();
        if (mounted && res && res.all && res.all.length > 0) {
          setAllCourses(res.all);
        } else if (mounted) {
          setAllCourses([]);
        }
      } catch (err) {
        if (mounted) setAllCourses([]);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [user]);

  const items = useMemo(() => {
    if (tab === 'all') return allCourses;
    if (tab === 'in-progress') return allCourses.filter((c) => c.progress < 100);
    return allCourses.filter((c) => c.progress >= 100);
  }, [allCourses, tab]);

  return (
    <SafeAreaView style={[styles.safe, { backgroundColor: theme.background }]} edges={['top']}>
      <FlatList
        data={items}
        keyExtractor={(item) => item.id}
        style={[styles.list, { maxWidth: MaxContentWidth }]}
        contentContainerStyle={{
          paddingHorizontal: Spacing.four,
          paddingTop: Spacing.three,
          paddingBottom: BottomTabInset + 60,
          gap: Spacing.three,
        }}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <View style={{ gap: Spacing.three, marginBottom: Spacing.two }}>
            <ThemedText style={[styles.h1, { color: theme.text }]} accessibilityRole="header">
              Khóa học của tôi
            </ThemedText>

            <View style={[styles.banner, { backgroundColor: theme.primary }]}>
              <ThemedText style={[styles.bannerText, { color: theme.onPrimary }]}>
                Khóa học thúc đẩy sự nghiệp
              </ThemedText>
              <Pressable
                accessibilityRole="button"
                accessibilityLabel="Xem ngay"
                onPress={() => router.push('/')}
                style={[styles.bannerCta, { backgroundColor: theme.accentYellow }]}
              >
                <ThemedText style={[styles.bannerCtaText, { color: theme.text }]}>Xem ngay</ThemedText>
              </Pressable>
            </View>

            <View style={styles.tabRow}>
              {tabLabels.map((t) => {
                const active = tab === t.key;
                return (
                  <Pressable
                    key={t.key}
                    onPress={() => setTab(t.key)}
                    accessibilityRole="tab"
                    accessibilityLabel={t.label}
                    accessibilityState={{ selected: active }}
                    hitSlop={6}
                  >
                    <ThemedText
                      style={[
                        styles.tab,
                        { color: active ? theme.primary : theme.muted, fontWeight: active ? '700' : '500' },
                      ]}
                    >
                      {t.label}
                    </ThemedText>
                  </Pressable>
                );
              })}
            </View>
          </View>
        }
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <ThemedText style={{ fontSize: 44, textAlign: 'center', marginBottom: Spacing.two }}>
              📚
            </ThemedText>
            <ThemedText style={[styles.emptyTitle, { color: theme.text }]}>
              {user ? 'Bạn chưa có khóa học nào' : 'Chưa đăng nhập'}
            </ThemedText>
            <ThemedText style={[styles.emptySubtitle, { color: theme.textSecondary }]}>
              {user
                ? 'Khám phá các khóa học để bắt đầu học tập ngay hôm nay!'
                : 'Vui lòng đăng nhập để xem tiến độ và các khóa học bạn đã đăng ký.'}
            </ThemedText>
            <Pressable
              onPress={() => router.push(user ? '/' : '/login')}
              style={[styles.emptyBtn, { backgroundColor: theme.primary }]}
            >
              <ThemedText style={{ color: '#fff', fontWeight: '700', fontSize: 14 }}>
                {user ? 'Khám phá khóa học' : 'Đăng nhập ngay'}
              </ThemedText>
            </Pressable>
          </View>
        }
        renderItem={({ item }) => (
          <MyCourseCard
            course={item}
            onPress={() => router.push(`/course/${item.id}/player` as any)}
          />
        )}
      />
      <BottomNavBar currentTab="courses" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1 },
  list: { width: '100%', alignSelf: 'center' },
  h1: { fontSize: FontSizes.xl, fontWeight: '700' },
  banner: { borderRadius: Radius.xl, padding: Spacing.four, gap: Spacing.three },
  bannerText: { fontSize: FontSizes.lg, fontWeight: '700' },
  bannerCta: {
    alignSelf: 'flex-start',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Radius.pill,
  },
  bannerCtaText: { fontSize: FontSizes.sm, fontWeight: '700' },
  tabRow: { flexDirection: 'row', gap: Spacing.four },
  tab: { fontSize: FontSizes.base },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: Spacing.six,
    paddingHorizontal: Spacing.four,
    gap: Spacing.two,
  },
  emptyTitle: {
    fontSize: FontSizes.lg,
    fontWeight: '700',
    textAlign: 'center',
  },
  emptySubtitle: {
    fontSize: FontSizes.sm,
    textAlign: 'center',
    lineHeight: 20,
    maxWidth: 280,
  },
  emptyBtn: {
    marginTop: Spacing.three,
    paddingHorizontal: Spacing.four,
    paddingVertical: Spacing.two + 2,
    borderRadius: Radius.pill,
  },
});
