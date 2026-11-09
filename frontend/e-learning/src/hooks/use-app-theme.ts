import { useColorScheme } from 'react-native';

import { Colors, type AppColors } from '@/constants/theme';

export type AppTheme = AppColors & { isDark: boolean };

/**
 * Trả về palette đầy đủ (light/dark) kèm cờ isDark.
 * Bọc `Colors` để các màn hình mới dùng được token mở rộng (primary, star, pastel...).
 */
export function useAppTheme(): AppTheme {
  const scheme = useColorScheme();
  const isDark = scheme === 'dark';
  const palette: AppColors = isDark ? Colors.dark : Colors.light;

  return { ...palette, isDark };
}
