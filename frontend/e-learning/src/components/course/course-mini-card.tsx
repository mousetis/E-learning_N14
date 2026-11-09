import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { RelatedCourse } from '@/types/course';

type Props = {
  course: RelatedCourse;
  onPress?: () => void;
};

export function CourseMiniCard({ course, onPress }: Props) {
  const theme = useAppTheme();

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`Mở khoá học ${course.title}`}
      onPress={onPress}
      style={[styles.card, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}
    >
      <View style={[styles.thumb, { backgroundColor: (theme[course.tone as keyof typeof theme] as string) || theme.primarySoft }]}>
        {course.image ? (
          <Image
            source={{ uri: course.image }}
            style={StyleSheet.absoluteFill}
            contentFit="cover"
            transition={200}
          />
        ) : null}
        {course.badge ? (
          <View style={[styles.badge, { backgroundColor: theme.gold }]}>
            <Text style={[styles.badgeText, { color: theme.onPastel }]}>{course.badge}</Text>
          </View>
        ) : null}
      </View>
      <View style={styles.body}>
        <Text numberOfLines={1} style={[styles.title, { color: theme.text }]}>
          {course.title}
        </Text>
        <Text numberOfLines={1} style={[styles.author, { color: theme.textSecondary }]}>
          {course.author}
        </Text>
        <Text numberOfLines={1} style={[styles.meta, { color: theme.primary }]}>
          {course.meta}
        </Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    borderRadius: Radius.lg,
    borderWidth: 1,
    padding: Spacing.two,
  },
  thumb: {
    width: 72,
    height: 72,
    borderRadius: Radius.md,
    alignItems: 'flex-start',
    justifyContent: 'flex-start',
    padding: Spacing.one,
    overflow: 'hidden',
  },
  badge: {
    paddingHorizontal: Spacing.one + 2,
    paddingVertical: Spacing.half,
    borderRadius: Radius.pill,
    zIndex: 2,
  },
  badgeText: {
    fontSize: 9,
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
  author: {
    fontSize: 12,
  },
  meta: {
    fontSize: 12,
    fontWeight: '600',
  },
});
