import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Card } from '@/components/ui/card';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { CartItem } from '@/types/cart';
import { formatUsd } from '@/utils/format';

const THUMBNAIL_LABEL_COLOR = '#181528';

export type CartItemRowProps = {
  item: CartItem;
  onRemove: (id: string) => void;
};

export function CartItemRow({ item, onRemove }: CartItemRowProps) {
  const theme = useTheme();

  return (
    <Card style={styles.card}>
      <View style={[styles.thumbnail, { backgroundColor: item.thumbnailColor }]}>
        <Text style={styles.thumbnailLabel}>{item.thumbnailLabel}</Text>
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
        <Text style={[styles.price, { color: theme.primary }]}>{formatUsd(item.price)}</Text>
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
  thumbnail: {
    width: 56,
    height: 56,
    borderRadius: Radius.lg,
    alignItems: 'center',
    justifyContent: 'center',
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
