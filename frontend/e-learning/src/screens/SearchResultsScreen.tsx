import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useEffect, useState } from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { CourseRow } from '@/components/course-row';
import { ThemedText } from '@/components/themed-text';
import { FilterButton } from '@/components/ui/filter-button';
import { SearchInput } from '@/components/ui/search-input';
import { BottomNavBar } from '@/components/bottom-bar';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { courseService } from '@/services/courseService';
import type { Course } from '@/types/course';

const PASTEL_COLORS = ['#c9bcff', '#ffd6a3', '#b2eedf', '#ffbfd2', '#ffe8a3', '#d0e1fd'];

export default function SearchResultsScreen() {
  const theme = useTheme();
  const router = useRouter();
  const params = useLocalSearchParams<{
    q?: string;
    categoryId?: string;
    title?: string;
    minPrice?: string;
    maxPrice?: string;
    sort?: string;
    rating?: string;
  }>();
  const initialQuery = typeof params.q === 'string' ? params.q : '';
  const [query, setQuery] = useState(initialQuery);
  const [results, setResults] = useState<Course[]>([]);
  const [totalCount, setTotalCount] = useState<number>(0);
  const [loading, setLoading] = useState(false);

  const performSearch = async (keyword?: string) => {
    try {
      setLoading(true);
      const res = await courseService.searchCourses({
        keyword: keyword && keyword.trim().length > 0 ? keyword.trim() : undefined,
        categoryId: params.categoryId || undefined,
        minPrice: params.minPrice ? Number(params.minPrice) : undefined,
        maxPrice: params.maxPrice ? Number(params.maxPrice) : undefined,
        sort: params.sort || undefined,
      });
      const items = res?.items || res;
      if (Array.isArray(items) && items.length > 0) {
        const mapped: Course[] = items.map((c: any, idx: number) => ({
          id: c.id,
          title: c.title,
          author: c.instructor?.fullName || 'Giảng viên',
          price: Number(c.salePrice ?? c.originalPrice ?? 499000),
          rating: Number(c.avgRating ?? 4.8),
          lessons: c.totalLessons || 12,
          tint: PASTEL_COLORS[idx % PASTEL_COLORS.length],
          coverUri: c.thumbnailUrl || undefined,
          image: c.thumbnailUrl || undefined,
        }));
        setResults(mapped);
        setTotalCount(res.totalElements ?? mapped.length);
        return;
      }
      setResults([]);
      setTotalCount(0);
    } catch {
      setResults([]);
      setTotalCount(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setQuery(initialQuery);
    performSearch(initialQuery);
  }, [params.q, params.categoryId, params.minPrice, params.maxPrice, params.sort]);

  return (
    <SafeAreaView style={[styles.screen, { backgroundColor: theme.background }]} edges={['top']}>
      <FlatList
        data={results}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <CourseRow
            course={item}
            onPress={() => router.push(`/course/${item.id}` as any)}
          />
        )}
        showsVerticalScrollIndicator={false}
        contentContainerStyle={[styles.list, { paddingBottom: BottomTabInset + 70 }]}
        ListHeaderComponent={
          <View style={styles.header}>
            <View style={styles.searchRow}>
              <SearchInput
                value={query}
                onChangeText={setQuery}
                onSubmitEditing={() => performSearch(query)}
              />
              <FilterButton
                onPress={() =>
                  router.push({
                    pathname: '/search/filter',
                    params: {
                      q: query,
                      categoryId: params.categoryId,
                      sort: params.sort,
                      minPrice: params.minPrice,
                      maxPrice: params.maxPrice,
                    },
                  })
                }
              />
            </View>
            <ThemedText type="smallBold" style={styles.count}>
              {loading ? 'Đang tìm kiếm...' : `${totalCount} kết quả`}
            </ThemedText>
          </View>
        }
      />
      <BottomNavBar currentTab="search" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  list: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    paddingHorizontal: Spacing.three,
    paddingTop: Spacing.three,
  },
  header: {
    gap: Spacing.three,
    marginBottom: Spacing.two,
  },
  searchRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two + 4,
  },
  count: {
    fontSize: 15,
    fontWeight: '700',
  },
});
