import { StyleSheet, Text, View } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type OrDividerProps = {
  label?: string;
};

export function OrDivider({ label = 'hoặc' }: OrDividerProps) {
  const theme = useTheme();

  return (
    <View style={styles.row} accessibilityRole="text" accessibilityLabel={label}>
      <View style={[styles.line, { backgroundColor: theme.border }]} />
      <Text style={[styles.label, { color: theme.textSecondary }]}>{label}</Text>
      <View style={[styles.line, { backgroundColor: theme.border }]} />
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    alignItems: 'center',
    flexDirection: 'row',
    gap: Spacing.three,
  },
  line: {
    flex: 1,
    height: StyleSheet.hairlineWidth,
  },
  label: {
    fontSize: 13,
    fontWeight: '500',
  },
});
