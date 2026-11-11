import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View, type TextInputProps } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

export type FormFieldProps = {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  placeholder?: string;
  secureTextEntry?: boolean;
  rightLabel?: string;
  onRightLabelPress?: () => void;
  rightLabelAccessibilityLabel?: string;
  error?: string;
  keyboardType?: TextInputProps['keyboardType'];
  autoCapitalize?: TextInputProps['autoCapitalize'];
  autoComplete?: TextInputProps['autoComplete'];
  textContentType?: TextInputProps['textContentType'];
  returnKeyType?: TextInputProps['returnKeyType'];
  onSubmitEditing?: TextInputProps['onSubmitEditing'];
  maxLength?: number;
};

export function FormField({
  label,
  value,
  onChangeText,
  placeholder,
  secureTextEntry = false,
  rightLabel,
  onRightLabelPress,
  rightLabelAccessibilityLabel,
  error,
  keyboardType,
  autoCapitalize,
  autoComplete,
  textContentType,
  returnKeyType,
  onSubmitEditing,
  maxLength,
}: FormFieldProps) {
  const theme = useTheme();
  const [focused, setFocused] = useState(false);

  const borderColor = error ? theme.danger : focused ? theme.primary : theme.border;

  return (
    <View style={styles.wrapper}>
      <Text style={[styles.label, { color: theme.text }]}>{label}</Text>
      <View
        style={[
          styles.inputWrapper,
          { backgroundColor: theme.backgroundElement, borderColor },
        ]}
      >
        <TextInput
          value={value}
          onChangeText={onChangeText}
          placeholder={placeholder}
          placeholderTextColor={theme.placeholder}
          secureTextEntry={secureTextEntry}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          style={[styles.input, { color: theme.text }]}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
          autoComplete={autoComplete}
          textContentType={textContentType}
          returnKeyType={returnKeyType}
          onSubmitEditing={onSubmitEditing}
          maxLength={maxLength}
          selectionColor={theme.primary}
          accessibilityLabel={label}
        />
        {rightLabel ? (
          <Pressable
            onPress={onRightLabelPress}
            accessibilityRole="button"
            accessibilityLabel={rightLabelAccessibilityLabel ?? rightLabel}
            hitSlop={Spacing.two}
            style={styles.rightAction}
          >
            <Text style={[styles.rightLabel, { color: theme.primary }]}>{rightLabel}</Text>
          </Pressable>
        ) : null}
      </View>
      {error ? (
        <Text style={[styles.error, { color: theme.danger }]} accessibilityLiveRegion="polite">
          {error}
        </Text>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    gap: Spacing.two,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    letterSpacing: 0.1,
  },
  inputWrapper: {
    alignItems: 'center',
    borderRadius: Radius.md,
    borderWidth: 1,
    flexDirection: 'row',
    minHeight: 52,
    paddingHorizontal: Spacing.three,
  },
  input: {
    flex: 1,
    fontSize: 15,
    paddingVertical: Spacing.three,
  },
  rightAction: {
    paddingLeft: Spacing.two,
  },
  rightLabel: {
    fontSize: 13,
    fontWeight: '600',
  },
  error: {
    fontSize: 12,
    lineHeight: 16,
  },
});
