import { Pressable, StyleSheet, Text } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type FooterLinkProps = {
  question: string;
  action: string;
  onPress: () => void;
  accessibilityLabel?: string;
};

export function FooterLink({ question, action, onPress, accessibilityLabel }: FooterLinkProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="link"
      accessibilityLabel={accessibilityLabel ?? `${question} ${action}`}
      hitSlop={Spacing.two}
      style={styles.pressable}
    >
      <Text style={[styles.text, { color: theme.textSecondary }]}>
        {question}{' '}
        <Text style={[styles.action, { color: theme.primary }]}>{action}</Text>
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  pressable: {
    alignSelf: 'center',
    paddingVertical: Spacing.two,
  },
  text: {
    fontSize: 14,
    lineHeight: 20,
    textAlign: 'center',
  },
  action: {
    fontWeight: '600',
  },
});
