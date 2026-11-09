import type { ReactNode } from 'react';
import { StyleSheet, View, type StyleProp, type ViewStyle } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { MaxContentWidth } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';

type Props = {
  children: ReactNode;
  footer?: ReactNode;
  style?: StyleProp<ViewStyle>;
};

export function Screen({ children, footer, style }: Props) {
  const theme = useAppTheme();
  const insets = useSafeAreaInsets();

  return (
    <View
      style={[
        styles.root,
        { backgroundColor: theme.background, paddingTop: insets.top },
        style,
      ]}
    >
      <View style={styles.content}>{children}</View>
      {footer}
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
  content: {
    flex: 1,
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
  },
});
