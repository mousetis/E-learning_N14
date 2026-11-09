import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { Radius, Spacing, FontSizes } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { Course } from '@/types/course';

type CourseRowProps = {
  course: Course;
  onPress?: () => void;
};

export function CourseRow({ course, onPress }: CourseRowProps) {
  const theme = useTheme();

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={course.title}
      style={({ pressed }) => [
        styles.row,
        { backgroundColor: theme.card, opacity: pressed ? 0.9 : 1 },
      ]}>
      <View style={[styles.thumb, { backgroundColor: course.tint }]}>
        {course.image ? (
          <Image
            source={{ uri: course.image }}
            style={styles.thumbImage}
            contentFit="cover"
            cachePolicy="memory-disk"
            priority="high"
          />
        ) : null}
      </View>
      <View style={styles.content}>
        <Text numberOfLines={1} style={[styles.title, { color: theme.text }]}>
          {course.title}
        </Text>
        <Text numberOfLines={1} style={[styles.author, { color: theme.textSecondary }]}>
          {course.author}
        </Text>
        <Text style={[styles.meta, { color: theme.primary }]}>
          {`$${course.price} · ★ ${course.rating} · ${course.lessons} bài`}
        </Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.three,
    padding: Spacing.two + 4,
    borderRadius: Radius.lg,
    marginBottom: Spacing.two + 2,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 1,
  },
  thumb: {
    width: 68,
    height: 68,
    borderRadius: Radius.md,
    overflow: 'hidden',
    position: 'relative',
  },
  thumbImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
  },
  content: {
    flex: 1,
    gap: 3,
  },
  title: {
    fontSize: FontSizes.md,
    fontWeight: '700',
  },
  author: {
    fontSize: FontSizes.sm,
  },
  meta: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
    marginTop: 2,
  },
});
