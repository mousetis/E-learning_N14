import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';
import { ThemedText } from '@/components/themed-text';
import { useTheme } from '@/hooks/use-theme';
import { FontSizes, Radius, Spacing } from '@/constants/theme';
import type { Instructor } from '@/types/instructor';

interface Props {
  instructor: Instructor;
  onPress?: () => void;
}

function getInitials(name: string): string {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('');
}

export function InstructorCard({ instructor, onPress }: Props) {
  const theme = useTheme();
  return (
    <Pressable
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={instructor.name}
      style={[styles.card, { backgroundColor: theme.surface }]}
    >
      <View style={[styles.avatar, { backgroundColor: instructor.avatarColor }]}>
        {instructor.avatarUri ? (
          <Image
            source={{ uri: instructor.avatarUri }}
            style={styles.avatarImage}
            contentFit="cover"
            cachePolicy="memory-disk"
            priority="high"
          />
        ) : (
          <Text style={[styles.initials, { color: theme.text }]}>
            {getInitials(instructor.name)}
          </Text>
        )}
      </View>
      <ThemedText style={[styles.name, { color: theme.text }]} numberOfLines={1}>
        {instructor.name}
      </ThemedText>
      {instructor.role ? (
        <ThemedText style={[styles.role, { color: theme.primary }]} numberOfLines={1}>
          {instructor.role}
        </ThemedText>
      ) : null}
      <ThemedText style={[styles.rating, { color: theme.textSecondary }]}>
        {instructor.rating}
      </ThemedText>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    width: 144,
    padding: Spacing.three,
    borderRadius: Radius.lg,
    alignItems: 'center',
    gap: 4,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 2,
  },
  avatar: {
    width: 76,
    height: 76,
    borderRadius: 38,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
    position: 'relative',
    marginBottom: Spacing.half,
  },
  avatarImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
    borderRadius: 38,
  },
  initials: {
    fontSize: FontSizes.lg,
    fontWeight: '800',
  },
  name: { fontSize: FontSizes.sm + 1, fontWeight: '700', textAlign: 'center' },
  role: { fontSize: FontSizes.xs, fontWeight: '600', textAlign: 'center' },
  rating: { fontSize: FontSizes.xs, marginTop: 2 },
});
