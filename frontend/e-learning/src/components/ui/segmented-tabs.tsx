import { useRouter, type Href } from 'expo-router';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

export type SegmentItem = {
  key: string;
  label: string;
  href?: Href;
};

type Props = {
  items: SegmentItem[];
  activeKey: string;
  onSelect?: (key: string) => void;
};

export function SegmentedTabs({ items, activeKey, onSelect }: Props) {
  const theme = useAppTheme();
  const router = useRouter();

  return (
    <View style={[styles.row, { borderBottomColor: theme.border }]}>
      {items.map((item) => {
        const active = item.key === activeKey;

        return (
          <Pressable
            key={item.key}
            accessibilityRole="tab"
            accessibilityLabel={item.label}
            accessibilityState={{ selected: active }}
            onPress={() => {
              if (item.href) {
                router.push(item.href);
                return;
              }
              onSelect?.(item.key);
            }}
            style={styles.tab}
          >
            <Text style={[styles.label, { color: active ? theme.primary : theme.muted }]}>
              {item.label}
            </Text>
            <View
              style={[
                styles.underline,
                { backgroundColor: active ? theme.primary : 'transparent' },
              ]}
            />
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: Spacing.four,
    borderBottomWidth: 1,
  },
  tab: {
    paddingBottom: Spacing.two,
    alignItems: 'center',
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
  },
  underline: {
    height: 2,
    width: '100%',
    borderRadius: 2,
    marginTop: Spacing.two,
  },
});
