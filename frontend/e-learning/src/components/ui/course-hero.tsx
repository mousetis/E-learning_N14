import type { ReactNode } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { SegmentedTabs, type SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  category: string;
  headline: string[];
  title: string;
  tabs: SegmentItem[];
  activeTab: string;
  footer?: ReactNode;
  tone?: 'primary' | 'ink';
  compact?: boolean;
  coverImage?: string;
  onPlayPress?: () => void;
};

export function CourseHero({
  category,
  headline,
  title,
  tabs,
  activeTab,
  footer,
  tone = 'primary',
  compact = false,
  coverImage,
  onPlayPress,
}: Props) {
  const theme = useAppTheme();
  const coverColor = tone === 'ink' ? theme.ink : theme.primary;

  return (
    <View
      style={[styles.card, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}
    >
      <View
        style={[styles.cover, { backgroundColor: coverColor, height: compact ? 132 : 176 }]}
      >
        {coverImage ? (
          <>
            <Image
              source={{ uri: coverImage }}
              style={StyleSheet.absoluteFill}
              contentFit="cover"
              transition={300}
            />
            <View style={[StyleSheet.absoluteFill, styles.scrim]} />
          </>
        ) : null}

        <View style={[styles.chip, { backgroundColor: theme.backgroundElement }]}>
          <Text style={[styles.chipLabel, { color: theme.primary }]}>{category}</Text>
        </View>

        <View style={styles.headline}>
          {headline.map((line) => (
            <Text key={line} style={[styles.headlineText, { color: '#ffffff' }]}>
              {line}
            </Text>
          ))}
        </View>

        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Phát video giới thiệu khoá học"
          onPress={onPlayPress}
          style={[styles.play, { backgroundColor: theme.backgroundElement }]}
        >
          <Text style={[styles.playIcon, { color: theme.primary }]}>▶</Text>
        </Pressable>
      </View>

      <Text style={[styles.title, { color: theme.text }]}>{title}</Text>
      {footer ? <View style={styles.footer}>{footer}</View> : null}
      <SegmentedTabs activeKey={activeTab} items={tabs} />
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: Radius.xl,
    borderWidth: 1,
    padding: Spacing.three,
    gap: Spacing.three,
  },
  cover: {
    borderRadius: Radius.lg,
    padding: Spacing.three,
    justifyContent: 'space-between',
    overflow: 'hidden',
  },
  scrim: {
    backgroundColor: 'rgba(24, 21, 40, 0.45)',
  },
  chip: {
    alignSelf: 'flex-start',
    paddingHorizontal: Spacing.three,
    paddingVertical: 5,
    borderRadius: Radius.pill,
    zIndex: 2,
  },
  chipLabel: {
    fontSize: 12,
    fontWeight: '700',
  },
  headline: {
    gap: 2,
    zIndex: 2,
  },
  headlineText: {
    fontSize: 22,
    fontWeight: '800',
    lineHeight: 26,
    textShadowColor: 'rgba(0,0,0,0.3)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 3,
  },
  play: {
    position: 'absolute',
    right: Spacing.three,
    bottom: Spacing.three,
    width: 44,
    height: 44,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 3,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.15,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
  },
  playIcon: {
    fontSize: 14,
    marginLeft: 2,
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    lineHeight: 24,
  },
  footer: {
    gap: Spacing.one,
  },
});
