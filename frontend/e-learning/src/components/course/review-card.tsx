import { StyleSheet, Text, View } from 'react-native';

import { Avatar } from '@/components/ui/avatar';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { Review } from '@/types/course';

type Props = {
  review: Review;
};

export function ReviewCard({ review }: Props) {
  const theme = useAppTheme();

  return (
    <View
      style={[styles.card, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}
    >
      <View style={styles.head}>
        <Avatar name={review.author} size={40} tone={review.tone} />
        <View style={styles.headBody}>
          <Text style={[styles.name, { color: theme.text }]}>{review.author}</Text>
          <Text style={[styles.time, { color: theme.textSecondary }]}>{review.timeAgo}</Text>
        </View>
        <Text style={[styles.rating, { color: theme.star }]}>★ {review.rating.toFixed(1)}</Text>
      </View>
      <Text style={[styles.body, { color: theme.textSecondary }]}>{review.body}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    gap: Spacing.two,
    borderRadius: Radius.xl,
    borderWidth: 1,
    padding: Spacing.three,
  },
  head: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  headBody: {
    flex: 1,
    gap: Spacing.half,
  },
  name: {
    fontSize: 14,
    fontWeight: '600',
  },
  time: {
    fontSize: 12,
  },
  rating: {
    fontSize: 13,
    fontWeight: '600',
  },
  body: {
    fontSize: 13,
    lineHeight: 20,
  },
});
