import { StyleSheet, View } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type PriceRangeProps = {
  minimum: number;
  maximum: number;
  low: number;
  high: number;
};

export function PriceRange({ minimum, maximum, low, high }: PriceRangeProps) {
  const theme = useTheme();
  const span = maximum - minimum || 1;
  const lowPercent = Math.min(100, Math.max(0, ((low - minimum) / span) * 100));
  const highPercent = Math.min(100, Math.max(0, ((high - minimum) / span) * 100));

  return (
    <View
      accessibilityRole="adjustable"
      accessibilityLabel={`Mức giá từ ${low} đến ${high} đô la`}
      style={styles.wrapper}>
      <View style={[styles.track, { backgroundColor: theme.backgroundElement }]}>
        <View
          style={[
            styles.fill,
            {
              backgroundColor: theme.primary,
              left: `${lowPercent}%`,
              right: `${100 - highPercent}%`,
            },
          ]}
        />
        <View
          style={[
            styles.thumb,
            {
              backgroundColor: theme.card,
              borderColor: theme.primary,
              left: `${lowPercent}%`,
            },
          ]}
        />
        <View
          style={[
            styles.thumb,
            {
              backgroundColor: theme.card,
              borderColor: theme.primary,
              left: `${highPercent}%`,
            },
          ]}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    paddingHorizontal: Spacing.two,
    paddingVertical: Spacing.three,
  },
  track: {
    height: 6,
    borderRadius: 999,
    justifyContent: 'center',
  },
  fill: {
    position: 'absolute',
    height: 6,
    borderRadius: 999,
  },
  thumb: {
    position: 'absolute',
    top: -7,
    width: 20,
    height: 20,
    marginLeft: -10,
    borderRadius: 10,
    borderWidth: 2,
  },
});
