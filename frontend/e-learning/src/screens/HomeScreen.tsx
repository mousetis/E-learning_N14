import { useState, useEffect } from 'react';
import { useRouter } from '@/navigation/routerHook';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
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

import { courseService } from '@/services/courseService';
import { useAppStore } from '@/store';
import type { Category } from '@/types/category';
import type { PopularCourse, FeaturedCourse } from '@/types/course';
import type { Instructor } from '@/types/instructor';

export default function HomeScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { user, unreadCount, cart } = useAppStore();
  const [bannerPressed, setBannerPressed] = useState(false);

  const [categoriesList, setCategoriesList] = useState<Category[]>([]);
  const [popularList, setPopularList] = useState<PopularCourse[]>([]);
  const [forYouList, setForYouList] = useState<FeaturedCourse[]>([]);
  const [instructorsList, setInstructorsList] = useState<Instructor[]>([]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [cats, pop, fy, ins] = await Promise.all([
          courseService.getCategories(),
          courseService.getPopularCourses(),
          courseService.getForYouCourses(),
          courseService.getInstructors(),
        ]);
        if (mounted) {
          if (cats && cats.length > 0) setCategoriesList(cats);
          if (pop && pop.length > 0) setPopularList(pop);
          if (fy && fy.length > 0) setForYouList(fy);
          if (ins && ins.length > 0) setInstructorsList(ins);
        }
      } catch (err) {
        console.warn('HomeScreen data loading error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const greetingName = user?.name ? user.name.split(' ').pop() : 'bạn';

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
            <ThemedText style={[styles.greeting, { color: theme.text }]}>
              Xin chào, {greetingName}!
            </ThemedText>
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
              {cart.length > 0 ? (
                <View style={[styles.cartBadge, { backgroundColor: theme.primary }]}>
                  <Text style={styles.badgeText}>{cart.length}</Text>
                </View>
              ) : null}
            </Pressable>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Thông báo"
              onPress={() => router.push('/notifications')}
              style={[styles.iconBtn, { backgroundColor: theme.surface }]}
            >
              <Icon sf="bell.fill" emoji="🔔" size={20} color={theme.primary} />
              {unreadCount > 0 ? (
                <View style={[styles.notificationBadge, { backgroundColor: theme.danger }]} />
              ) : null}
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
          onPress={() => router.push('/course/30000000-0000-0000-0000-000000000001')}
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
          onActionPress={() => router.push('/search')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {categoriesList.map((c) => (
            <CategoryCard
              key={c.id}
              category={c}
              onPress={() =>
                router.push({
                  pathname: '/search/results',
                  params: { categoryId: c.id, q: c.title, title: c.title },
                })
              }
            />
          ))}
        </ScrollView>

        {/* Popular */}
        <SectionHeader
          title="Khóa học phổ biến"
          onActionPress={() =>
            router.push({
              pathname: '/search/results',
              params: { title: 'Khóa học phổ biến', sort: 'popular' },
            })
          }
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {popularList.map((c) => (
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
          onActionPress={() =>
            router.push({
              pathname: '/search/results',
              params: { title: 'Dành cho bạn', sort: 'rating' },
            })
          }
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {forYouList.map((c) => (
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
          onActionPress={() =>
            router.push({
              pathname: '/search/results',
              params: { title: 'Khóa học truyền cảm hứng', sort: 'newest' },
            })
          }
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {forYouList.map((c) => (
            <FeaturedCourseCard
              key={`inspire-${c.id}`}
              course={c}
              onPress={() => router.push(`/course/${c.id}` as any)}
            />
          ))}
        </ScrollView>

        {/* Instructors */}
        <SectionHeader
          title="Giảng viên hàng đầu"
          onActionPress={() => router.push('/search')}
        />
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.horizontalRow}
        >
          {instructorsList.map((i) => (
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
    position: 'relative',
  },
  notificationBadge: {
    position: 'absolute',
    top: 8,
    right: 8,
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  cartBadge: {
    position: 'absolute',
    top: -2,
    right: -2,
    minWidth: 18,
    height: 18,
    borderRadius: 9,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 4,
    zIndex: 10,
  },
  badgeText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '700',
    textAlign: 'center',
    textAlignVertical: 'center',
    includeFontPadding: false,
    lineHeight: 12,
  },
  banner: {
    marginHorizontal: Spacing.four,
    marginBottom: Spacing.four,
    borderRadius: Radius.xl,
    padding: Spacing.four,
    overflow: 'hidden',
    position: 'relative',
    minHeight: 140,
    justifyContent: 'center',
  },
  bannerDecor: {
    position: 'absolute',
    right: -20,
    top: -20,
    width: 140,
    height: 140,
    borderRadius: 70,
    opacity: 0.25,
  },
  bannerInsideDecor: {
    position: 'absolute',
    left: 40,
    bottom: -30,
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#fff',
    opacity: 0.1,
  },
  bannerContent: { gap: Spacing.one },
  bannerKicker: { fontSize: FontSizes.xs, fontWeight: '700', letterSpacing: 0.8 },
  bannerTitle: { fontSize: FontSizes.xxl, fontWeight: '800' },
  bannerCta: {
    alignSelf: 'flex-start',
    paddingHorizontal: Spacing.three,
    paddingVertical: 6,
    borderRadius: Radius.pill,
    marginTop: Spacing.one,
  },
  bannerCtaText: { fontSize: FontSizes.xs, fontWeight: '700' },
  horizontalRow: {
    paddingHorizontal: Spacing.four,
    gap: Spacing.three,
    paddingBottom: Spacing.two,
  },
});
