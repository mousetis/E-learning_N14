import { router } from 'expo-router';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { FontSizes, Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type ScreenHeaderProps = {
  title: string;
  onBack?: () => void;
};

export function ScreenHeader({ title, onBack }: ScreenHeaderProps) {
  const theme = useTheme();

  const handleBack = () => {
    if (onBack) {
      onBack();
      return;
    }
    if (router.canGoBack()) {
      router.back();
      return;
    }
    router.replace('/');
  };

  return (
    <View style={styles.container}>
      <Pressable
        accessibilityRole="button"
        accessibilityLabel="Quay lại"
        onPress={handleBack}
        hitSlop={Spacing.two}
        style={({ pressed }) => [
          styles.backButton,
          {
            backgroundColor: theme.backgroundElement,
            borderColor: theme.border,
            opacity: pressed ? 0.7 : 1,
            shadowColor: theme.shadow,
          },
        ]}>
        <Text style={[styles.backGlyph, { color: theme.text }]}>‹</Text>
      </Pressable>
      <Text numberOfLines={1} style={[styles.title, { color: theme.text }]}>
        {title}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    paddingVertical: Spacing.two,
  },
  backButton: {
    width: 44,
    height: 44,
    borderRadius: Radius.pill,
    borderWidth: StyleSheet.hairlineWidth,
    alignItems: 'center',
    justifyContent: 'center',
    shadowOpacity: 0.06,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
    elevation: 1,
  },
  backGlyph: {
    fontSize: 26,
    lineHeight: 28,
    marginTop: -2,
    fontWeight: '600',
  },
  title: {
    flex: 1,
    fontSize: FontSizes.xxl,
    fontWeight: '700',
  },
});
