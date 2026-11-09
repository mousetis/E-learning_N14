import { Pressable, StyleSheet, View } from 'react-native';
import { Image } from 'expo-image';
import { ThemedText } from '@/components/themed-text';
import { Icon } from '@/components/ui/icon';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { SavedCourse } from '@/types/course';

interface Props {
  course: SavedCourse;
  onPress?: () => void;
}

export function SavedCourseCard({ course, onPress }: Props) {
  const theme = useTheme();
  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={course.title}
      style={[styles.card, { backgroundColor: theme.surface }]}
    >
      <View style={[styles.thumbnail, { backgroundColor: course.coverColor }]}>
        {course.image ? (
          <Image
            source={{ uri: course.image }}
            style={styles.thumbnailImage}
            contentFit="cover"
            cachePolicy="memory-disk"
            priority="high"
          />
        ) : null}
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
      <View style={[styles.bookmark, { backgroundColor: theme.primarySoft }]}>
        <Icon sf="bookmark.fill" emoji="🔖" size={16} color={theme.primary} />
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    gap: Spacing.three,
    padding: Spacing.three,
    borderRadius: Radius.lg,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  thumbnail: {
    width: 80,
    height: 80,
    borderRadius: Radius.md,
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
  body: { flex: 1, gap: 2 },
  title: { fontSize: FontSizes.md, fontWeight: '700', lineHeight: 20 },
  author: { fontSize: FontSizes.sm },
  meta: { fontSize: FontSizes.sm, fontWeight: '600', marginTop: Spacing.half },
  bookmark: {
    width: 32,
    height: 32,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
