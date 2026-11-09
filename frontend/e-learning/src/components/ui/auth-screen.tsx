import type { ReactNode } from 'react';
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type AuthScreenProps = {
  children: ReactNode;
  testID?: string;
};

export function AuthScreen({ children, testID }: AuthScreenProps) {
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  return (
    <View style={[styles.root, { backgroundColor: theme.background }]} testID={testID}>
      <View pointerEvents="none" style={[styles.blob, { backgroundColor: theme.primarySoft }]} />
      <View pointerEvents="none" style={[styles.blobAccent, { backgroundColor: theme.accent }]} />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        style={styles.flex}
      >
        <ScrollView
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
          contentContainerStyle={[
            styles.content,
            {
              paddingTop: insets.top + Spacing.four,
              paddingBottom: insets.bottom + Spacing.five,
            },
          ]}
        >
          <View style={styles.inner}>{children}</View>
        </ScrollView>
      </KeyboardAvoidingView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    overflow: 'hidden',
  },
  flex: {
    flex: 1,
  },
  blob: {
    borderRadius: 160,
    height: 320,
    position: 'absolute',
    right: -120,
    top: -140,
    width: 320,
  },
  blobAccent: {
    borderRadius: 70,
    bottom: -50,
    height: 140,
    left: -60,
    opacity: 0.6,
    position: 'absolute',
    width: 140,
  },
  content: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: Spacing.four,
  },
  inner: {
    alignSelf: 'center',
    maxWidth: MaxContentWidth,
    width: '100%',
  },
});
