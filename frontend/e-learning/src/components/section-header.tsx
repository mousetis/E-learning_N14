import { Pressable, StyleSheet, View } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Spacing } from '@/constants/theme';

interface Props {
  title: string;
  actionLabel?: string;
  onActionPress?: () => void;
}

export function SectionHeader({ title, actionLabel = 'Xem thêm', onActionPress }: Props) {
  const theme = useTheme();
  return (
    <View style={styles.row}>
      <ThemedText style={[styles.title, { color: theme.text }]} accessibilityRole="header">
        {title}
      </ThemedText>
      {onActionPress ? (
        <Pressable onPress={onActionPress} accessibilityRole="button" accessibilityLabel={actionLabel} hitSlop={8}>
          <ThemedText style={[styles.action, { color: theme.primary }]}>{actionLabel}</ThemedText>
        </Pressable>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    paddingHorizontal: Spacing.four,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: Spacing.four,
    marginBottom: Spacing.three,
  },
  title: { fontSize: FontSizes.lg, fontWeight: '700' },
  action: { fontSize: FontSizes.base, fontWeight: '600' },
});
