import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { Radius, Spacing, FontSizes } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import type { Course } from '@/types/course';

type CourseCardProps = {
  course: Course;
  onPress?: () => void;
};

function formatVnd(amount?: number | null): string {
  if (amount == null || amount === 0) return 'Miễn phí';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

export function CourseCard({ course, onPress }: CourseCardProps) {
  const theme = useTheme();
  const imageUri = course.image || (course as any).coverUri;

  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={course.title}
      style={({ pressed }) => [
        styles.card,
        { backgroundColor: theme.card, opacity: pressed ? 0.9 : 1 },
      ]}>
      <View style={[styles.thumb, { backgroundColor: course.tint }]}>
        {imageUri ? (
          <>
            <Image
              source={{ uri: imageUri }}
              style={styles.thumbImage}
              contentFit="cover"
              cachePolicy="memory-disk"
              priority="high"
            />
            <View style={styles.thumbOverlay} />
          </>
        ) : null}
        {course.badge ? (
          <View style={[styles.badge, { backgroundColor: course.badge.tint }]}>
            <Text style={[styles.badgeLabel, { color: theme.text }]}>{course.badge.label}</Text>
          </View>
        ) : null}
      </View>
      <Text numberOfLines={2} style={[styles.title, { color: theme.text }]}>
        {course.title}
      </Text>
      <Text numberOfLines={1} style={[styles.author, { color: theme.textSecondary }]}>
        {course.author}
      </Text>
      <Text style={[styles.meta, { color: theme.primary }]}>
        {`${formatVnd(course.price)} · ★ ${course.rating} · ${course.lessons} bài`}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: 210,
    borderRadius: Radius.xl,
    padding: Spacing.two + 2,
    gap: Spacing.one,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  thumb: {
    width: '100%',
    height: 115,
    borderRadius: Radius.lg,
    marginBottom: Spacing.two,
    alignItems: 'flex-start',
    justifyContent: 'flex-start',
    padding: Spacing.two,
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
  thumbOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.08)',
  },
  badge: {
    borderRadius: Radius.pill,
    paddingHorizontal: Spacing.two + 2,
    paddingVertical: Spacing.half + 1,
    zIndex: 2,
  },
  badgeLabel: {
    fontSize: FontSizes.xs,
    fontWeight: '700',
  },
  title: {
    fontSize: FontSizes.md,
    fontWeight: '700',
    lineHeight: 20,
  },
  author: {
    fontSize: FontSizes.sm,
  },
  meta: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
    marginTop: Spacing.one,
  },
});
