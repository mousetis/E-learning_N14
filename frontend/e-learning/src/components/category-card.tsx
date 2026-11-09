import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { Icon } from '@/components/ui/icon';
import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { Category } from '@/types/category';

type CategoryCardProps = {
  category: Category;
  onPress?: () => void;
};

export function CategoryCard({ category, onPress }: CategoryCardProps) {
  const theme = useTheme();
  const label = category.title || category.label || 'Danh mục';
  const bgColor = category.tint || category.color || '#ece9fe';

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={label}
      style={({ pressed }) => [
        styles.card,
        { backgroundColor: theme.card, opacity: pressed ? 0.85 : 1 },
      ]}>
      <View style={[styles.iconTile, { backgroundColor: bgColor }]}>
        {category.image ? (
          <>
            <Image
              source={{ uri: category.image }}
              style={styles.tileImage}
              contentFit="cover"
              cachePolicy="memory-disk"
              priority="high"
            />
            <View style={styles.overlay} />
          </>
        ) : null}
        <Icon
          name={category.icon}
          sf={category.icon}
          glyph={category.glyph || category.emoji}
          emoji={category.emoji || category.glyph}
          size={category.image ? 22 : 20}
          color={category.image ? '#ffffff' : theme.text}
        />
      </View>
      <Text numberOfLines={1} style={[styles.label, { color: theme.text }]}>
        {label}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: 96,
    borderRadius: Radius.lg,
    paddingVertical: Spacing.three - 2,
    paddingHorizontal: Spacing.two,
    alignItems: 'center',
    gap: Spacing.two,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 1,
  },
  iconTile: {
    width: 52,
    height: 52,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    position: 'relative',
  },
  tileImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
  },
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(24, 21, 40, 0.35)',
  },
  label: {
    fontSize: 12,
    fontWeight: '700',
    textAlign: 'center',
  },
});
