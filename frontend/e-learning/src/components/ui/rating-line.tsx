import { StyleSheet, Text } from 'react-native';

import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  rating: number;
  reviewCount?: number;
  lessonCount?: number;
  fontSize?: number;
};

export function RatingLine({ rating, reviewCount, lessonCount, fontSize = 13 }: Props) {
  const theme = useAppTheme();

  const suffix = [
    reviewCount ? `(${reviewCount})` : null,
    lessonCount ? `${lessonCount} bài học` : null,
  ]
    .filter((part): part is string => part !== null)
    .join(' · ');

  return (
    <Text style={[styles.text, { color: theme.textSecondary, fontSize }]}>
      <Text style={{ color: theme.star }}>★ </Text>
      {rating.toFixed(1)}
      {suffix ? ` ${suffix}` : ''}
    </Text>
  );
}

const styles = StyleSheet.create({
  text: {
    fontWeight: '500',
  },
});
