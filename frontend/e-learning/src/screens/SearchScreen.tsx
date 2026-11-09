import { useRouter } from '@/navigation/routerHook';
import { useState } from 'react';
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
import { categories, featuredTopics, recommendedCourses } from '@/data/search';
import { useTheme } from '@/hooks/use-theme';

export default function SearchScreen() {
  const theme = useTheme();
  const router = useRouter();
  const [query, setQuery] = useState('');

  const openResults = (keyword: string) => {
    const value = keyword.trim().length > 0 ? keyword.trim() : 'Design';
    router.push({ pathname: '/search/results', params: { q: value } });
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
            {featuredTopics.map((topic) => (
              <Chip key={topic.id} label={topic.label} onPress={() => openResults(topic.label)} />
            ))}
          </View>
        </View>

        <View style={styles.section}>
          <SectionHeader
            title="Danh mục"
            actionLabel="Xem thêm"
            onAction={() => openResults('Danh mục')}
          />
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.horizontalRow}>
            {categories.map((category) => (
              <CategoryCard
                key={category.id}
                category={category}
                onPress={() => openResults(category.title)}
              />
            ))}
          </ScrollView>
        </View>

        <View style={styles.section}>
          <SectionHeader
            title="Dành cho bạn"
            actionLabel="Xem thêm"
            onAction={() => openResults('Design')}
          />
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.horizontalRow}>
            {recommendedCourses.map((course) => (
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
    paddingHorizontal: Spacing.three,
    paddingTop: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.four,
  },
  searchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two + 4,
  },
  section: {
    marginTop: Spacing.four,
  },
  chips: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
  },
  horizontalRow: {
    gap: Spacing.three - 4,
    paddingRight: Spacing.three,
  },
});
