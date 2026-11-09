import type { ReactNode } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';

type ButtonVariant = 'primary' | 'secondary' | 'accent' | 'danger' | 'ghost' | 'outline';
type ButtonSize = 'sm' | 'md' | 'lg';

interface ButtonProps {
  label: string;
  onPress?: () => void;
  variant?: ButtonVariant;
  size?: ButtonSize;
  disabled?: boolean;
  loading?: boolean;
  fullWidth?: boolean;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
  accessibilityLabel?: string;
}

const HEIGHTS: Record<ButtonSize, number> = { sm: 36, md: 48, lg: 56 };
const PADDINGS: Record<ButtonSize, number> = { sm: 12, md: 20, lg: 24 };
const FONT_SIZES: Record<ButtonSize, number> = { sm: FontSizes.sm, md: FontSizes.md, lg: FontSizes.lg };

export function Button({
  label,
  onPress,
  variant = 'primary',
  size = 'md',
  disabled,
  loading,
  fullWidth,
  leftIcon,
  rightIcon,
  accessibilityLabel,
}: ButtonProps) {
  const theme = useTheme();
  const blocked = !!(disabled || loading);

  let backgroundColor: string = 'transparent';
  let textColor: string = theme.primary;
  let borderColor: string = 'transparent';
  let borderWidth = 0;

  switch (variant) {
    case 'primary':
      backgroundColor = blocked ? theme.border : theme.primary;
      textColor = '#FFFFFF';
      break;
    case 'secondary':
      backgroundColor = blocked ? theme.border : theme.backgroundSelected;
      textColor = theme.primary;
      break;
    case 'accent':
      backgroundColor = blocked ? theme.border : theme.accent;
      textColor = theme.text;
      break;
    case 'danger':
      backgroundColor = blocked ? theme.border : theme.danger;
      textColor = '#FFFFFF';
      break;
    case 'outline':
      backgroundColor = 'transparent';
      borderColor = theme.border;
      borderWidth = 1;
      textColor = theme.text;
      break;
    case 'ghost':
      backgroundColor = 'transparent';
      textColor = theme.primary;
      break;
  }

  return (
    <Pressable
      onPress={onPress}
      disabled={blocked}
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel ?? label}
      accessibilityState={{ disabled: blocked }}
      style={({ pressed }) => [
        styles.base,
        {
          backgroundColor,
          borderColor,
          borderWidth,
          height: HEIGHTS[size],
          paddingHorizontal: PADDINGS[size],
          borderRadius: Radius.md,
          opacity: pressed ? 0.85 : 1,
          alignSelf: fullWidth ? 'stretch' : 'center',
        },
      ]}
    >
      {loading ? (
        <ActivityIndicator color={textColor} />
      ) : (
        <View style={styles.row}>
          {leftIcon ? <View style={styles.iconLeft}>{leftIcon}</View> : null}
          <Text style={{ color: textColor, fontSize: FONT_SIZES[size], fontWeight: '600' }}>{label}</Text>
          {rightIcon ? <View style={styles.iconRight}>{rightIcon}</View> : null}
        </View>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconLeft: { marginRight: Spacing.two },
  iconRight: { marginLeft: Spacing.two },
});
