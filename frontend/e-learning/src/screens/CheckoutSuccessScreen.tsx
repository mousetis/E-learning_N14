import { router, useLocalSearchParams } from '@/navigation/routerHook';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Card } from '@/components/ui/card';
import { InfoRow } from '@/components/ui/info-row';
import { PrimaryButton } from '@/components/ui/primary-button';
import { FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { cartItems, cartOrderId } from '@/data/cart';
import { useTheme } from '@/hooks/use-theme';
import { formatUsd } from '@/utils/format';

const DEFAULT_METHOD_LABEL = 'Thẻ ngân hàng';
const DEFAULT_TOTAL = 160;

function readParam(value: string | string[] | undefined, fallback: string): string {
  if (Array.isArray(value)) {
    return value[0] ?? fallback;
  }
  return value && value.length > 0 ? value : fallback;
}

export default function CheckoutSuccessScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams<{ orderId?: string; method?: string; total?: string }>();

  const orderId = readParam(params.orderId, cartOrderId);
  const methodLabel = readParam(params.method, DEFAULT_METHOD_LABEL);
  const parsedTotal = Number(readParam(params.total, String(DEFAULT_TOTAL)));
  const total = Number.isFinite(parsedTotal) ? parsedTotal : DEFAULT_TOTAL;

  return (
    <ScrollView
      style={[styles.root, { backgroundColor: theme.background }]}
      contentContainerStyle={[
        styles.content,
        {
          paddingTop: insets.top + Spacing.six,
          paddingBottom: insets.bottom + Spacing.four,
        },
      ]}
      showsVerticalScrollIndicator={false}>
      <View style={styles.inner}>
        <View style={[styles.badgeOuter, { backgroundColor: theme.primarySoft }]}>
          <View style={[styles.badgeInner, { backgroundColor: theme.primary }]}>
            <Text style={styles.badgeGlyph}>✓</Text>
          </View>
        </View>

        <Text style={[styles.title, { color: theme.text }]}>Thanh toán thành công</Text>
        <Text style={[styles.subtitle, { color: theme.textSecondary }]}>
          {`Bạn đã đăng ký ${cartItems.length} khóa học. Bắt đầu học ngay thôi!`}
        </Text>

        <Card style={styles.details}>
          <InfoRow label="Mã đơn hàng" value={orderId} />
          <InfoRow label="Phương thức" value={methodLabel} />
          <View style={[styles.divider, { backgroundColor: theme.borderStrong }]} />
          <InfoRow label="Tổng cộng" value={formatUsd(total)} emphasis />
        </Card>

        <View style={styles.actions}>
          <PrimaryButton
            label="Bắt đầu học"
            accessibilityLabel="Bắt đầu học"
            onPress={() => router.replace('/my-courses')}
          />
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="Về trang chủ"
            onPress={() => router.replace('/')}
            style={({ pressed }) => [styles.linkButton, { opacity: pressed ? 0.6 : 1 }]}>
            <Text style={[styles.link, { color: theme.primary }]}>Về trang chủ</Text>
          </Pressable>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
  content: {
    flexGrow: 1,
    paddingHorizontal: Spacing.three,
    justifyContent: 'center',
  },
  inner: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    alignItems: 'center',
    gap: Spacing.two,
  },
  badgeOuter: {
    width: 104,
    height: 104,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Spacing.three,
  },
  badgeInner: {
    width: 72,
    height: 72,
    borderRadius: Radius.pill,
    alignItems: 'center',
    justifyContent: 'center',
  },
  badgeGlyph: {
    color: '#ffffff',
    fontSize: 34,
    fontWeight: '700',
  },
  title: {
    fontSize: FontSizes.xxl,
    fontWeight: '700',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: FontSizes.md,
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: Spacing.three,
  },
  details: {
    width: '100%',
    gap: Spacing.half,
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    marginVertical: Spacing.two,
  },
  actions: {
    width: '100%',
    gap: Spacing.two,
    marginTop: Spacing.three,
  },
  linkButton: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: Spacing.three,
  },
  link: {
    fontSize: FontSizes.md,
    fontWeight: '700',
  },
});
