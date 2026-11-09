import { StyleSheet, Text, View } from 'react-native';

const GOOGLE_BLUE = '#4285f4';

export type GoogleMarkProps = {
  size?: number;
};

export function GoogleMark({ size = 20 }: GoogleMarkProps) {
  return (
    <View
      style={[
        styles.mark,
        { borderColor: GOOGLE_BLUE, borderRadius: size / 2, height: size, width: size },
      ]}
    >
      <Text style={[styles.letter, { color: GOOGLE_BLUE, fontSize: size * 0.58 }]}>G</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  mark: {
    alignItems: 'center',
    borderWidth: 1.5,
    justifyContent: 'center',
  },
  letter: {
    fontWeight: '700',
    includeFontPadding: false,
    textAlign: 'center',
  },
});
