import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  onPress?: () => void;
};

export function UploadCard({ onPress }: Props) {
  const theme = useAppTheme();

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel="Tải dự án lên tại đây"
      onPress={onPress}
      style={[styles.card, { backgroundColor: theme.primarySoft, borderColor: theme.primaryMuted }]}
    >
      <View style={[styles.icon, { backgroundColor: theme.primary }]}>
        <Text style={[styles.iconText, { color: theme.onPrimary }]}>↑</Text>
      </View>
      <Text style={[styles.label, { color: theme.primary }]}>Tải dự án lên tại đây</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    alignItems: 'center',
    justifyContent: 'center',
    gap: Spacing.two,
    paddingVertical: Spacing.four,
    borderRadius: Radius.xl,
    borderWidth: 1,
    borderStyle: 'dashed',
  },
  icon: {
    width: 40,
    height: 40,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconText: {
    fontSize: 18,
    fontWeight: '700',
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
  },
});
