import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useEffect, useState } from 'react';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Chip } from '@/components/ui/chip';
import { PrimaryButton } from '@/components/ui/primary-button';
import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { courseService } from '@/services/courseService';
import type { Category } from '@/types/category';

const SORT_OPTIONS = [
  { id: 'popular', label: 'Phổ biến' },
  { id: 'newest', label: 'Mới nhất' },
  { id: 'rating', label: 'Đánh giá cao' },
  { id: 'price_asc', label: 'Giá thấp → cao' },
  { id: 'price_desc', label: 'Giá cao → thấp' },
];

const PRICE_PRESETS = [
  { id: 'all', label: 'Tất cả mức giá', min: undefined, max: undefined },
  { id: 'under-500k', label: 'Dưới 500k', min: 0, max: 500000 },
  { id: '500k-1m', label: '500k - 1 triệu', min: 500000, max: 1000000 },
  { id: 'above-1m', label: 'Trên 1 triệu', min: 1000000, max: 5000000 },
];

export default function SearchFilterScreen() {
  const theme = useTheme();
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams<{
    q?: string;
    categoryId?: string;
    sort?: string;
    minPrice?: string;
    maxPrice?: string;
  }>();

  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<string>(params.categoryId || '');
  const [sortId, setSortId] = useState<string>(params.sort || 'popular');
  const [selectedPriceId, setSelectedPriceId] = useState<string>(() => {
    if (params.minPrice === '0' && params.maxPrice === '500000') return 'under-500k';
    if (params.minPrice === '500000' && params.maxPrice === '1000000') return '500k-1m';
    if (params.minPrice === '1000000') return 'above-1m';
    return 'all';
  });

  useEffect(() => {
    courseService.getCategories().then((cats) => {
      if (Array.isArray(cats) && cats.length > 0) {
        setCategories(cats);
      }
    });
  }, []);

  const toggleCategory = (id: string) => {
    setSelectedCategoryId((curr) => (curr === id ? '' : id));
  };

  const resetFilters = () => {
    setSortId('popular');
    setSelectedCategoryId('');
    setSelectedPriceId('all');
  };

  const applyFilters = () => {
    const preset = PRICE_PRESETS.find((p) => p.id === selectedPriceId);
    router.replace({
      pathname: '/search/results',
      params: {
        q: params.q || '',
        categoryId: selectedCategoryId || '',
        sort: sortId,
        minPrice: preset?.min != null ? String(preset.min) : '',
        maxPrice: preset?.max != null ? String(preset.max) : '',
      },
    });
  };

  return (
    <View style={[styles.screen, { backgroundColor: theme.overlay }]}>
      <Pressable
        onPress={() => router.back()}
        accessibilityRole="button"
        accessibilityLabel="Đóng bộ lọc"
        style={styles.backdrop}
      />

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
          <Text style={[styles.title, { color: theme.text }]}>Bộ lọc khóa học</Text>
          <Pressable onPress={resetFilters} accessibilityRole="button" accessibilityLabel="Đặt lại">
            <Text style={[styles.reset, { color: theme.primary }]}>Đặt lại</Text>
          </Pressable>
        </View>

        <ScrollView
          style={styles.body}
          showsVerticalScrollIndicator={false}
          contentContainerStyle={styles.bodyContent}>
          <Text style={[styles.groupTitle, { color: theme.text }]}>Sắp xếp theo</Text>
          <View style={styles.chipRow}>
            {SORT_OPTIONS.map((option) => (
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
            <Chip
              label="Tất cả danh mục"
              active={!selectedCategoryId}
              onPress={() => setSelectedCategoryId('')}
            />
            {categories.map((category) => (
              <Chip
                key={category.id}
                label={category.title || category.label || ''}
                active={selectedCategoryId === category.id}
                onPress={() => toggleCategory(category.id)}
              />
            ))}
          </View>

          <View style={styles.priceHeader}>
            <Text style={[styles.groupTitleInline, { color: theme.text }]}>Mức giá</Text>
            <Text style={[styles.priceValue, { color: theme.primary }]}>
              {PRICE_PRESETS.find((p) => p.id === selectedPriceId)?.label}
            </Text>
          </View>
          <View style={styles.chipRow}>
            {PRICE_PRESETS.map((preset) => (
              <Chip
                key={preset.id}
                label={preset.label}
                active={selectedPriceId === preset.id}
                onPress={() => setSelectedPriceId(preset.id)}
              />
            ))}
          </View>
        </ScrollView>

        <PrimaryButton label="Áp dụng bộ lọc" onPress={applyFilters} />
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
