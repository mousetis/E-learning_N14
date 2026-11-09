import { useState } from 'react';
import { useRouter } from '@/navigation/routerHook';
import { FlatList, Pressable, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Image } from 'expo-image';

import { ThemedText } from '@/components/themed-text';
import { SavedCourseCard } from '@/components/saved-course-card';
import { BottomNavBar } from '@/components/bottom-bar';
import { Icon } from '@/components/ui/icon';
import { useTheme } from '@/hooks/use-theme';
import { BottomTabInset, FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { currentUser, savedCourses } from '@/data/user';

type ProfileTab = 'saved' | 'certificates' | 'bio';

export default function ProfileScreen() {
  const router = useRouter();
  const theme = useTheme();
  const [activeTab, setActiveTab] = useState<ProfileTab>('saved');

  const stats = [
    { label: 'Đã lưu', value: currentUser.stats.saved, key: 'saved' as ProfileTab },
    { label: 'Đang học', value: currentUser.stats.inProgress, route: '/my-courses' },
    { label: 'Hoàn thành', value: currentUser.stats.completed, route: '/my-courses' },
  ];

  return (
    <SafeAreaView style={[styles.safe, { backgroundColor: theme.background }]} edges={['top']}>
      <FlatList
        data={activeTab === 'saved' ? savedCourses : []}
        keyExtractor={(item) => item.id}
        style={[styles.list, { maxWidth: MaxContentWidth }]}
        contentContainerStyle={{
          paddingHorizontal: Spacing.four,
          paddingTop: Spacing.three,
          paddingBottom: BottomTabInset + 70,
          gap: Spacing.three,
        }}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <View style={{ gap: Spacing.four, marginBottom: Spacing.two }}>
            {/* Top Navigation */}
            <View style={styles.topBar}>
              <ThemedText style={[styles.h1, { color: theme.text }]} accessibilityRole="header">
                Hồ sơ của tôi
              </ThemedText>
              <View style={styles.topActions}>
                <Pressable
                  accessibilityRole="button"
                  accessibilityLabel="Giỏ hàng"
                  onPress={() => router.push('/cart')}
                  style={[styles.circleBtn, { backgroundColor: theme.surface, borderColor: theme.border }]}
                >
                  <Icon sf="cart.fill" emoji="🛒" size={18} color={theme.text} />
                </Pressable>
                <Pressable
                  accessibilityRole="button"
                  accessibilityLabel="Cài đặt tài khoản"
                  onPress={() => router.push('/settings')}
                  style={[styles.circleBtn, { backgroundColor: theme.surface, borderColor: theme.border }]}
                >
                  <Icon sf="gearshape.fill" emoji="⚙️" size={18} color={theme.text} />
                </Pressable>
              </View>
            </View>

            {/* Profile Hero Card */}
            <View style={[styles.profileCard, { backgroundColor: theme.surface, borderColor: theme.border }]}>
              <View style={styles.avatarContainer}>
                {currentUser.avatar ? (
                  <Image
                    source={{ uri: currentUser.avatar }}
                    style={styles.avatarImg}
                    contentFit="cover"
                    cachePolicy="memory-disk"
                    priority="high"
                  />
                ) : (
                  <View style={[styles.avatarPlaceholder, { backgroundColor: theme.accentYellowSoft }]}>
                    <ThemedText style={styles.avatarText}>{currentUser.initials}</ThemedText>
                  </View>
                )}
                <View style={[styles.verifiedBadge, { backgroundColor: theme.primary }]}>
                  <Text style={styles.verifiedGlyph}>✓</Text>
                </View>
              </View>

              <ThemedText style={[styles.name, { color: theme.text }]}>{currentUser.name}</ThemedText>
              <ThemedText style={[styles.role, { color: theme.primary }]}>{currentUser.role}</ThemedText>
              <ThemedText style={[styles.location, { color: theme.textSecondary }]}>
                📍 {currentUser.location} · {currentUser.level}
              </ThemedText>

              {/* Stats Box */}
              <View style={[styles.statsRow, { backgroundColor: theme.chipBackground, borderColor: theme.border }]}>
                {stats.map((s, index) => (
                  <Pressable
                    key={s.label}
                    onPress={() => {
                      if (s.key) setActiveTab(s.key);
                      else if (s.route) router.push(s.route as any);
                    }}
                    style={[
                      styles.statItem,
                      index < stats.length - 1 && { borderRightWidth: StyleSheet.hairlineWidth, borderRightColor: theme.border },
                    ]}
                  >
                    <ThemedText style={[styles.statValue, { color: theme.text }]}>{s.value}</ThemedText>
                    <ThemedText style={[styles.statLabel, { color: theme.textSecondary }]}>{s.label}</ThemedText>
                  </Pressable>
                ))}
              </View>
            </View>

            {/* Tab Filter Segment */}
            <View style={[styles.tabSegment, { backgroundColor: theme.surface, borderColor: theme.border }]}>
              <Pressable
                onPress={() => setActiveTab('saved')}
                style={[
                  styles.tabSegmentBtn,
                  activeTab === 'saved' && { backgroundColor: theme.primary },
                ]}
              >
                <Text
                  style={[
                    styles.tabSegmentText,
                    { color: activeTab === 'saved' ? '#ffffff' : theme.textSecondary },
                  ]}
                >
                  Khóa học đã lưu ({savedCourses.length})
                </Text>
              </Pressable>

              <Pressable
                onPress={() => setActiveTab('certificates')}
                style={[
                  styles.tabSegmentBtn,
                  activeTab === 'certificates' && { backgroundColor: theme.primary },
                ]}
              >
                <Text
                  style={[
                    styles.tabSegmentText,
                    { color: activeTab === 'certificates' ? '#ffffff' : theme.textSecondary },
                  ]}
                >
                  Chứng chỉ
                </Text>
              </Pressable>

              <Pressable
                onPress={() => setActiveTab('bio')}
                style={[
                  styles.tabSegmentBtn,
                  activeTab === 'bio' && { backgroundColor: theme.primary },
                ]}
              >
                <Text
                  style={[
                    styles.tabSegmentText,
                    { color: activeTab === 'bio' ? '#ffffff' : theme.textSecondary },
                  ]}
                >
                  Kỹ năng & Bio
                </Text>
              </Pressable>
            </View>

            {/* Content for Non-Saved Tabs */}
            {activeTab === 'certificates' ? (
              <View style={styles.sectionBlock}>
                {currentUser.certificates.map((cert) => (
                  <View
                    key={cert.id}
                    style={[styles.certCard, { backgroundColor: theme.surface, borderColor: theme.border }]}
                  >
                    <Text style={styles.certIcon}>{cert.icon}</Text>
                    <View style={styles.certBody}>
                      <Text style={[styles.certTitle, { color: theme.text }]}>{cert.title}</Text>
                      <Text style={[styles.certIssuer, { color: theme.textSecondary }]}>
                        {cert.issuer} · Cấp ngày {cert.date}
                      </Text>
                    </View>
                    <View style={[styles.certBadge, { backgroundColor: theme.successSoft }]}>
                      <Text style={[styles.certBadgeText, { color: theme.success }]}>Đã cấp</Text>
                    </View>
                  </View>
                ))}
              </View>
            ) : null}

            {activeTab === 'bio' ? (
              <View style={styles.sectionBlock}>
                <View style={[styles.bioCard, { backgroundColor: theme.surface, borderColor: theme.border }]}>
                  <Text style={[styles.bioTitle, { color: theme.text }]}>Giới thiệu bản thân</Text>
                  <Text style={[styles.bioText, { color: theme.textSecondary }]}>{currentUser.bio}</Text>
                  <Text style={[styles.bioTitle, { color: theme.text, marginTop: Spacing.two }]}>Kỹ năng chuyên môn</Text>
                  <View style={styles.skillsRow}>
                    {currentUser.skills.map((skill) => (
                      <View key={skill} style={[styles.skillPill, { backgroundColor: theme.primarySoft }]}>
                        <Text style={[styles.skillText, { color: theme.primary }]}>{skill}</Text>
                      </View>
                    ))}
                  </View>
                </View>
              </View>
            ) : null}
          </View>
        }
        renderItem={({ item }) => (
          <SavedCourseCard
            course={item}
            onPress={() => router.push(`/course/${item.id}` as any)}
          />
        )}
      />
      <BottomNavBar currentTab="profile" />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1 },
  list: { width: '100%', alignSelf: 'center' },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  h1: { fontSize: FontSizes.xl, fontWeight: '700' },
  topActions: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  circleBtn: {
    width: 42,
    height: 42,
    borderRadius: Radius.pill,
    borderWidth: 1,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 4,
    elevation: 1,
  },
  profileCard: {
    alignItems: 'center',
    padding: Spacing.four,
    borderRadius: Radius.xxl,
    borderWidth: 1,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  avatarContainer: {
    position: 'relative',
    marginBottom: Spacing.three,
  },
  avatarImg: {
    width: 100,
    height: 100,
    borderRadius: 50,
  },
  avatarPlaceholder: {
    width: 100,
    height: 100,
    borderRadius: 50,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarText: { fontSize: FontSizes.xxl, fontWeight: '800', color: '#181528' },
  verifiedBadge: {
    position: 'absolute',
    bottom: 0,
    right: 4,
    width: 26,
    height: 26,
    borderRadius: 13,
    borderWidth: 2,
    borderColor: '#ffffff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  verifiedGlyph: {
    color: '#ffffff',
    fontSize: 13,
    fontWeight: '800',
  },
  name: { fontSize: FontSizes.xl, fontWeight: '800' },
  role: { fontSize: FontSizes.md, fontWeight: '700', marginTop: 2 },
  location: { fontSize: FontSizes.sm, marginTop: 4 },
  statsRow: {
    flexDirection: 'row',
    borderRadius: Radius.xl,
    borderWidth: 1,
    paddingVertical: Spacing.three,
    marginTop: Spacing.four,
    width: '100%',
  },
  statItem: { flex: 1, alignItems: 'center', gap: 2 },
  statValue: { fontSize: FontSizes.xl, fontWeight: '800' },
  statLabel: { fontSize: FontSizes.sm },
  tabSegment: {
    flexDirection: 'row',
    borderRadius: Radius.pill,
    borderWidth: 1,
    padding: 4,
    gap: 4,
  },
  tabSegmentBtn: {
    flex: 1,
    paddingVertical: 9,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  tabSegmentText: {
    fontSize: FontSizes.xs,
    fontWeight: '700',
  },
  sectionBlock: {
    gap: Spacing.two + 2,
    marginTop: Spacing.one,
  },
  certCard: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: Spacing.three,
    borderRadius: Radius.lg,
    borderWidth: 1,
    gap: Spacing.three,
  },
  certIcon: { fontSize: 28 },
  certBody: { flex: 1, gap: 2 },
  certTitle: { fontSize: FontSizes.md, fontWeight: '700' },
  certIssuer: { fontSize: FontSizes.xs },
  certBadge: {
    paddingHorizontal: Spacing.two,
    paddingVertical: 4,
    borderRadius: Radius.pill,
  },
  certBadgeText: { fontSize: FontSizes.xs, fontWeight: '700' },
  bioCard: {
    padding: Spacing.four,
    borderRadius: Radius.lg,
    borderWidth: 1,
    gap: Spacing.two,
  },
  bioTitle: { fontSize: FontSizes.md, fontWeight: '700' },
  bioText: { fontSize: FontSizes.sm, lineHeight: 22 },
  skillsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
    marginTop: Spacing.half,
  },
  skillPill: {
    paddingHorizontal: Spacing.three,
    paddingVertical: 6,
    borderRadius: Radius.pill,
  },
  skillText: {
    fontSize: FontSizes.xs,
    fontWeight: '700',
  },
});
