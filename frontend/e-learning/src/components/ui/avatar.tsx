import { StyleSheet, Text, View } from 'react-native';
import { Image } from 'expo-image';

import { useAppTheme } from '@/hooks/use-app-theme';
import type { AvatarTone } from '@/types/course';

type Props = {
  name: string;
  tone?: AvatarTone;
  size?: number;
  uri?: string;
};

function getInitials(name: string): string {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('');
}

export function Avatar({ name, tone = 'pink', size = 44, uri }: Props) {
  const theme = useAppTheme();

  return (
    <View
      accessibilityLabel={name}
      style={[
        styles.wrap,
        {
          width: size,
          height: size,
          borderRadius: size / 2,
          backgroundColor: (theme[tone as keyof typeof theme] as string) || theme.primarySoft,
        },
      ]}
    >
      {uri ? (
        <Image
          source={{ uri }}
          style={{ width: size, height: size, borderRadius: size / 2 }}
          contentFit="cover"
          transition={200}
        />
      ) : (
        <Text style={[styles.text, { color: theme.onPastel, fontSize: size * 0.36 }]}>
          {getInitials(name)}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  text: {
    fontWeight: '700',
  },
});
