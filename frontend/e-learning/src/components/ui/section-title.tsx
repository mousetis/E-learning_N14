import { StyleSheet, Text } from 'react-native';

import { FontSizes, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type SectionTitleProps = {
  children: string;
};

export function SectionTitle({ children }: SectionTitleProps) {
  const theme = useTheme();

  return <Text style={[styles.title, { color: theme.text }]}>{children}</Text>;
}

const styles = StyleSheet.create({
  title: {
    fontSize: FontSizes.md,
    fontWeight: '700',
    marginTop: Spacing.one,
    marginBottom: Spacing.one,
  },
});
