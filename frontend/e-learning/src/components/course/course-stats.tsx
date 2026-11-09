import { StyleSheet, Text, View } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  likes: number;
  shares: number;
};

export function CourseStats({ likes, shares }: Props) {
  const theme = useAppTheme();

  return (
    <View style={styles.row}>
      <Text style={[styles.item, { color: theme.textSecondary }]}>{`♡ ${likes} Thích`}</Text>
      <Text style={[styles.item, { color: theme.textSecondary }]}>{`↗ ${shares} Chia sẻ`}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.four,
  },
  item: {
    fontSize: 13,
    fontWeight: '500',
  },
});
