import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Radius, Spacing } from '@/constants/theme';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { ProjectFile } from '@/types/course';

type Props = {
  file: ProjectFile;
  onDownload?: () => void;
};

export function FileRow({ file, onDownload }: Props) {
  const theme = useAppTheme();

  return (
    <View
      style={[styles.row, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}
    >
      <View style={[styles.thumb, { backgroundColor: theme.peach }]}>
        <Text style={[styles.thumbText, { color: theme.onPastel }]}>TXT</Text>
      </View>
      <View style={styles.body}>
        <Text numberOfLines={1} style={[styles.name, { color: theme.text }]}>
          {file.name}
        </Text>
        <Text style={[styles.size, { color: theme.textSecondary }]}>{file.size}</Text>
      </View>
      <Pressable
        accessibilityRole="button"
        accessibilityLabel={`Tải về ${file.name}`}
        hitSlop={8}
        onPress={onDownload}
      >
        <Text style={[styles.download, { color: theme.primary }]}>Tải về</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
    borderRadius: Radius.lg,
    borderWidth: 1,
    padding: Spacing.two,
  },
  thumb: {
    width: 40,
    height: 40,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  thumbText: {
    fontSize: 9,
    fontWeight: '700',
  },
  body: {
    flex: 1,
    gap: Spacing.half,
  },
  name: {
    fontSize: 14,
    fontWeight: '600',
  },
  size: {
    fontSize: 12,
  },
  download: {
    fontSize: 13,
    fontWeight: '600',
  },
});
