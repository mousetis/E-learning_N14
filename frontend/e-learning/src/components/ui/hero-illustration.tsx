import { StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

/**
 * Minh hoạ hero của frame "Chào mừng".
 * Khi có asset export từ Figma, thay bằng <Image source={require('@/../assets/images/welcome-hero.png')} />
 */
export function HeroIllustration() {
  const theme = useTheme();

  return (
    <View
      accessibilityElementsHidden
      importantForAccessibility="no-hide-descendants"
      style={[styles.container, { backgroundColor: theme.primarySoft }]}
    >
      <View style={[styles.blobLarge, { backgroundColor: theme.primaryLight }]} />
      <View style={[styles.blobAccent, { backgroundColor: theme.accent }]} />
      <View style={[styles.dot, { backgroundColor: theme.primary }]} />

      <View
        style={[
          styles.card,
          {
            backgroundColor: theme.backgroundElement,
            borderColor: theme.border,
            shadowColor: theme.shadow,
          },
        ]}
      >
        <View style={[styles.thumbnail, { backgroundColor: theme.primary }]}>
          <View style={styles.playTriangle} />
        </View>
        <View style={styles.cardBody}>
          <View style={[styles.line, { backgroundColor: theme.borderStrong, width: '86%' }]} />
          <View style={[styles.line, { backgroundColor: theme.border, width: '62%' }]} />
          <View style={[styles.line, { backgroundColor: theme.border, width: '40%' }]} />
        </View>
      </View>

      <View style={[styles.badge, { backgroundColor: theme.accent, shadowColor: theme.shadow }]}>
        <Text style={[styles.badgeValue, { color: theme.text }]}>100+</Text>
        <Text style={[styles.badgeLabel, { color: theme.text }]}>khóa học</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    borderRadius: Radius.xl + 8,
    height: 300,
    justifyContent: 'center',
    marginHorizontal: Spacing.four,
    overflow: 'hidden',
  },
  blobLarge: {
    borderRadius: 120,
    height: 240,
    opacity: 0.9,
    position: 'absolute',
    right: -70,
    top: -70,
    width: 240,
  },
  blobAccent: {
    borderRadius: 70,
    bottom: -40,
    height: 140,
    left: -40,
    opacity: 0.85,
    position: 'absolute',
    width: 140,
  },
  dot: {
    borderRadius: 9,
    height: 18,
    left: 36,
    position: 'absolute',
    top: 44,
    width: 18,
  },
  card: {
    alignItems: 'center',
    borderRadius: Radius.xl,
    borderWidth: 1,
    elevation: 5,
    flexDirection: 'row',
    gap: Spacing.three - Spacing.one,
    maxWidth: 280,
    padding: Spacing.three,
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.14,
    shadowRadius: 20,
    width: '72%',
  },
  thumbnail: {
    alignItems: 'center',
    borderRadius: Radius.md + 2,
    height: 56,
    justifyContent: 'center',
    width: 56,
  },
  playTriangle: {
    borderBottomColor: 'transparent',
    borderBottomWidth: 8,
    borderLeftColor: '#ffffff',
    borderLeftWidth: 13,
    borderTopColor: 'transparent',
    borderTopWidth: 8,
    height: 0,
    marginLeft: 4,
    width: 0,
  },
  cardBody: {
    flex: 1,
    gap: Spacing.two,
  },
  line: {
    borderRadius: 4,
    height: 8,
  },
  badge: {
    alignItems: 'center',
    borderRadius: Radius.lg,
    bottom: Spacing.five,
    elevation: 6,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two + Spacing.half,
    position: 'absolute',
    right: Spacing.four + Spacing.two,
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.18,
    shadowRadius: 14,
  },
  badgeValue: {
    fontSize: 16,
    fontWeight: '700',
  },
  badgeLabel: {
    fontSize: 11,
    fontWeight: '500',
  },
});
