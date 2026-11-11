import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { Card } from '@/components/ui/card';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { CartItem } from '@/types/cart';
import { formatVnd } from '@/utils/format';

const THUMBNAIL_LABEL_COLOR = '#181528';

export type CartItemRowProps = {
  item: CartItem;
  onRemove: (id: string) => void;
  selected?: boolean;
  onToggleSelect?: (id: string) => void;
};

export function CartItemRow({ item, onRemove, selected = true, onToggleSelect }: CartItemRowProps) {
  const theme = useTheme();

  return (
    <Card style={styles.card}>
      {onToggleSelect ? (
        <Pressable
          accessibilityRole="checkbox"
          accessibilityState={{ checked: selected }}
          onPress={() => onToggleSelect(item.id)}
          hitSlop={8}
          style={[
            styles.checkbox,
            {
              borderColor: selected ? theme.primary : theme.border,
              backgroundColor: selected ? theme.primary : 'transparent',
            },
          ]}
        >
          {selected ? <Text style={styles.checkmark}>✓</Text> : null}
        </Pressable>
      ) : null}

      <View style={[styles.thumbnail, { backgroundColor: item.thumbnailColor }]}>
        {item.image ? (
          <Image
            source={{ uri: item.image }}
            style={styles.thumbnailImage}
            contentFit="cover"
            cachePolicy="memory-disk"
          />
        ) : (
          <Text style={styles.thumbnailLabel}>{item.thumbnailLabel}</Text>
        )}
      </View>

      <View style={styles.info}>
        <Text numberOfLines={2} style={[styles.title, { color: theme.text }]}>
          {item.title}
        </Text>
        <Text numberOfLines={1} style={[styles.author, { color: theme.textSecondary }]}>
          {item.author}
        </Text>
      </View>

      <View style={styles.trailing}>
        <Text style={[styles.price, { color: theme.primary }]}>{formatVnd(item.price)}</Text>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={`Xóa ${item.title} khỏi giỏ hàng`}
          hitSlop={8}
          onPress={() => onRemove(item.id)}>
          {({ pressed }) => (
            <Text style={[styles.remove, { color: theme.danger, opacity: pressed ? 0.6 : 1 }]}>
              Xóa
            </Text>
          )}
        </Pressable>
      </View>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.three,
    padding: Spacing.three,
  },
  checkbox: {
    width: 22,
    height: 22,
    borderRadius: Radius.sm,
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkmark: {
    color: '#ffffff',
    fontSize: 13,
    fontWeight: '800',
    lineHeight: 16,
  },
  thumbnail: {
    width: 56,
    height: 56,
    borderRadius: Radius.lg,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  thumbnailImage: {
    width: '100%',
    height: '100%',
  },
  thumbnailLabel: {
    fontSize: FontSizes.md,
    fontWeight: '700',
    color: THUMBNAIL_LABEL_COLOR,
  },
  info: {
    flex: 1,
    gap: Spacing.half,
  },
  title: {
    fontSize: FontSizes.md,
    fontWeight: '600',
  },
  author: {
    fontSize: FontSizes.sm,
  },
  trailing: {
    alignItems: 'flex-end',
    gap: Spacing.one,
  },
  price: {
    fontSize: FontSizes.md,
    fontWeight: '700',
  },
  remove: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
  },
});
