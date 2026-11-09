import { StyleSheet, View } from 'react-native';
import { useTheme } from '@/hooks/use-theme';
import { Radius } from '@/constants/theme';

interface Props {
  progress: number;
}

export function ProgressBar({ progress }: Props) {
  const theme = useTheme();
  const clamped = Math.max(0, Math.min(100, progress));
  return (
    <View style={[styles.track, { backgroundColor: theme.primarySoft }]}>
      <View style={[styles.fill, { flex: clamped, backgroundColor: theme.primary }]} />
      <View style={{ flex: 100 - clamped }} />
    </View>
  );
}

const styles = StyleSheet.create({
  track: { height: 6, borderRadius: Radius.pill, overflow: 'hidden', flexDirection: 'row', marginTop: 6 },
  fill: { borderRadius: Radius.pill },
});
