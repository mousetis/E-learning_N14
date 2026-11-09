import { Pressable, StyleSheet, Text } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type ChipProps = {
  label: string;
  active?: boolean;
  onPress?: () => void;
};

export function Chip({ label, active = false, onPress }: ChipProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={label}
      accessibilityState={{ selected: active }}
      style={({ pressed }) => [
        styles.chip,
        {
          backgroundColor: active ? theme.primary : theme.card,
          borderColor: active ? theme.primary : theme.border,
          opacity: pressed ? 0.85 : 1,
        },
      ]}>
      <Text style={[styles.label, { color: active ? theme.onPrimary : theme.primary }]}>
        {label}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  chip: {
    borderRadius: Radius.pill,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two + 1,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
  },
});
