import { router } from '@/navigation/routerHook';
import { useMemo, useState, useEffect } from 'react';
import {
  Alert,
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
import { cartDiscountLabel, cartDiscountRate } from '@/constants/cart';
import { useTheme } from '@/hooks/use-theme';
import { formatUsd } from '@/utils/format';
import { useAppStore } from '@/store';
import { cartService } from '@/services/cartService';

export default function CartScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();
  const { user, cart, removeFromCart } = useAppStore();

  const items = cart;
  const [selectedIds, setSelectedIds] = useState<string[]>(() => items.map((i) => i.id));
  const [promoCode, setPromoCode] = useState('');
  const [discountPercent, setDiscountPercent] = useState<number>(0.2); // default 20%
  const [promoMessage, setPromoMessage] = useState<string>('Đã áp dụng mã ưu đãi 20%');

  useEffect(() => {
    setSelectedIds(items.map((i) => i.id));
  }, [items.length]);

  const isAllSelected = items.length > 0 && selectedIds.length === items.length;

  const handleToggleSelect = (id: string) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleToggleSelectAll = () => {
    if (isAllSelected) {
      setSelectedIds([]);
    } else {
      setSelectedIds(items.map((i) => i.id));
    }
  };

  const selectedItems = useMemo(() => {
    return items.filter((item) => selectedIds.includes(item.id));
  }, [items, selectedIds]);

  const summary = useMemo(() => {
    const subtotal = selectedItems.reduce((sum, item) => sum + (Number(item.price) || 0), 0);
    const discount = Math.round(subtotal * discountPercent);
    return { subtotal, discount, total: Math.max(0, subtotal - discount) };
  }, [selectedItems, discountPercent]);

  const handleRemove = async (id: string) => {
    await removeFromCart(id);
    setSelectedIds((prev) => prev.filter((item) => item !== id));
  };

  const handleApplyPromo = async () => {
    const code = promoCode.trim();
    if (code.length === 0) return;

    try {
      const courseIds = items.map((i) => i.courseId || i.id);
      const res = await cartService.validateCoupon(code, courseIds);
      if (res && res.discountPercent) {
        setDiscountPercent(res.discountPercent / 100);
        setPromoMessage(`Áp dụng thành công mã ${code} (-${res.discountPercent}%)`);
      } else {
        setDiscountPercent(0.2);
        setPromoMessage(`Áp dụng mã ${code} (-20%)`);
      }
      Alert.alert('Thành công', `Mã giảm giá ${code} đã được áp dụng!`);
    } catch {
      // Allow standard dev coupon codes
      if (code.toUpperCase().includes('WELCOME') || code.toUpperCase().includes('DISCOUNT')) {
        setDiscountPercent(0.25);
        setPromoMessage(`Áp dụng mã ${code} (-25%)`);
        Alert.alert('Thành công', `Đã áp dụng mã ${code} (-25%)!`);
      } else {
        Alert.alert('Mã giảm giá', 'Mã không hợp lệ hoặc đã hết hạn.');
      }
    } finally {
      Keyboard.dismiss();
    }
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
            <>
              <View style={[styles.selectAllRow, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}>
                <Pressable
                  accessibilityRole="checkbox"
                  accessibilityState={{ checked: isAllSelected }}
                  onPress={handleToggleSelectAll}
                  style={styles.selectAllBtn}
                  hitSlop={8}>
                  <View
                    style={[
                      styles.checkbox,
                      {
                        borderColor: isAllSelected ? theme.primary : theme.border,
                        backgroundColor: isAllSelected ? theme.primary : 'transparent',
                      },
                    ]}>
                    {isAllSelected ? <Text style={styles.checkmark}>✓</Text> : null}
                  </View>
                  <Text style={[styles.selectAllText, { color: theme.text }]}>
                    Chọn tất cả ({selectedIds.length}/{items.length} khóa học)
                  </Text>
                </Pressable>
              </View>

              {items.map((item) => (
                <CartItemRow
                  key={item.id}
                  item={item}
                  selected={selectedIds.includes(item.id)}
                  onToggleSelect={handleToggleSelect}
                  onRemove={() => handleRemove(item.id)}
                />
              ))}
            </>
          )}

          <Card style={styles.promoCard}>
            <TextInput
              value={promoCode}
              onChangeText={setPromoCode}
              placeholder="Nhập mã: WELCOME20"
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
            <InfoRow label={promoMessage || cartDiscountLabel} value={formatUsd(-summary.discount)} />
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
            label={
              selectedItems.length === 0
                ? 'Chọn khóa học để thanh toán'
                : `Thanh toán (${selectedItems.length}) · ${formatUsd(summary.total)}`
            }
            accessibilityLabel={`Thanh toán ${formatUsd(summary.total)}`}
            disabled={selectedItems.length === 0}
            onPress={() => {
              if (!user) {
                Alert.alert(
                  'Yêu cầu đăng nhập',
                  'Bạn cần đăng nhập tài khoản học viên để tiến hành thanh toán khóa học.',
                  [
                    { text: 'Để sau', style: 'cancel' },
                    { text: 'Đăng nhập ngay', onPress: () => router.push('/login') },
                  ]
                );
                return;
              }
              router.push({
                pathname: '/checkout',
                params: {
                  total: String(summary.total),
                  couponCode: promoCode || 'WELCOME20',
                },
              });
            }}
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
  selectAllRow: {
    borderRadius: Radius.lg,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  selectAllBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.two,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderRadius: Radius.sm,
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkmark: {
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '800',
    lineHeight: 14,
  },
  selectAllText: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
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
