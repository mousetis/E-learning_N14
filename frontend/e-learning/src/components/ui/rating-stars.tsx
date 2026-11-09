import { StyleSheet, Text, View } from 'react-native';
import { useTheme } from '@/hooks/use-theme';

interface RatingStarsProps {
  rating: number;
  count?: number;
  size?: number;
  showValue?: boolean;
}

export function RatingStars({ rating, count, size = 13, showValue = true }: RatingStarsProps) {
  const theme = useTheme();

  return (
    <View style={styles.row}>
      <Text style={{ color: theme.accentDark, fontSize: size }}>★</Text>
      {showValue ? (
        <Text style={[styles.value, { color: theme.textSecondary, fontSize: size }]}>
          {rating.toFixed(1)}
          {count != null ? ` (${count})` : ''}
        </Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
  },
  value: {
    marginLeft: 2,
  },
});
