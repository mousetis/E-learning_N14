import { StyleSheet, Text, View } from 'react-native';

import { LessonRow } from '@/components/course/lesson-row';
import { Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { Chapter, Lesson } from '@/types/course';

type Props = {
  chapter: Chapter;
  onLessonPress?: (lesson: Lesson) => void;
};

export function ChapterSection({ chapter, onLessonPress }: Props) {
  const theme = useAppTheme();

  return (
    <View style={styles.section}>
      <Text style={[styles.title, { color: theme.text }]}>{chapter.title}</Text>
      <View style={styles.lessons}>
        {chapter.lessons.map((lesson) => (
          <LessonRow key={lesson.id} lesson={lesson} onPress={() => onLessonPress?.(lesson)} />
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  section: {
    gap: Spacing.two,
  },
  title: {
    fontSize: 16,
    fontWeight: '700',
  },
  lessons: {
    gap: Spacing.two,
  },
});
