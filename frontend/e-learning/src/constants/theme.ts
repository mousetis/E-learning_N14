import { Platform } from 'react-native';

/**
 * Bảng màu light/dark tích hợp đầy đủ token thiết kế Figma E-Learning App.
 */
export const Colors = {
  light: {
    text: '#181528',
    textSecondary: '#78758e',
    background: '#f8f7ff',
    backgroundElement: '#ffffff',
    backgroundSelected: '#ece9fe',
    surface: '#ffffff',
    card: '#ffffff',
    sheet: '#ffffff',
    handle: '#d5d2eb',
    divider: '#ece9fe',
    border: '#ece9fe',
    borderStrong: '#d5d2eb',
    muted: '#78758e',
    placeholder: '#78758e',
    chipBackground: '#f6f3ff',
    dotInactive: '#d5d2eb',
    primary: '#5641f5',
    primarySoft: '#ece9fe',
    primaryLight: '#ece9fe',
    primaryMuted: '#c7c2fe',
    primaryBorder: '#c7c2fe',
    primaryPressed: '#4732db',
    onPrimary: '#ffffff',
    accent: '#5641f5',
    accentDark: '#181528',
    accentYellow: '#ffd466',
    accentYellowSoft: '#ffeb8d',
    gold: '#ffd466',
    star: '#ffd466',
    banner: '#5641f5',
    bannerDecoration: '#725af7',
    ink: '#181528',
    onInk: '#ffffff',
    onPastel: '#181528',
    peach: '#ffd9a8',
    peachSoft: '#ffd9a8',
    pink: '#ffbfd2',
    mint: '#b2eedf',
    lavender: '#c9bcff',
    blue: '#bce0fd',
    yellow: '#ffe58a',
    danger: '#b91c1c',
    success: '#0f9d76',
    successSoft: '#b8f1e0',
    warningSoft: '#ffeb8d',
    infoSoft: '#bce0fd',
    icon: '#6e6a85',
    tabInactive: '#6e6a85',
    switchTrackOff: '#d5d2eb',
    shadow: 'rgba(24, 21, 40, 0.1)',
    overlay: 'rgba(24, 21, 40, 0.45)',
  },
  dark: {
    text: '#f4f2ff',
    textSecondary: '#a9a4c4',
    background: '#121019',
    backgroundElement: '#1c1929',
    backgroundSelected: '#2a2544',
    surface: '#1c1929',
    card: '#1c1929',
    sheet: '#1c1929',
    handle: '#3a3358',
    divider: '#2a2544',
    border: '#2a2544',
    borderStrong: '#3a3358',
    muted: '#a9a4c4',
    placeholder: '#a9a4c4',
    chipBackground: '#1e1b2e',
    dotInactive: '#3a3358',
    primary: '#8574ff',
    primarySoft: '#2a2544',
    primaryLight: '#2a2544',
    primaryMuted: '#4b3fb0',
    primaryBorder: '#4b3fb0',
    primaryPressed: '#705ef0',
    onPrimary: '#ffffff',
    accent: '#8574ff',
    accentDark: '#f4f2ff',
    accentYellow: '#ffd466',
    accentYellowSoft: '#3a3320',
    gold: '#ffd466',
    star: '#ffd466',
    banner: '#4732db',
    bannerDecoration: '#5641f5',
    ink: '#f4f2ff',
    onInk: '#121019',
    onPastel: '#ffffff',
    peach: '#3a2a1c',
    peachSoft: '#3a2a1c',
    pink: '#3a1f28',
    mint: '#1a332d',
    lavender: '#2b2447',
    blue: '#1c2938',
    yellow: '#383218',
    danger: '#f87171',
    success: '#4ade9f',
    successSoft: '#14342a',
    warningSoft: '#3a3320',
    infoSoft: '#17293a',
    icon: '#a9a4c4',
    tabInactive: '#a9a4c4',
    switchTrackOff: '#3a3358',
    shadow: 'rgba(0, 0, 0, 0.45)',
    overlay: 'rgba(0, 0, 0, 0.6)',
  },
} as const;

/** Màu minh họa và tag category (dùng chung) */
export const Tints = {
  lavender: '#c7c2fe',
  peach: '#ffd9a8',
  mint: '#b8f1e0',
  pink: '#ffc5d9',
  sky: '#bce0fd',
  lemon: '#ffd466',
} as const;

export type ThemeColorName = keyof typeof Colors.light;
export type ThemeColors = Record<ThemeColorName, string>;
export type Theme = Record<ThemeColorName, string>;
export type AppColors = Record<ThemeColorName, string>;
export type ThemeColor = keyof typeof Colors.light;

const fallbackFonts = {
  sans: 'System',
  serif: 'serif',
  rounded: 'System',
  mono: 'monospace',
};

export const Fonts =
  Platform.select({
    ios: {
      sans: 'System',
      serif: 'ui-serif',
      rounded: 'ui-rounded',
      mono: 'ui-monospace',
    },
    android: {
      sans: 'sans-serif',
      serif: 'serif',
      rounded: 'sans-serif',
      mono: 'monospace',
    },
    web: {
      sans: 'system-ui, -apple-system, "Segoe UI", Roboto, sans-serif',
      serif: 'Georgia, "Times New Roman", serif',
      rounded: '"SF Pro Rounded", system-ui, sans-serif',
      mono: 'ui-monospace, SFMono-Regular, Menlo, monospace',
    },
  }) ?? fallbackFonts;

export const FontSizes = {
  xs: 11,
  sm: 13,
  md: 15,
  base: 15,
  lg: 17,
  xl: 20,
  xxl: 24,
  display: 28,
} as const;

export const Spacing = {
  half: 2,
  one: 4,
  two: 8,
  three: 16,
  four: 24,
  five: 32,
  six: 64,
} as const;

export const Radius = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 28,
  pill: 999,
} as const;

export const BottomTabInset =
  Platform.select({ ios: 84, android: 64, web: 72, default: 64 }) ?? 64;

export const MaxContentWidth = 640;
