import {
  Pressable,
  StyleSheet,
  Text,
  type StyleProp,
  type ViewStyle,
} from 'react-native';

import { FontSizes, Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type PrimaryButtonVariant = 'primary' | 'outline' | 'soft';

export type PrimaryButtonProps = {
  label: string;
  onPress?: () => void;
  variant?: PrimaryButtonVariant;
  disabled?: boolean;
  accessibilityLabel?: string;
  style?: StyleProp<ViewStyle>;
};

export function PrimaryButton({
  label,
  onPress,
  variant = 'primary',
  disabled = false,
  accessibilityLabel,
  style,
}: PrimaryButtonProps) {
  const theme = useTheme();
  const isPrimary = variant === 'primary';
  const isSoft = variant === 'soft';

  const bgColor = isPrimary
    ? theme.primary
    : isSoft
      ? theme.primarySoft
      : theme.backgroundElement;
  const borderColor = isPrimary ? theme.primary : isSoft ? 'transparent' : theme.primaryBorder;
  const textColor = isPrimary ? theme.onPrimary : theme.primary;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ disabled }}
      accessibilityLabel={accessibilityLabel ?? label}
      disabled={disabled}
      onPress={onPress}
      style={({ pressed }) => [
        styles.base,
        {
          backgroundColor: bgColor,
          borderColor,
          opacity: disabled ? 0.45 : pressed ? 0.85 : 1,
        },
        style,
      ]}>
      <Text style={[styles.label, { color: textColor }]}>
        {label}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    height: 54,
    borderRadius: Radius.lg,
    borderWidth: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: Spacing.three,
  },
  label: {
    fontSize: FontSizes.lg,
    fontWeight: '700',
  },
});
