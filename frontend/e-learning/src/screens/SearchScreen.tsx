import { useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { ScrollView, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { CategoryCard } from '@/components/category-card';
import { CourseCard } from '@/components/course-card';
import { Chip } from '@/components/ui/chip';
import { FilterButton } from '@/components/ui/filter-button';
import { SearchInput } from '@/components/ui/search-input';
import { SectionHeader } from '@/components/ui/section-header';
import { BottomNavBar } from '@/components/bottom-bar';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { courseService } from '@/services/courseService';
import type { Category } from '@/types/category';
import type { Course } from '@/types/course';

export default function SearchScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [query, setQuery] = useState('');
  const [categoriesList, setCategoriesList] = useState<Category[]>([]);
  const [topicsList, setTopicsList] = useState<{ id: string; label: string }[]>([]);
  const [recommendedList, setRecommendedList] = useState<Course[]>([]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const [cats, topics, coursesRes] = await Promise.all([
          courseService.getCategories(),
          courseService.getHotTopics(),
          courseService.getForYouCourses(),
        ]);
        if (mounted) {
          if (cats && cats.length > 0) setCategoriesList(cats);
          if (topics && topics.length > 0) setTopicsList(topics);
          if (coursesRes && coursesRes.length > 0) {
            setRecommendedList(
              coursesRes.map((c) => ({
                id: c.id,
                title: c.title,
                author: c.author,
                price: 499000,
                rating: 4.8,
                lessons: 16,
                tint: c.coverColor,
                coverUri: c.image,
                image: c.image,
              })),
            );
          }
        }
      } catch (err) {
        console.warn('Search screen data load error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const openResults = (keyword: string) => {
    router.push({ pathname: '/search/results', params: { q: keyword.trim() } });
  };

  return (
    <SafeAreaView style={[styles.screen, { backgroundColor: theme.background }]} edges={['top']}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.content}>
        <View style={styles.searchRow}>
          <SearchInput
            value={query}
            onChangeText={setQuery}
            onSubmitEditing={() => openResults(query)}
          />
          <FilterButton onPress={() => router.push('/search/filter')} />
        </View>

        <View style={styles.section}>
          <SectionHeader title="Chủ đề nổi bật" />
          <View style={styles.chips}>
            {topicsList.map((topic) => (
              <Chip key={topic.id} label={topic.label} onPress={() => openResults(topic.label)} />
            ))}
          </View>
        </View>

        <View style={styles.section}>
          <SectionHeader
            title="Danh mục"
            actionLabel="Xem thêm"
            onAction={() => router.push({ pathname: '/search/results', params: { title: 'Tất cả khóa học' } })}
          />
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.horizontalRow}>
            {categoriesList.map((category) => (
              <CategoryCard
                key={category.id}
                category={category}
                onPress={() =>
                  router.push({
                    pathname: '/search/results',
                    params: {
                      categoryId: category.id,
                      q: category.title || category.label || '',
                      title: category.title || category.label || '',
                    },
                  })
                }
              />
            ))}
          </ScrollView>
        </View>

        <View style={styles.section}>
          <SectionHeader
            title="Dành cho bạn"
            actionLabel="Xem thêm"
            onAction={() =>
              router.push({
                pathname: '/search/results',
                params: { title: 'Dành cho bạn', sort: 'rating' },
              })
            }
          />
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.horizontalRow}>
            {recommendedList.map((course) => (
              <CourseCard
                key={course.id}
                course={course}
                onPress={() => router.push(`/course/${course.id}` as any)}
              />
            ))}
          </ScrollView>
        </View>
      </ScrollView>
      <BottomNavBar currentTab="search" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  content: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    paddingTop: Spacing.two,
    paddingBottom: BottomTabInset + 60,
    gap: Spacing.four,
  },
  searchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.four,
    gap: Spacing.two + 4,
  },
  section: {
    gap: Spacing.two,
  },
  chips: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
    paddingHorizontal: Spacing.four,
  },
  horizontalRow: {
    paddingHorizontal: Spacing.four,
    gap: Spacing.three,
  },
});
