import { SymbolView } from 'expo-symbols';
import type { ReactNode } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type CheckboxProps = {
  checked: boolean;
  onCheckedChange: (checked: boolean) => void;
  accessibilityLabel: string;
  children: ReactNode;
};

export function Checkbox({ checked, onCheckedChange, accessibilityLabel, children }: CheckboxProps) {
  const theme = useTheme();

  return (
    <View style={styles.row}>
      <Pressable
        onPress={() => onCheckedChange(!checked)}
        accessibilityRole="checkbox"
        accessibilityState={{ checked }}
        accessibilityLabel={accessibilityLabel}
        hitSlop={Spacing.two}
        style={[
          styles.box,
          {
            backgroundColor: checked ? theme.primary : theme.backgroundElement,
            borderColor: checked ? theme.primary : theme.borderStrong,
          },
        ]}
      >
        {checked ? (
          <SymbolView
            name="checkmark"
            size={13}
            weight="bold"
            tintColor={theme.onPrimary}
            fallback={<Text style={[styles.fallback, { color: theme.onPrimary }]}>✓</Text>}
          />
        ) : null}
      </Pressable>
      <Pressable
        onPress={() => onCheckedChange(!checked)}
        accessibilityRole="checkbox"
        accessibilityState={{ checked }}
        accessibilityLabel={accessibilityLabel}
        style={styles.labelArea}
      >
        {children}
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    alignItems: 'flex-start',
    flexDirection: 'row',
    gap: Spacing.two + Spacing.half,
  },
  box: {
    alignItems: 'center',
    borderRadius: Radius.sm - 2,
    borderWidth: 1.5,
    height: 22,
    justifyContent: 'center',
    marginTop: Spacing.half,
    width: 22,
  },
  labelArea: {
    flex: 1,
  },
  fallback: {
    fontSize: 12,
    fontWeight: '700',
  },
});
