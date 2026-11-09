import { StyleSheet, TextInput, View } from 'react-native';

import { Icon } from '@/components/ui/icon';
import { Radius, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

type SearchInputProps = {
  value: string;
  onChangeText: (value: string) => void;
  placeholder?: string;
  onSubmitEditing?: () => void;
  autoFocus?: boolean;
};

export function SearchInput({
  value,
  onChangeText,
  placeholder = 'Tìm khóa học',
  onSubmitEditing,
  autoFocus = false,
}: SearchInputProps) {
  const theme = useTheme();

  return (
    <View style={[styles.container, { backgroundColor: theme.card }]}>
      <Icon name="magnifyingglass" glyph="⌕" size={18} color={theme.textSecondary} />
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor={theme.textSecondary}
        onSubmitEditing={onSubmitEditing}
        returnKeyType="search"
        autoFocus={autoFocus}
        accessibilityLabel={placeholder}
        style={[styles.input, { color: theme.text }]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    height: 52,
    borderRadius: Radius.lg,
    paddingHorizontal: Spacing.three,
  },
  input: {
    flex: 1,
    height: '100%',
    fontSize: 15,
    padding: 0,
  },
});
