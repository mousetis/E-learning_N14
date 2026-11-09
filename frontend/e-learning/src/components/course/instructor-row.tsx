import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Avatar } from '@/components/ui/avatar';
import { PrimaryButton } from '@/components/ui/primary-button';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { InstructorSummary } from '@/types/course';

type Props = {
  instructor: InstructorSummary;
  following?: boolean;
  onPress?: () => void;
  onFollow?: () => void;
};

export function InstructorRow({ instructor, following = false, onPress, onFollow }: Props) {
  const theme = useAppTheme();

  return (
    <View
      style={[styles.card, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}
    >
      <Pressable
        accessibilityRole="button"
        accessibilityLabel={`Xem hồ sơ giảng viên ${instructor.name}`}
        onPress={onPress}
        style={styles.profile}
      >
        <Avatar name={instructor.name} size={48} tone={instructor.tone} uri={instructor.avatarUri} />
        <View style={styles.body}>
          <Text style={[styles.name, { color: theme.text }]}>{instructor.name}</Text>
          <Text style={[styles.role, { color: theme.textSecondary }]}>{instructor.role}</Text>
        </View>
      </Pressable>
      <PrimaryButton
        label={following ? 'Đang theo dõi' : 'Theo dõi'}
        onPress={onFollow}
        variant={following ? 'soft' : 'outline'}
        style={styles.follow}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    borderRadius: Radius.xl,
    borderWidth: 1,
    padding: Spacing.three,
  },
  profile: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  body: {
    flex: 1,
    gap: Spacing.half,
  },
  name: {
    fontSize: 15,
    fontWeight: '600',
  },
  role: {
    fontSize: 12,
  },
  follow: {
    paddingHorizontal: Spacing.three,
    minHeight: 38,
  },
});
