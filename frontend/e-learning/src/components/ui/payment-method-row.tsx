import { Pressable, StyleSheet, Text, View } from 'react-native';

import { FontSizes, Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { PaymentMethod } from '@/types/settings';

export type PaymentMethodRowProps = {
  method: PaymentMethod;
  selected: boolean;
  onSelect: (id: string) => void;
};

export function PaymentMethodRow({ method, selected, onSelect }: PaymentMethodRowProps) {
  const theme = useTheme();

  return (
    <Pressable
      accessibilityRole="radio"
      accessibilityState={{ selected }}
      accessibilityLabel={method.label}
      onPress={() => onSelect(method.id)}
      style={({ pressed }) => [
        styles.row,
        {
          backgroundColor: theme.backgroundElement,
          borderColor: selected ? theme.primary : theme.border,
          opacity: pressed ? 0.85 : 1,
          shadowColor: theme.shadow,
        },
      ]}>
      <View style={[styles.icon, { backgroundColor: method.iconBackground }]}>
        <Text style={styles.iconGlyph}>{method.icon}</Text>
      </View>

      <Text style={[styles.label, { color: theme.text }]}>{method.label}</Text>

      <View style={[styles.radio, { borderColor: selected ? theme.primary : theme.borderStrong }]}>
        {selected ? <View style={[styles.radioDot, { backgroundColor: theme.primary }]} /> : null}
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.three,
    padding: Spacing.three,
    borderRadius: Radius.lg,
    borderWidth: 1,
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 3 },
    elevation: 1,
  },
  icon: {
    width: 44,
    height: 44,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconGlyph: {
    fontSize: FontSizes.xl,
  },
  label: {
    flex: 1,
    fontSize: FontSizes.md,
    fontWeight: '600',
  },
  radio: {
    width: 22,
    height: 22,
    borderRadius: Radius.pill,
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
  },
  radioDot: {
    width: 10,
    height: 10,
    borderRadius: Radius.pill,
  },
});
