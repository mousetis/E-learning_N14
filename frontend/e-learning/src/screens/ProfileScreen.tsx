import { useEffect, useState } from 'react';
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
import { useAppStore } from '@/store';
import { savedCourseService } from '@/services/savedCourseService';
import { enrollmentService } from '@/services/enrollmentService';
import type { SavedCourse } from '@/types/course';

type ProfileTab = 'saved' | 'certificates' | 'bio';

export default function ProfileScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { user, cart } = useAppStore();
  const [activeTab, setActiveTab] = useState<ProfileTab>('saved');
  const [savedList, setSavedList] = useState<SavedCourse[]>([]);
  const [certList, setCertList] = useState<any[]>([]);

  useEffect(() => {
    if (!user) {
      setSavedList([]);
      setCertList([]);
      return;
    }
    let mounted = true;
    (async () => {
      try {
        const [saved, certs] = await Promise.all([
          savedCourseService.getSavedCourses().catch(() => []),
          enrollmentService.getCertificates().catch(() => []),
        ]);
        if (mounted) {
          if (saved && Array.isArray(saved)) setSavedList(saved);
          if (certs && Array.isArray(certs)) {
            setCertList(
              certs.map((c: any) => ({
                id: c.code,
                title: c.courseTitle,
                issuer: 'Học viện E-Learning',
                date: c.issuedAt ? new Date(c.issuedAt).toLocaleDateString('vi-VN') : '10/2026',
                icon: '🎓',
              })),
            );
          }
        }
      } catch (err) {
        console.warn('Profile screen loading error:', err);
      }
    })();
    return () => {
      mounted = false;
    };
  }, [user]);

  const profile = {
    name: user?.name || 'Khách',
    role: user ? (user.role === 'INSTRUCTOR' ? 'Giảng viên' : 'Học viên') : 'Chưa đăng nhập',
    location: user?.location || (user ? 'TP. Hồ Chí Minh' : 'Chưa cập nhật'),
    level: user?.level || (user ? 'Học viên tiêu chuẩn' : 'Tài khoản khách'),
    avatar: user?.avatar || user?.avatarUrl,
    initials: user?.initials || (user ? user.name.slice(0, 2).toUpperCase() : 'K'),
    bio: user ? 'Học viên chăm chỉ tại E-Learning Platform' : 'Vui lòng đăng nhập để lưu trữ khóa học và chứng chỉ của bạn.',
    skills: user ? ['UI/UX Design', 'React Native', 'Mobile Dev'] : [],
    stats: {
      saved: user ? (user?.stats?.saved ?? savedList.length) : 0,
      inProgress: user ? (user?.stats?.inProgress ?? 0) : 0,
      completed: user ? (user?.stats?.completed ?? certList.length) : 0,
    },
  };

  const stats = [
    { label: 'Đã lưu', value: profile.stats.saved, key: 'saved' as ProfileTab },
    { label: 'Đang học', value: profile.stats.inProgress, route: '/my-courses' },
    { label: 'Hoàn thành', value: profile.stats.completed, route: '/my-courses' },
  ];

  return (
    <SafeAreaView style={[styles.safe, { backgroundColor: theme.background }]} edges={['top']}>
      <FlatList
        data={activeTab === 'saved' ? savedList : []}
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
                  {cart.length > 0 ? (
                    <View style={[styles.cartBadge, { backgroundColor: theme.primary }]}>
                      <Text style={styles.badgeText}>{cart.length}</Text>
                    </View>
                  ) : null}
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
                {profile.avatar ? (
                  <Image
                    source={{ uri: profile.avatar }}
                    style={styles.avatarImg}
                    contentFit="cover"
                    cachePolicy="memory-disk"
                    priority="high"
                  />
                ) : (
                  <View style={[styles.avatarPlaceholder, { backgroundColor: theme.accentYellowSoft }]}>
                    <ThemedText style={styles.avatarText}>{profile.initials}</ThemedText>
                  </View>
                )}
                <View style={[styles.verifiedBadge, { backgroundColor: theme.primary }]}>
                  <Text style={styles.verifiedGlyph}>✓</Text>
                </View>
              </View>

              <ThemedText style={[styles.name, { color: theme.text }]}>{profile.name}</ThemedText>
              <ThemedText style={[styles.role, { color: theme.primary }]}>{profile.role}</ThemedText>
              <ThemedText style={[styles.location, { color: theme.textSecondary }]}>
                📍 {profile.location} · {profile.level}
              </ThemedText>

              {!user && (
                <Pressable
                  onPress={() => router.push('/login')}
                  style={[styles.guestLoginBtn, { backgroundColor: theme.primary }]}>
                  <Text style={{ color: '#ffffff', fontWeight: '700', fontSize: 13 }}>
                    Đăng nhập / Đăng ký
                  </Text>
                </Pressable>
              )}

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
                  Khóa học đã lưu ({savedList.length})
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
                {certList.map((cert) => (
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
                  <Text style={[styles.bioText, { color: theme.textSecondary }]}>{profile.bio}</Text>
                  <Text style={[styles.bioTitle, { color: theme.text, marginTop: Spacing.two }]}>Kỹ năng chuyên môn</Text>
                  <View style={styles.skillsRow}>
                    {profile.skills.map((skill) => (
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
  topBar: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  h1: { fontSize: FontSizes.xl, fontWeight: '700' },
  topActions: { flexDirection: 'row', gap: Spacing.two },
  circleBtn: {
    width: 40,
    height: 40,
    borderRadius: Radius.pill,
    borderWidth: 1,
    alignItems: 'center',
    justifyContent: 'center',
    position: 'relative',
  },
  cartBadge: {
    position: 'absolute',
    top: -2,
    right: -2,
    minWidth: 18,
    height: 18,
    borderRadius: 9,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 4,
    zIndex: 10,
  },
  badgeText: {
    color: '#ffffff',
    fontSize: 10,
    fontWeight: '700',
    textAlign: 'center',
    textAlignVertical: 'center',
    includeFontPadding: false,
    lineHeight: 12,
  },
  profileCard: {
    padding: Spacing.four,
    borderRadius: Radius.xl,
    borderWidth: 1,
    alignItems: 'center',
    gap: Spacing.one,
  },
  avatarContainer: { position: 'relative', marginBottom: Spacing.two },
  avatarImg: { width: 84, height: 84, borderRadius: 42 },
  avatarPlaceholder: {
    width: 84,
    height: 84,
    borderRadius: 42,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarText: { fontSize: FontSizes.xl, fontWeight: '800' },
  verifiedBadge: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: 24,
    height: 24,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: '#fff',
  },
  verifiedGlyph: { color: '#fff', fontSize: 12, fontWeight: '800' },
  name: { fontSize: FontSizes.lg, fontWeight: '700' },
  role: { fontSize: FontSizes.sm, fontWeight: '600' },
  location: { fontSize: FontSizes.xs, marginBottom: Spacing.two },
  guestLoginBtn: {
    marginTop: Spacing.one,
    paddingHorizontal: Spacing.four,
    paddingVertical: Spacing.two,
    borderRadius: Radius.pill,
  },
  statsRow: {
    flexDirection: 'row',
    width: '100%',
    borderRadius: Radius.lg,
    borderWidth: 1,
    paddingVertical: Spacing.two,
    marginTop: Spacing.two,
  },
  statItem: { flex: 1, alignItems: 'center', gap: 2 },
  statValue: { fontSize: FontSizes.lg, fontWeight: '800' },
  statLabel: { fontSize: FontSizes.xs },
  tabSegment: {
    flexDirection: 'row',
    borderRadius: Radius.lg,
    borderWidth: 1,
    padding: 4,
    gap: 4,
  },
  tabSegmentBtn: {
    flex: 1,
    paddingVertical: Spacing.two,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  tabSegmentText: { fontSize: 12, fontWeight: '700' },
  sectionBlock: { gap: Spacing.two },
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
  certTitle: { fontSize: FontSizes.sm, fontWeight: '700' },
  certIssuer: { fontSize: FontSizes.xs },
  certBadge: { paddingHorizontal: Spacing.two, paddingVertical: 4, borderRadius: Radius.pill },
  certBadgeText: { fontSize: 10, fontWeight: '700' },
  bioCard: { padding: Spacing.three, borderRadius: Radius.lg, borderWidth: 1, gap: Spacing.two },
  bioTitle: { fontSize: FontSizes.sm, fontWeight: '700' },
  bioText: { fontSize: FontSizes.xs, lineHeight: 18 },
  skillsRow: { flexDirection: 'row', flexWrap: 'wrap', gap: Spacing.two },
  skillPill: { paddingHorizontal: Spacing.two, paddingVertical: 4, borderRadius: Radius.pill },
  skillText: { fontSize: 11, fontWeight: '700' },
});
