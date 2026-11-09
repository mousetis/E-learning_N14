import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useMemo, useState } from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { CourseRow } from '@/components/course-row';
import { ThemedText } from '@/components/themed-text';
import { FilterButton } from '@/components/ui/filter-button';
import { SearchInput } from '@/components/ui/search-input';
import { BottomNavBar } from '@/components/bottom-bar';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { searchResults, totalResults } from '@/data/search';
import { useTheme } from '@/hooks/use-theme';

export default function SearchResultsScreen() {
  const theme = useTheme();
  const router = useRouter();
  const params = useLocalSearchParams<{ q?: string }>();
  const initialQuery = typeof params.q === 'string' && params.q.length > 0 ? params.q : 'Design';
  const [query, setQuery] = useState(initialQuery);

  const results = useMemo(() => {
    const term = query.trim().toLowerCase();
    if (term.length === 0) {
      return searchResults;
    }
    const matches = searchResults.filter(
      (course) =>
        course.title.toLowerCase().includes(term) || course.author.toLowerCase().includes(term),
    );
    return matches.length > 0 ? matches : searchResults;
  }, [query]);

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
                onSubmitEditing={() => setQuery(query.trim())}
              />
              <FilterButton onPress={() => router.push('/search/filter')} />
            </View>
            <ThemedText type="smallBold" style={styles.count}>
              {`${totalResults} kết quả`}
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
