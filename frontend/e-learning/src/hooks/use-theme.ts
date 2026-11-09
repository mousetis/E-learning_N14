import { Colors, type Theme } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';

export function useTheme(): Theme {
  const scheme = useColorScheme() ?? 'light';
  return Colors[scheme];
}
