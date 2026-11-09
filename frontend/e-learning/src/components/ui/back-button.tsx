import { SymbolView } from 'expo-symbols';
import { Pressable, StyleSheet, Text, type StyleProp, type ViewStyle } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type BackButtonProps = {
  onPress: () => void;
  accessibilityLabel?: string;
  style?: StyleProp<ViewStyle>;
};

export function BackButton({ onPress, accessibilityLabel = 'Quay lại', style }: BackButtonProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel}
      hitSlop={Spacing.two}
      style={({ pressed }) => [
        styles.button,
        {
          backgroundColor: theme.backgroundElement,
          borderColor: theme.border,
          opacity: pressed ? 0.8 : 1,
        },
        style,
      ]}
    >
      <SymbolView
        name="chevron.left"
        size={18}
        weight="semibold"
        tintColor={theme.text}
        fallback={<Text style={[styles.fallback, { color: theme.text }]}>‹</Text>}
      />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    borderRadius: Radius.md,
    borderWidth: StyleSheet.hairlineWidth,
    height: 40,
    justifyContent: 'center',
    width: 40,
  },
  fallback: {
    fontSize: 20,
    fontWeight: '600',
    lineHeight: 24,
  },
});
