import { StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  label: string;
};

export function BenefitRow({ label }: Props) {
  const theme = useAppTheme();

  return (
    <View style={styles.row}>
      <View style={[styles.dot, { backgroundColor: theme.primarySoft }]}>
        <Text style={[styles.check, { color: theme.primary }]}>✓</Text>
      </View>
      <Text style={[styles.label, { color: theme.text }]}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  dot: {
    width: 22,
    height: 22,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  check: {
    fontSize: 12,
    fontWeight: '700',
  },
  label: {
    flex: 1,
    fontSize: 14,
    lineHeight: 20,
  },
});
