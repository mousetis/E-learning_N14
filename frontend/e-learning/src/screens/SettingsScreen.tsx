import { Appearance, Pressable, ScrollView, StyleSheet, Switch, Text, View } from 'react-native';
import { useCallback, useState } from 'react';
import { useRouter } from '@/navigation/routerHook';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Card } from '@/components/ui/card';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SettingsRow } from '@/components/ui/settings-row';
import { FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { accountSettings, defaultLanguageLabel, supportSettings } from '@/data/settings';
import { currentUser } from '@/data/user';
import { useColorScheme } from '@/hooks/use-color-scheme';
import { useTheme } from '@/hooks/use-theme';

const AVATAR_LABEL_COLOR = '#181528';

export default function SettingsScreen() {
  const router = useRouter();
  const theme = useTheme();
  const insets = useSafeAreaInsets();
  const scheme = useColorScheme();

  const [darkMode, setDarkMode] = useState(scheme === 'dark');
  const [notice, setNotice] = useState<string | null>(null);

  const showNotice = useCallback((label: string) => {
    setNotice(`${label} sẽ sớm được cập nhật.`);
  }, []);

  const handleRowPress = useCallback((id: string, label: string) => {
    if (id === 'logout') {
      router.replace('/welcome');
    } else if (id === 'notifications') {
      router.push('/notifications');
    } else if (id === 'payment-methods') {
      router.push('/checkout');
    } else {
      showNotice(label);
    }
  }, [router, showNotice]);

  const handleToggleDarkMode = useCallback((value: boolean) => {
    setDarkMode(value);
    Appearance.setColorScheme(value ? 'dark' : 'light');
  }, []);

  return (
    <View
      style={[
        styles.root,
        { backgroundColor: theme.background, paddingTop: insets.top + Spacing.two },
      ]}>
      <View style={styles.content}>
        <ScreenHeader title="Cài đặt" />
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={[styles.scrollContent, { paddingBottom: insets.bottom + Spacing.five }]}
        showsVerticalScrollIndicator={false}>
        <View style={styles.column}>
          {notice ? (
            <Text
              style={[
                styles.notice,
                { backgroundColor: theme.primarySoft, color: theme.primary },
              ]}>
              {notice}
            </Text>
          ) : null}

          <Card style={styles.profileCard}>
            <View style={[styles.avatar, { backgroundColor: currentUser.avatarColor }]}>
              <Text style={styles.avatarLabel}>{currentUser.initials}</Text>
            </View>
            <View style={styles.profileInfo}>
              <Text numberOfLines={1} style={[styles.profileName, { color: theme.text }]}>
                {currentUser.name}
              </Text>
              <Text numberOfLines={1} style={[styles.profileEmail, { color: theme.textSecondary }]}>
                {currentUser.email}
              </Text>
            </View>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Sửa thông tin cá nhân"
              hitSlop={Spacing.two}
              onPress={() => showNotice('Chỉnh sửa hồ sơ')}>
              {({ pressed }) => (
                <Text style={[styles.editLabel, { color: theme.primary, opacity: pressed ? 0.6 : 1 }]}>
                  Sửa
                </Text>
              )}
            </Pressable>
          </Card>

          <Card padded={false}>
            {accountSettings.items.map((item, index) => (
              <SettingsRow
                key={item.id}
                label={item.label}
                showDivider={index < accountSettings.items.length - 1}
                onPress={() => handleRowPress(item.id, item.label)}
              />
            ))}
          </Card>

          <Card padded={false}>
            <SettingsRow
              label="Chế độ tối"
              showDivider
              right={
                <Switch
                  value={darkMode}
                  onValueChange={handleToggleDarkMode}
                  trackColor={{ false: theme.switchTrackOff, true: theme.primary }}
                  thumbColor="#ffffff"
                  accessibilityLabel="Chế độ tối"
                />
              }
            />
            <SettingsRow
              label="Ngôn ngữ"
              value={defaultLanguageLabel}
              onPress={() => showNotice('Ngôn ngữ')}
            />
          </Card>

          <Card padded={false}>
            {supportSettings.items.map((item, index) => (
              <SettingsRow
                key={item.id}
                label={item.label}
                tone={item.tone}
                showChevron={item.tone !== 'danger'}
                showDivider={index < supportSettings.items.length - 1}
                onPress={() => handleRowPress(item.id, item.label)}
              />
            ))}
          </Card>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
  content: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    paddingHorizontal: Spacing.three,
  },
  scroll: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: Spacing.three,
    paddingTop: Spacing.three,
  },
  column: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    gap: Spacing.three,
  },
  notice: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Radius.md,
    overflow: 'hidden',
  },
  profileCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.three,
  },
  avatar: {
    width: 56,
    height: 56,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarLabel: {
    fontSize: FontSizes.lg,
    fontWeight: '700',
    color: AVATAR_LABEL_COLOR,
  },
  profileInfo: {
    flex: 1,
    gap: Spacing.half,
  },
  profileName: {
    fontSize: FontSizes.lg,
    fontWeight: '700',
  },
  profileEmail: {
    fontSize: FontSizes.sm,
  },
  editLabel: {
    fontSize: FontSizes.md,
    fontWeight: '700',
  },
});
