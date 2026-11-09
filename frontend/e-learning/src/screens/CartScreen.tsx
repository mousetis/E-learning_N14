import { router } from '@/navigation/routerHook';
import { useMemo, useState } from 'react';
import {
  Keyboard,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Card } from '@/components/ui/card';
import { CartItemRow } from '@/components/ui/cart-item-row';
import { InfoRow } from '@/components/ui/info-row';
import { PrimaryButton } from '@/components/ui/primary-button';
import { ScreenHeader } from '@/components/ui/screen-header';
import { FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { cartDiscountLabel, cartDiscountRate, cartItems as initialCartItems } from '@/data/cart';
import { useTheme } from '@/hooks/use-theme';
import type { CartItem } from '@/types/cart';
import { formatUsd } from '@/utils/format';

export default function CartScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  const [items, setItems] = useState<CartItem[]>(initialCartItems);
  const [promoCode, setPromoCode] = useState('');
  const [promoApplied, setPromoApplied] = useState(true);

  const summary = useMemo(() => {
    const subtotal = items.reduce((sum, item) => sum + item.price, 0);
    const discount = promoApplied ? Math.round(subtotal * cartDiscountRate) : 0;
    return { subtotal, discount, total: subtotal - discount };
  }, [items, promoApplied]);

  const handleRemove = (id: string) => {
    setItems((current) => current.filter((item) => item.id !== id));
  };

  const handleApplyPromo = () => {
    if (promoCode.trim().length === 0) {
      return;
    }
    setPromoApplied(true);
    Keyboard.dismiss();
  };

  return (
    <View
      style={[
        styles.root,
        { backgroundColor: theme.background, paddingTop: insets.top + Spacing.two },
      ]}>
      <View style={styles.content}>
        <ScreenHeader title="Giỏ hàng" />
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled">
        <View style={styles.column}>
          {items.length === 0 ? (
            <Card>
              <Text style={[styles.emptyTitle, { color: theme.text }]}>Giỏ hàng trống</Text>
              <Text style={[styles.emptyText, { color: theme.textSecondary }]}>
                Khám phá thêm khóa học và thêm vào giỏ hàng của bạn.
              </Text>
            </Card>
          ) : (
            items.map((item) => <CartItemRow key={item.id} item={item} onRemove={handleRemove} />)
          )}

          <Card style={styles.promoCard}>
            <TextInput
              value={promoCode}
              onChangeText={setPromoCode}
              placeholder="Nhập mã giảm giá"
              placeholderTextColor={theme.textSecondary}
              accessibilityLabel="Nhập mã giảm giá"
              autoCapitalize="characters"
              returnKeyType="done"
              onSubmitEditing={handleApplyPromo}
              style={[styles.promoInput, { color: theme.text }]}
            />
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Áp dụng mã giảm giá"
              onPress={handleApplyPromo}
              style={({ pressed }) => [
                styles.promoButton,
                { backgroundColor: theme.primarySoft, opacity: pressed ? 0.7 : 1 },
              ]}>
              <Text style={[styles.promoButtonLabel, { color: theme.primary }]}>Áp dụng</Text>
            </Pressable>
          </Card>

          <Card>
            <InfoRow label="Tạm tính" value={formatUsd(summary.subtotal)} />
            <InfoRow label={cartDiscountLabel} value={formatUsd(-summary.discount)} />
            <View style={[styles.divider, { backgroundColor: theme.borderStrong }]} />
            <InfoRow label="Tổng cộng" value={formatUsd(summary.total)} emphasis />
          </Card>
        </View>
      </ScrollView>

      <View
        style={[
          styles.footer,
          {
            paddingBottom: insets.bottom + Spacing.three,
            backgroundColor: theme.background,
            borderTopColor: theme.border,
          },
        ]}>
        <View style={styles.footerInner}>
          <PrimaryButton
            label={`Thanh toán · ${formatUsd(summary.total)}`}
            accessibilityLabel={`Thanh toán ${formatUsd(summary.total)}`}
            disabled={items.length === 0}
            onPress={() => router.push('/checkout')}
          />
        </View>
      </View>
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
    paddingBottom: Spacing.five,
  },
  column: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    gap: Spacing.three,
  },
  emptyTitle: {
    fontSize: FontSizes.lg,
    fontWeight: '700',
    marginBottom: Spacing.one,
  },
  emptyText: {
    fontSize: FontSizes.sm,
  },
  promoCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  promoInput: {
    flex: 1,
    fontSize: FontSizes.md,
    paddingVertical: Spacing.one,
  },
  promoButton: {
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    borderRadius: Radius.md,
  },
  promoButtonLabel: {
    fontSize: FontSizes.sm,
    fontWeight: '700',
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    marginVertical: Spacing.two,
  },
  footer: {
    borderTopWidth: StyleSheet.hairlineWidth,
    paddingTop: Spacing.three,
    paddingHorizontal: Spacing.three,
  },
  footerInner: {
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
  },
});
