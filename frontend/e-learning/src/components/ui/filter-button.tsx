import { Pressable, StyleSheet, Text } from 'react-native';

import { Icon } from '@/components/ui/icon';
import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type FilterButtonProps = {
  onPress?: () => void;
  label?: string;
};

export function FilterButton({ onPress, label = 'Lọc' }: FilterButtonProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={label}
      style={({ pressed }) => [
        styles.button,
        { backgroundColor: pressed ? theme.primaryPressed : theme.primary },
      ]}>
      <Icon name="slider.horizontal.3" glyph="☰" size={16} color={theme.onPrimary} />
      <Text style={[styles.label, { color: theme.onPrimary }]}>{label}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: Spacing.two,
    height: 52,
    borderRadius: Radius.lg,
    paddingHorizontal: Spacing.three + 2,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
  },
});
