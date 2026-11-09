import { StyleSheet, View, Pressable, Text } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useTheme } from '@/hooks/use-theme';
import { Icon } from '@/components/ui/icon';
import { FontSizes, Spacing, MaxContentWidth } from '@/constants/theme';
import { navigate } from '@/navigation/navigationRef';

export type TabName = 'home' | 'search' | 'courses' | 'profile';

interface Props {
  currentTab?: TabName;
}

export function BottomNavBar({ currentTab = 'home' }: Props) {
  const insets = useSafeAreaInsets();
  const theme = useTheme();

  const active: TabName = currentTab;

  // Exactly 4 tabs matching Figma design
  const tabs: { key: TabName; label: string; sf: string; emoji: string; screen: string }[] = [
    { key: 'home', label: 'Trang chủ', sf: 'house.fill', emoji: '🏠', screen: 'Home' },
    { key: 'search', label: 'Tìm kiếm', sf: 'magnifyingglass', emoji: '🔍', screen: 'Search' },
    { key: 'courses', label: 'Khóa học', sf: 'book.fill', emoji: '📚', screen: 'MyCourses' },
    { key: 'profile', label: 'Hồ sơ', sf: 'person.fill', emoji: '👤', screen: 'Profile' },
  ];

  return (
    <View
      style={[
        styles.container,
        {
          backgroundColor: theme.backgroundElement,
          borderTopColor: theme.border,
          paddingBottom: Math.max(insets.bottom, Spacing.two),
        },
      ]}
    >
      <View style={[styles.inner, { maxWidth: MaxContentWidth }]}>
        {tabs.map((tab) => {
          const isActive = active === tab.key;
          const color = isActive ? theme.primary : theme.textSecondary;
          return (
            <Pressable
              key={tab.key}
              accessibilityRole="tab"
              accessibilityState={{ selected: isActive }}
              accessibilityLabel={tab.label}
              onPress={() => {
                if (!isActive) {
                  navigate(tab.screen as any);
                }
              }}
              style={styles.tabBtn}
            >
              <Icon
                sf={tab.sf}
                name={tab.sf}
                emoji={tab.emoji}
                glyph={tab.emoji}
                size={22}
                color={color}
              />
              <Text
                style={[
                  styles.tabLabel,
                  {
                    color,
                    fontWeight: isActive ? '700' : '500',
                  },
                ]}
              >
                {tab.label}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    borderTopWidth: StyleSheet.hairlineWidth,
    paddingTop: Spacing.one + 2,
    elevation: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: -2 },
    shadowOpacity: 0.06,
    shadowRadius: 4,
    zIndex: 99,
  },
  inner: {
    width: '100%',
    alignSelf: 'center',
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  tabBtn: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: Spacing.half,
    gap: 3,
  },
  tabLabel: {
    fontSize: FontSizes.xs,
  },
});
