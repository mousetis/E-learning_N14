import { useState } from 'react';
import { useRouter } from '@/navigation/routerHook';
import { Pressable, ScrollView, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { Icon } from '@/components/ui/icon';
import { SectionHeader } from '@/components/section-header';
import { CategoryCard } from '@/components/category-card';
import { PopularCourseCard } from '@/components/popular-course-card';
import { FeaturedCourseCard } from '@/components/featured-course-card';
import { InstructorCard } from '@/components/instructor-card';
import { BottomNavBar } from '@/components/bottom-bar';
import { useTheme } from '@/hooks/use-theme';
import { BottomTabInset, FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';

import { categories } from '@/data/categories';
import { popularCourses } from '@/data/popular-courses';
import { forYouCourses } from '@/data/for-you';
import { inspirationCourses } from '@/data/inspiration';
import { instructors } from '@/data/instructors';

export default function HomeScreen() {
  const router = useRouter();
  const theme = useTheme();
  const [bannerPressed, setBannerPressed] = useState(false);

  return (
    <SafeAreaView style={[styles.safe, { backgroundColor: theme.chipBackground }]} edges={['top']}>
      <ScrollView
        style={[styles.scroll, { maxWidth: MaxContentWidth }]}
        contentContainerStyle={{ paddingTop: Spacing.three, paddingBottom: BottomTabInset + 60 }}
        showsVerticalScrollIndicator={false}
      >
        {/* Header */}
        <View style={styles.header}>
          <View style={styles.headerText}>
            <ThemedText style={[styles.greeting, { color: theme.text }]}>Xin chào, Rosie!</ThemedText>
            <ThemedText style={[styles.subtitle, { color: theme.textSecondary }]}>
              Hôm nay bạn muốn học gì?
            </ThemedText>
          </View>
          <View style={styles.headerActions}>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Giỏ hàng"
              onPress={() => router.push('/cart')}
              style={[styles.iconBtn, { backgroundColor: theme.surface }]}
            >
              <Icon sf="cart.fill" emoji="🛒" size={20} color={theme.primary} />
            </Pressable>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Thông báo"
              onPress={() => router.push('/notifications')}
              style={[styles.iconBtn, { backgroundColor: theme.surface }]}
            >
              <Icon sf="bell.fill" emoji="🔔" size={20} color={theme.primary} />
              <View style={[styles.notificationBadge, { backgroundColor: theme.danger }]} />
            </Pressable>
          </View>
        </View>

        {/* Quick Search Bar */}
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Tìm kiếm khóa học"
          onPress={() => router.push('/search')}
          style={[styles.searchBar, { backgroundColor: theme.surface, borderColor: theme.border }]}
        >
          <Icon sf="magnifyingglass" emoji="🔍" size={18} color={theme.textSecondary} />
          <ThemedText style={[styles.searchPlaceholder, { color: theme.textSecondary }]}>
            Tìm khóa học, chủ đề, giảng viên...
          </ThemedText>
          <View style={[styles.filterBadge, { backgroundColor: theme.primarySoft }]}>
            <Icon sf="slider.horizontal.3" emoji="⚙️" size={14} color={theme.primary} />
          </View>
        </Pressable>

        {/* Banner */}
        <Pressable
          onPress={() => router.push('/course/ux-foundation')}
          accessibilityRole="button"
          accessibilityLabel="Khóa học Quản lý dự án giảm 20%"
          style={[styles.banner, { backgroundColor: theme.banner }]}
        >
          <View style={[styles.bannerDecor, { backgroundColor: theme.bannerDecoration }]} />
          <View style={styles.bannerContent}>
            <ThemedText style={[styles.bannerKicker, { color: theme.onPrimary }]}>
              KHÓA HỌC QUẢN LÝ DỰ ÁN
            </ThemedText>
            <ThemedText style={[styles.bannerTitle, { color: theme.onPrimary }]}>Giảm 20%</ThemedText>
            <View style={[styles.bannerCta, { backgroundColor: theme.accentYellow }]}>
              <ThemedText style={[styles.bannerCtaText, { color: theme.text }]}>Tham gia ngay</ThemedText>
            </View>
          </View>
          {bannerPressed ? <View style={styles.bannerInsideDecor} /> : null}
        </Pressable>

        {/* Categories */}
        <SectionHeader
          title="Danh mục"
          onActionPress={() => router.push('/search/results')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {categories.map((c) => (
            <CategoryCard
              key={c.id}
              category={c}
              onPress={() => router.push('/search/results')}
            />
          ))}
        </ScrollView>

        {/* Popular */}
        <SectionHeader
          title="Khóa học phổ biến"
          onActionPress={() => router.push('/search/results')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {popularCourses.map((c) => (
            <PopularCourseCard
              key={c.id}
              course={c}
              onPress={() => router.push(`/course/${c.id}` as any)}
            />
          ))}
        </ScrollView>

        {/* For you */}
        <SectionHeader
          title="Dành cho bạn"
          onActionPress={() => router.push('/search/results')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {forYouCourses.map((c) => (
            <FeaturedCourseCard
              key={c.id}
              course={c}
              onPress={() => router.push(`/course/${c.id}` as any)}
            />
          ))}
        </ScrollView>

        {/* Inspiration */}
        <SectionHeader
          title="Khóa học truyền cảm hứng"
          onActionPress={() => router.push('/search/results')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {inspirationCourses.map((c) => (
            <FeaturedCourseCard
              key={c.id}
              course={c}
              onPress={() => router.push(`/course/${c.id}` as any)}
            />
          ))}
        </ScrollView>

        {/* Instructors */}
        <SectionHeader
          title="Giảng viên hàng đầu"
          onActionPress={() => router.push('/instructor/sara-weise')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {instructors.map((i) => (
            <InstructorCard
              key={i.id}
              instructor={i}
              onPress={() => router.push(`/instructor/${i.id}` as any)}
            />
          ))}
        </ScrollView>
      </ScrollView>

      {/* Bottom Navigation */}
      <BottomNavBar currentTab="home" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1 },
  scroll: { width: '100%', alignSelf: 'center' },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    paddingHorizontal: Spacing.four,
    marginBottom: Spacing.two,
  },
  headerText: { flex: 1 },
  headerActions: { flexDirection: 'row', gap: Spacing.two },
  greeting: { fontSize: FontSizes.xl, fontWeight: '700' },
  subtitle: { fontSize: FontSizes.base, marginTop: 2 },
  searchBar: {
    marginHorizontal: Spacing.four,
    marginBottom: Spacing.three,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.three,
    paddingVertical: 10,
    borderRadius: Radius.lg,
    borderWidth: 1,
    gap: Spacing.two,
  },
  searchPlaceholder: {
    flex: 1,
    fontSize: FontSizes.sm,
  },
  filterBadge: {
    width: 28,
    height: 28,
    borderRadius: Radius.sm,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconBtn: {
    width: 44,
    height: 44,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
    position: 'relative',
  },
  notificationBadge: {
    position: 'absolute',
    top: 10,
    right: 11,
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  banner: {
    marginHorizontal: Spacing.four,
    borderRadius: Radius.xl,
    overflow: 'hidden',
    padding: Spacing.four,
    minHeight: 150,
    justifyContent: 'center',
  },
  bannerDecor: {
    position: 'absolute',
    width: 220,
    height: 220,
    borderRadius: 110,
    right: -70,
    top: -80,
    opacity: 0.9,
  },
  bannerInsideDecor: {
    position: 'absolute',
    width: 120,
    height: 120,
    borderRadius: 60,
    right: 16,
    bottom: -40,
    backgroundColor: 'rgba(255,255,255,0.12)',
  },
  bannerContent: { gap: Spacing.two },
  bannerKicker: { fontSize: FontSizes.xs, fontWeight: '700', letterSpacing: 1, opacity: 0.85 },
  bannerTitle: { fontSize: FontSizes.xxl, fontWeight: '800' },
  bannerCta: {
    marginTop: Spacing.two,
    alignSelf: 'flex-start',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Radius.pill,
  },
  bannerCtaText: { fontSize: FontSizes.sm, fontWeight: '700' },
  horizontalRow: {
    paddingHorizontal: Spacing.four,
    gap: Spacing.three,
    paddingBottom: Spacing.two,
  },
});
