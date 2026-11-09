import type { ReactNode } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { FontSizes, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { SettingsItemTone } from '@/types/settings';

export type SettingsRowProps = {
  label: string;
  value?: string;
  onPress?: () => void;
  tone?: SettingsItemTone;
  showChevron?: boolean;
  showDivider?: boolean;
  right?: ReactNode;
};

export function SettingsRow({
  label,
  value,
  onPress,
  tone = 'default',
  showChevron = true,
  showDivider = false,
  right,
}: SettingsRowProps) {
  const theme = useTheme();
  const labelColor = tone === 'danger' ? theme.danger : theme.text;

  const content = (
    <>
      <Text numberOfLines={1} style={[styles.label, { color: labelColor }]}>
        {label}
      </Text>
      <View style={styles.trailing}>
        {value ? (
          <Text numberOfLines={1} style={[styles.value, { color: theme.textSecondary }]}>
            {value}
          </Text>
        ) : null}
        {right}
        {right ? null : showChevron ? (
          <Text style={[styles.chevron, { color: theme.icon }]}>›</Text>
        ) : null}
      </View>
    </>
  );

  return (
    <View
      style={[
        styles.wrapper,
        showDivider && {
          borderBottomWidth: StyleSheet.hairlineWidth,
          borderBottomColor: theme.border,
        },
      ]}>
      {onPress ? (
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={label}
          onPress={onPress}
          style={({ pressed }) => [styles.row, { opacity: pressed ? 0.6 : 1 }]}>
          {content}
        </Pressable>
      ) : (
        <View style={styles.row}>{content}</View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    paddingHorizontal: Spacing.three,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: Spacing.three,
    paddingVertical: Spacing.three,
    minHeight: 54,
  },
  label: {
    flexShrink: 1,
    fontSize: FontSizes.md,
    fontWeight: '600',
  },
  trailing: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.one,
  },
  value: {
    fontSize: FontSizes.sm,
  },
  chevron: {
    fontSize: FontSizes.xl,
    lineHeight: 22,
  },
});
