import { StyleSheet, Text, View } from 'react-native';

import { FontSizes, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type InfoRowTone = 'default' | 'primary';

export type InfoRowProps = {
  label: string;
  value: string;
  emphasis?: boolean;
  tone?: InfoRowTone;
};

export function InfoRow({ label, value, emphasis = false, tone = 'default' }: InfoRowProps) {
  const theme = useTheme();
  const valueColor = tone === 'primary' ? theme.primary : theme.text;

  return (
    <View style={styles.row}>
      <Text
        numberOfLines={1}
        style={[styles.label, { color: theme.textSecondary }, emphasis && styles.labelEmphasis]}>
        {label}
      </Text>
      <Text style={[styles.value, { color: valueColor }, emphasis && styles.valueEmphasis]}>
        {value}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: Spacing.three,
    paddingVertical: Spacing.one,
  },
  label: {
    flexShrink: 1,
    fontSize: FontSizes.sm,
  },
  labelEmphasis: {
    fontSize: FontSizes.md,
    fontWeight: '600',
  },
  value: {
    fontSize: FontSizes.md,
    fontWeight: '600',
  },
  valueEmphasis: {
    fontSize: FontSizes.xl,
    fontWeight: '700',
  },
});
