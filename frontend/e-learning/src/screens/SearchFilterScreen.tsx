import { useRouter } from '@/navigation/routerHook';
import { useState } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Chip } from '@/components/ui/chip';
import { PriceRange } from '@/components/ui/price-range';
import { PrimaryButton } from '@/components/ui/primary-button';
import { Spacing } from '@/constants/theme';
import { filterCategories, priceRange, ratingOptions, sortOptions, totalResults } from '@/data/search';
import { useTheme } from '@/hooks/use-theme';

export default function SearchFilterScreen() {
  const theme = useTheme();
  const router = useRouter();
  const insets = useSafeAreaInsets();

  const [sortId, setSortId] = useState<string>(sortOptions[0].id);
  const [selectedCategories, setSelectedCategories] = useState<string[]>(['design']);
  const [ratingId, setRatingId] = useState<string>(ratingOptions[0].id);
  const [price] = useState<[number, number]>([priceRange.minimum, priceRange.maximum]);

  const toggleCategory = (id: string) => {
    setSelectedCategories((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    );
  };

  const resetFilters = () => {
    setSortId(sortOptions[0].id);
    setSelectedCategories([]);
    setRatingId(ratingOptions[0].id);
  };

  const applyFilters = () => {
    router.replace({ pathname: '/search/results', params: { q: 'Design' } });
  };

  return (
    <View style={[styles.screen, { backgroundColor: theme.overlay }]}>
      <Pressable
        onPress={() => router.back()}
        accessibilityRole="button"
        accessibilityLabel="Đóng bộ lọc"
        style={styles.backdrop}
      />

      <View style={[styles.countWrapper, { top: insets.top + Spacing.four }]}>
        <Text style={[styles.count, { color: theme.onPrimary }]}>{`${totalResults} kết quả`}</Text>
      </View>

      <View
        style={[
          styles.sheet,
          {
            backgroundColor: theme.sheet,
            paddingBottom: insets.bottom + Spacing.three,
          },
        ]}>
        <View style={[styles.handle, { backgroundColor: theme.handle }]} />

        <View style={styles.sheetHeader}>
          <Text style={[styles.title, { color: theme.text }]}>Bộ lọc</Text>
          <Pressable onPress={resetFilters} accessibilityRole="button" accessibilityLabel="Đặt lại">
            <Text style={[styles.reset, { color: theme.primary }]}>Đặt lại</Text>
          </Pressable>
        </View>

        <ScrollView
          style={styles.body}
          showsVerticalScrollIndicator={false}
          contentContainerStyle={styles.bodyContent}>
          <Text style={[styles.groupTitle, { color: theme.text }]}>Sắp xếp</Text>
          <View style={styles.chipRow}>
            {sortOptions.map((option) => (
              <Chip
                key={option.id}
                label={option.label}
                active={sortId === option.id}
                onPress={() => setSortId(option.id)}
              />
            ))}
          </View>

          <Text style={[styles.groupTitle, { color: theme.text }]}>Danh mục</Text>
          <View style={styles.chipRow}>
            {filterCategories.map((category) => (
              <Chip
                key={category.id}
                label={category.title}
                active={selectedCategories.includes(category.id)}
                onPress={() => toggleCategory(category.id)}
              />
            ))}
          </View>

          <View style={styles.priceHeader}>
            <Text style={[styles.groupTitleInline, { color: theme.text }]}>Mức giá</Text>
            <Text style={[styles.priceValue, { color: theme.primary }]}>
              {`$${price[0]} – $${price[1]}`}
            </Text>
          </View>
          <PriceRange
            minimum={priceRange.minimum}
            maximum={priceRange.maximum}
            low={price[0]}
            high={price[1]}
          />

          <Text style={[styles.groupTitle, { color: theme.text }]}>Đánh giá</Text>
          <View style={styles.chipRow}>
            {ratingOptions.map((option) => (
              <Chip
                key={option.id}
                label={option.label}
                active={ratingId === option.id}
                onPress={() => setRatingId(option.id)}
              />
            ))}
          </View>
        </ScrollView>

        <PrimaryButton label="Áp dụng" onPress={applyFilters} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  backdrop: {
    ...StyleSheet.absoluteFill,
  },
  countWrapper: {
    position: 'absolute',
    alignSelf: 'center',
  },
  count: {
    fontSize: 15,
    fontWeight: '600',
  },
  sheet: {
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    paddingHorizontal: Spacing.four - 4,
    paddingTop: Spacing.two + 2,
    maxHeight: '92%',
  },
  handle: {
    width: 40,
    height: 4,
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: Spacing.three,
  },
  sheetHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  title: {
    fontSize: 20,
    fontWeight: '700',
  },
  reset: {
    fontSize: 14,
    fontWeight: '600',
  },
  body: {
    flexGrow: 0,
  },
  bodyContent: {
    paddingBottom: Spacing.two,
  },
  groupTitle: {
    fontSize: 15,
    fontWeight: '700',
    marginTop: Spacing.four - 4,
    marginBottom: Spacing.two + 2,
  },
  groupTitleInline: {
    fontSize: 15,
    fontWeight: '700',
  },
  chipRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
  },
  priceHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: Spacing.four - 4,
  },
  priceValue: {
    fontSize: 14,
    fontWeight: '700',
  },
});
