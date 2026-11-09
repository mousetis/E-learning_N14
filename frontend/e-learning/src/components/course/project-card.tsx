import { Pressable, StyleSheet, Text, View, type StyleProp, type ViewStyle } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { Project } from '@/types/course';

type Props = {
  project: Project;
  onPress?: () => void;
  style?: StyleProp<ViewStyle>;
};

export function ProjectCard({ project, onPress, style }: Props) {
  const theme = useAppTheme();

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`Dự án ${project.title} của ${project.author}`}
      onPress={onPress}
      style={[styles.card, style]}
    >
      <View style={[styles.thumb, { backgroundColor: (theme[project.tone as keyof typeof theme] as string) || theme.primarySoft }]} />
      <Text numberOfLines={1} style={[styles.title, { color: theme.text }]}>
        {project.title}
      </Text>
      <Text numberOfLines={1} style={[styles.author, { color: theme.textSecondary }]}>
        {project.author}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    gap: Spacing.one,
  },
  thumb: {
    height: 104,
    borderRadius: Radius.lg,
  },
  title: {
    fontSize: 14,
    fontWeight: '600',
    marginTop: Spacing.one,
  },
  author: {
    fontSize: 12,
  },
});
