import { Platform, Text, type StyleProp, type TextStyle } from 'react-native';
import { useTheme } from '@/hooks/use-theme';

let SymbolViewComponent: any = null;
if (Platform.OS === 'ios') {
  try {
    SymbolViewComponent = require('expo-symbols').SymbolView;
  } catch {}
}

export type IconProps = {
  name?: string;
  sf?: string;
  glyph?: string;
  emoji?: string;
  size?: number;
  color?: string;
  style?: StyleProp<TextStyle>;
};

export function Icon({ name, sf, glyph, emoji, size = 18, color, style }: IconProps) {
  const theme = useTheme();
  const tintColor = color ?? theme.text;
  const symbolName = name || sf;
  const fallbackGlyph = glyph || emoji || '•';

  if (Platform.OS === 'ios' && SymbolViewComponent && symbolName) {
    return (
      <SymbolViewComponent
        name={symbolName as never}
        tintColor={tintColor}
        style={{ width: size, height: size }}
      />
    );
  }

  return (
    <Text
      style={[
        { fontSize: size, lineHeight: size + 2, color: tintColor, textAlign: 'center' },
        style,
      ]}>
      {fallbackGlyph}
    </Text>
  );
}
