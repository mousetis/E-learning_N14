import { Pressable, StyleSheet, View } from 'react-native';
import { Image } from 'expo-image';
import { ThemedText } from '@/components/themed-text';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { PopularCourse } from '@/types/course';

interface Props {
  course: PopularCourse;
  onPress?: () => void;
}

export function PopularCourseCard({ course, onPress }: Props) {
  const theme = useTheme();
  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={course.title}
      style={[styles.card, { backgroundColor: theme.surface }]}
    >
      <View style={[styles.cover, { backgroundColor: course.coverColor }]}>
        {course.image ? (
          <>
            <Image
              source={{ uri: course.image }}
              style={styles.coverImage}
              contentFit="cover"
              cachePolicy="memory-disk"
              priority="high"
            />
            <View style={styles.coverOverlay} />
          </>
        ) : null}
        {course.badge ? (
          <View style={[styles.badge, { backgroundColor: theme.accentYellow }]}>
            <ThemedText style={[styles.badgeText, { color: '#1A1A2E' }]}>{course.badge}</ThemedText>
          </View>
        ) : null}
      </View>
      <View style={styles.body}>
        <ThemedText style={[styles.title, { color: theme.text }]} numberOfLines={2}>
          {course.title}
        </ThemedText>
        <ThemedText style={[styles.author, { color: theme.textSecondary }]}>
          {course.author}
        </ThemedText>
        <ThemedText style={[styles.price, { color: theme.primary }]}>{course.price}</ThemedText>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: 200,
    borderRadius: Radius.lg,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  cover: {
    width: '100%',
    height: 115,
    position: 'relative',
    overflow: 'hidden',
    padding: Spacing.two,
  },
  coverImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
  },
  coverOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
  },
  badge: {
    alignSelf: 'flex-start',
    paddingHorizontal: Spacing.two,
    paddingVertical: 3,
    borderRadius: Radius.pill,
    elevation: 1,
    zIndex: 2,
  },
  badgeText: { fontSize: FontSizes.xs, fontWeight: '700' },
  body: { padding: Spacing.three, gap: 3 },
  title: { fontSize: FontSizes.md, fontWeight: '700', lineHeight: 20 },
  author: { fontSize: FontSizes.sm },
  price: { fontSize: FontSizes.md, fontWeight: '800', marginTop: Spacing.half },
});
