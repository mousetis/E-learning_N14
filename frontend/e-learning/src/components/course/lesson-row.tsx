import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import { useAppStore } from '@/store';
import type { Lesson } from '@/types/course';

type Props = {
  lesson: Lesson;
  onPress?: () => void;
};

export function LessonRow({ lesson, onPress }: Props) {
  const theme = useAppTheme();
  const { user } = useAppStore();
  const isLocked = lesson.status === 'locked';
  const isActive = lesson.status === 'in-progress';

  // Xác định nhãn trạng thái bài học
  let statusText = '';
  if (isLocked) {
    statusText = '🔒 Đã khóa';
  } else if (lesson.isPreview) {
    // Khóa học chưa mua: bài học xem thử hiển thị "Học thử miễn phí"
    statusText = 'Học thử miễn phí';
  } else if (lesson.status === 'completed') {
    statusText = 'Hoàn thành';
  } else if (lesson.status === 'in-progress') {
    statusText = 'Đang học';
  }

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`Bài ${lesson.index}: ${lesson.title}`}
      onPress={onPress}
      style={({ pressed }) => [
        styles.row,
        {
          backgroundColor: theme.backgroundElement,
          borderColor: isActive ? theme.primary : theme.border,
          opacity: pressed ? 0.7 : 1,
        },
      ]}
    >
      <View
        style={[
          styles.badge,
          { backgroundColor: isLocked ? theme.chipBackground : theme.primarySoft },
        ]}
      >
        <Text style={[styles.badgeText, { color: isLocked ? theme.muted : theme.primary }]}>
          {lesson.index}
        </Text>
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
      {statusText ? (
        <Text
          style={[
            styles.status,
            {
              color: isLocked ? theme.muted : theme.primary,
              fontWeight: isLocked ? '500' : '700',
            },
          ]}
        >
          {statusText}
        </Text>
      ) : null}
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
