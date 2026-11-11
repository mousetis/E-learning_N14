import { router } from '@/navigation/routerHook';
import { ScrollView, StyleSheet, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { AppButton } from '@/components/ui/app-button';
import { FooterLink } from '@/components/ui/footer-link';
import { HeroIllustration } from '@/components/ui/hero-illustration';
import { PaginationDots } from '@/components/ui/pagination-dots';
import { MaxContentWidth, Spacing } from '@/constants/theme';
import { welcomeCopy } from '@/constants/auth';
import { useTheme } from '@/hooks/use-theme';

export default function WelcomeScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  return (
    <ScrollView
      style={[styles.root, { backgroundColor: theme.background }]}
      contentContainerStyle={[
        styles.content,
        { paddingBottom: insets.bottom + Spacing.four, paddingTop: insets.top + Spacing.two },
      ]}
      showsVerticalScrollIndicator={false}
    >
      <View style={styles.inner}>
        <HeroIllustration />

        <View style={styles.body}>
          <View>
            <PaginationDots count={3} activeIndex={0} />
            <ThemedText type="title" style={[styles.title, { color: theme.text }]}>
              {welcomeCopy.title}
            </ThemedText>
            <ThemedText style={[styles.subtitle, { color: theme.textSecondary }]}>
              {welcomeCopy.subtitle}
            </ThemedText>
          </View>

          <View style={styles.actions}>
            <AppButton
              label="Bắt đầu ngay"
              onPress={() => router.replace('/')}
              accessibilityLabel="Bắt đầu khám phá ứng dụng học tập"
            />
            <FooterLink
              question={welcomeCopy.footerQuestion}
              action={welcomeCopy.footerAction}
              onPress={() => router.push('/login')}
            />
          </View>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
  content: {
    flexGrow: 1,
  },
  inner: {
    alignSelf: 'center',
    flex: 1,
    maxWidth: MaxContentWidth,
    width: '100%',
  },
  body: {
    flex: 1,
    gap: Spacing.four,
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.four,
    paddingTop: Spacing.five,
  },
  title: {
    fontSize: 30,
    fontWeight: '700',
    letterSpacing: -0.6,
    lineHeight: 38,
    marginTop: Spacing.three,
  },
  subtitle: {
    fontSize: 15,
    lineHeight: 22,
    marginTop: Spacing.two,
  },
  actions: {
    gap: Spacing.one,
    paddingBottom: Spacing.two,
  },
});
