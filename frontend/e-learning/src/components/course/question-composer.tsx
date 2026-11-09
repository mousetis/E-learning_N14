import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  value: string;
  onChangeText: (text: string) => void;
  onSubmit: () => void;
};

export function QuestionComposer({ value, onChangeText, onSubmit }: Props) {
  const theme = useAppTheme();
  const insets = useSafeAreaInsets();
  const disabled = value.trim().length === 0;

  return (
    <View
      style={[
        styles.bar,
        {
          backgroundColor: theme.backgroundElement,
          borderTopColor: theme.border,
          paddingBottom: insets.bottom + Spacing.two,
        },
      ]}
    >
      <View style={styles.inner}>
        <TextInput
          accessibilityLabel="Viết câu hỏi"
          multiline
          onChangeText={onChangeText}
          placeholder="Viết câu hỏi..."
          placeholderTextColor={theme.textSecondary}
          style={[
            styles.input,
            { backgroundColor: theme.background, color: theme.text, borderColor: theme.border },
          ]}
          value={value}
        />
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Gửi câu hỏi"
          accessibilityState={{ disabled }}
          disabled={disabled}
          onPress={onSubmit}
          style={[styles.send, { backgroundColor: disabled ? theme.primarySoft : theme.primary }]}
        >
          <Text style={[styles.sendIcon, { color: disabled ? theme.primaryMuted : theme.onPrimary }]}>
            ➤
          </Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  bar: {
    borderTopWidth: 1,
    paddingTop: Spacing.two,
    paddingHorizontal: Spacing.three,
  },
  inner: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: Spacing.two,
  },
  input: {
    flex: 1,
    minHeight: 44,
    maxHeight: 110,
    borderRadius: Radius.lg,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    paddingTop: Spacing.two + 2,
    paddingBottom: Spacing.two + 2,
    fontSize: 14,
  },
  send: {
    width: 44,
    height: 44,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  sendIcon: {
    fontSize: 16,
  },
});
