import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { Lesson, LessonStatus } from '@/types/course';

const STATUS_LABEL: Record<LessonStatus, string> = {
  completed: 'Hoàn thành',
  'in-progress': 'Đang học',
  locked: 'Đã khóa',
  available: 'Học ngay',
};

type Props = {
  lesson: Lesson;
  onPress?: () => void;
};

export function LessonRow({ lesson, onPress }: Props) {
  const theme = useAppTheme();
  const isLocked = lesson.status === 'locked';
  const isActive = lesson.status === 'in-progress';

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`Bài ${lesson.index}: ${lesson.title}`}
      accessibilityState={{ disabled: isLocked }}
      disabled={isLocked}
      onPress={onPress}
      style={[
        styles.row,
        {
          backgroundColor: theme.backgroundElement,
          borderColor: isActive ? theme.primary : theme.border,
        },
      ]}
    >
      <View style={[styles.badge, { backgroundColor: theme.primarySoft }]}>
        <Text style={[styles.badgeText, { color: theme.primary }]}>{lesson.index}</Text>
      </View>
      <View style={styles.body}>
        <Text
          numberOfLines={1}
          style={[styles.title, { color: isLocked ? theme.muted : theme.text }]}
        >
          {lesson.title}
        </Text>
        <Text style={[styles.duration, { color: theme.textSecondary }]}>{lesson.duration}</Text>
      </View>
      <Text style={[styles.status, { color: isLocked ? theme.muted : theme.primary }]}>
        {STATUS_LABEL[lesson.status]}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    padding: Spacing.two + 2,
    borderRadius: Radius.lg,
    borderWidth: 1,
  },
  badge: {
    width: 36,
    height: 36,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  badgeText: {
    fontSize: 13,
    fontWeight: '700',
  },
  body: {
    flex: 1,
    gap: Spacing.half,
  },
  title: {
    fontSize: 14,
    fontWeight: '600',
  },
  duration: {
    fontSize: 12,
  },
  status: {
    fontSize: 12,
    fontWeight: '600',
  },
});
