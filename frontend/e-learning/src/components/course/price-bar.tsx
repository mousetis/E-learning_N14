import { StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { PrimaryButton } from '@/components/ui/primary-button';
import { MaxContentWidth, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  price: string;
  discount: string;
  actionLabel?: string;
  onPress?: () => void;
};

export function PriceBar({ price, discount, actionLabel = 'Thêm vào giỏ', onPress }: Props) {
  const theme = useAppTheme();
  const insets = useSafeAreaInsets();

  return (
    <View
      style={[
        styles.bar,
        {
          backgroundColor: theme.backgroundElement,
          borderTopColor: theme.border,
          paddingBottom: insets.bottom + Spacing.three,
        },
      ]}
    >
      <View style={styles.inner}>
        <View style={styles.priceBlock}>
          <Text style={[styles.price, { color: theme.text }]}>{price}</Text>
          <Text style={[styles.discount, { color: theme.textSecondary }]}>{discount}</Text>
        </View>
        <PrimaryButton label={actionLabel} onPress={onPress} style={styles.action} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  bar: {
    borderTopWidth: 1,
    paddingTop: Spacing.three,
    paddingHorizontal: Spacing.three,
  },
  inner: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.three,
  },
  priceBlock: {
    gap: Spacing.half,
  },
  price: {
    fontSize: 20,
    fontWeight: '700',
  },
  discount: {
    fontSize: 12,
  },
  action: {
    flex: 1,
  },
});
