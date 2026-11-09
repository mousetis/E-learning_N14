import { Pressable, StyleSheet, View } from 'react-native';
import { Image } from 'expo-image';
import { ThemedText } from '@/components/themed-text';
import { ProgressBar } from '@/components/ui/progress-bar';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { MyCourse } from '@/types/course';

interface Props {
  course: MyCourse;
  onPress?: () => void;
}

export function MyCourseCard({ course, onPress }: Props) {
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
        <ThemedText style={[styles.duration, { color: theme.textSecondary }]}>
          {course.duration}
        </ThemedText>
        <ProgressBar progress={course.progress} />
        <ThemedText style={[styles.progressLabel, { color: theme.primary }]}>
          Hoàn thành {course.progress}%
        </ThemedText>
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
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
    alignItems: 'center',
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
  body: { flex: 1, gap: 4 },
  title: { fontSize: FontSizes.md, fontWeight: '700', lineHeight: 20 },
  duration: { fontSize: FontSizes.xs },
  progressLabel: { fontSize: FontSizes.xs, fontWeight: '700', marginTop: 2 },
});
