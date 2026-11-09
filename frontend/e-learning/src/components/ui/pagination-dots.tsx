import { StyleSheet, View } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type PaginationDotsProps = {
  count?: number;
  activeIndex?: number;
};

export function PaginationDots({ count = 3, activeIndex = 0 }: PaginationDotsProps) {
  const theme = useTheme();

  return (
    <View
      style={styles.row}
      accessibilityRole="adjustable"
      accessibilityLabel={`Bước ${activeIndex + 1} trên ${count}`}
    >
      {Array.from({ length: count }, (_, index) => (
        <View
          key={`dot-${index}`}
          style={[
            styles.dot,
            { backgroundColor: index === activeIndex ? theme.primary : theme.dotInactive },
          ]}
        />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  dot: {
    borderRadius: 4,
    height: 8,
    width: 8,
  },
});
