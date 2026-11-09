import { Pressable, StyleSheet, View } from 'react-native';
import { Image } from 'expo-image';
import { ThemedText } from '@/components/themed-text';
import { Icon } from '@/components/ui/icon';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { FeaturedCourse } from '@/types/course';

interface Props {
  course: FeaturedCourse;
  onPress?: () => void;
}

export function FeaturedCourseCard({ course, onPress }: Props) {
  const theme = useTheme();
  return (
    <Pressable
      onPress={onPress}
      accessibilityLabel={course.title}
      style={[styles.card, { backgroundColor: theme.surface }]}
    >
      <View style={[styles.thumbnail, { backgroundColor: course.coverColor }]}>
        {course.image ? (
          <>
            <Image
              source={{ uri: course.image }}
              style={styles.thumbnailImage}
              contentFit="cover"
              cachePolicy="memory-disk"
              priority="high"
            />
            <View style={styles.thumbnailOverlay} />
          </>
        ) : null}
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Lưu khóa học"
          hitSlop={8}
          style={[styles.bookmark, { backgroundColor: 'rgba(255,255,255,0.9)' }]}
        >
          <Icon sf="bookmark.fill" emoji="🔖" size={15} color={theme.primary} />
        </Pressable>
      </View>
      <View style={styles.body}>
        <ThemedText style={[styles.title, { color: theme.text }]} numberOfLines={2}>
          {course.title}
        </ThemedText>
        <ThemedText style={[styles.author, { color: theme.textSecondary }]} numberOfLines={1}>
          {course.author}
        </ThemedText>
        <ThemedText style={[styles.meta, { color: theme.primary }]}>{course.meta}</ThemedText>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: 240,
    borderRadius: Radius.lg,
    padding: Spacing.three,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  thumbnail: {
    width: '100%',
    height: 120,
    borderRadius: Radius.md,
    marginBottom: Spacing.three,
    overflow: 'hidden',
    position: 'relative',
  },
  thumbnailImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
  },
  thumbnailOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.08)',
  },
  body: { gap: 2 },
  title: { fontSize: FontSizes.md, fontWeight: '700', lineHeight: 20 },
  author: { fontSize: FontSizes.sm },
  meta: { fontSize: FontSizes.sm, fontWeight: '600', marginTop: Spacing.one },
  bookmark: {
    position: 'absolute',
    top: Spacing.two,
    right: Spacing.two,
    width: 32,
    height: 32,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
    zIndex: 2,
  },
});
