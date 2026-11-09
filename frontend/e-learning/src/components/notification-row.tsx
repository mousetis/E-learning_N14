import { Pressable, StyleSheet, View } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { Icon } from '@/components/ui/icon';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { NotificationItem } from '@/types/notification';

interface Props {
  item: NotificationItem;
  onPress?: () => void;
}

export function NotificationRow({ item, onPress }: Props) {
  const theme = useTheme();
  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={item.title}
      style={[styles.row, { backgroundColor: theme.surface }]}
    >
      <View style={[styles.iconWrap, { backgroundColor: item.iconColor }]}>
        <Icon sf="bell.fill" emoji="🔔" size={18} color="#19183f" />
      </View>
      <View style={styles.body}>
        <ThemedText style={[styles.title, { color: theme.text }]}>{item.title}</ThemedText>
        <ThemedText style={[styles.desc, { color: theme.textSecondary }]}>{item.body}</ThemedText>
        <ThemedText style={[styles.time, { color: theme.textSecondary }]}>{item.time}</ThemedText>
      </View>
      {item.unread ? <View style={[styles.dot, { backgroundColor: theme.primary }]} /> : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: Spacing.three,
    padding: Spacing.three,
    borderRadius: Radius.lg,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 1,
  },
  iconWrap: { width: 44, height: 44, borderRadius: Radius.pill, alignItems: 'center', justifyContent: 'center' },
  body: { flex: 1, gap: 2 },
  title: { fontSize: FontSizes.md, fontWeight: '700' },
  desc: { fontSize: FontSizes.base },
  time: { fontSize: FontSizes.sm, marginTop: 2, opacity: 0.85 },
  dot: { width: 8, height: 8, borderRadius: 4, marginTop: 6 },
});
