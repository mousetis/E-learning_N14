import { router } from '@/navigation/routerHook';
import { useMemo, useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Card } from '@/components/ui/card';
import { InfoRow } from '@/components/ui/info-row';
import { PaymentMethodRow } from '@/components/ui/payment-method-row';
import { PrimaryButton } from '@/components/ui/primary-button';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionTitle } from '@/components/ui/section-title';
import { FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { cartDiscountLabel, cartDiscountRate } from '@/constants/cart';
import { defaultPaymentMethodId, paymentMethods } from '@/constants/settings';
import { useTheme } from '@/hooks/use-theme';
import { formatCardNumber, formatExpiry, formatUsd } from '@/utils/format';
import { useAppStore } from '@/store';
import { cartService } from '@/services/cartService';

const DEFAULT_METHOD_LABEL = 'Thẻ ngân hàng';

export default function CheckoutScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();
  const { cart, clearCart, refreshUserData } = useAppStore();

  const [selectedMethodId, setSelectedMethodId] = useState<string>(defaultPaymentMethodId);
  const [cardNumber, setCardNumber] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');
  const [loading, setLoading] = useState(false);
  const cartOrderId = '#ELN-20268';
  const activeItems = cart;

  const totals = useMemo(() => {
    const subtotal = activeItems.reduce((sum, item) => sum + (Number(item.price) || 0), 0);
    const discount = Math.round(subtotal * cartDiscountRate);
    return { subtotal, discount, total: Math.max(0, subtotal - discount) };
  }, [activeItems]);

  const isCardSelected = selectedMethodId === defaultPaymentMethodId;
  const selectedMethod = paymentMethods.find((method) => method.id === selectedMethodId);
  const methodLabel = selectedMethod ? selectedMethod.label : DEFAULT_METHOD_LABEL;

  const handlePay = async () => {
    try {
      setLoading(true);
      const courseIds = activeItems.map((item) => item.courseId || item.id);
      
      let orderCode = `ORD${Date.now().toString().slice(-8)}`;
      try {
        const orderRes = await cartService.checkout({
          courseIds,
          method: 'MOCK',
          couponCode: 'WELCOME20',
        });
        if (orderRes?.orderCode) {
          orderCode = orderRes.orderCode;
        }
      } catch (e) {
        console.warn('Backend order checkout error, proceeding with local success flow:', e);
      }

      clearCart();
      await refreshUserData();

      router.replace({
        pathname: '/checkout-success',
        params: {
          orderId: orderCode,
          method: methodLabel,
          total: String(totals.total),
        },
      });
    } catch (err: any) {
      Alert.alert('Lỗi thanh toán', err?.message || 'Không thể hoàn tất thanh toán');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View
      style={[
        styles.root,
        { backgroundColor: theme.background, paddingTop: insets.top + Spacing.two },
      ]}>
      <View style={styles.content}>
        <ScreenHeader title="Thanh toán" />
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled">
        <View style={styles.column}>
          <SectionTitle>Phương thức thanh toán</SectionTitle>

          <View style={styles.methods}>
            {paymentMethods.map((method) => (
              <PaymentMethodRow
                key={method.id}
                method={method}
                selected={method.id === selectedMethodId}
                onSelect={setSelectedMethodId}
              />
            ))}
          </View>

          {isCardSelected ? (
            <View style={styles.form}>
              <View style={styles.field}>
                <Text style={[styles.fieldLabel, { color: theme.text }]}>Số thẻ</Text>
                <TextInput
                  value={cardNumber}
                  onChangeText={(value) => setCardNumber(formatCardNumber(value))}
                  placeholder="1234 5678 9012 3456"
                  placeholderTextColor={theme.textSecondary}
                  keyboardType="number-pad"
                  maxLength={19}
                  accessibilityLabel="Số thẻ"
                  style={[
                    styles.input,
                    {
                      color: theme.text,
                      backgroundColor: theme.backgroundElement,
                      borderColor: theme.border,
                    },
                  ]}
                />
              </View>

              <View style={styles.rowFields}>
                <View style={[styles.field, styles.flexField]}>
                  <Text style={[styles.fieldLabel, { color: theme.text }]}>Hết hạn</Text>
                  <TextInput
                    value={expiry}
                    onChangeText={(value) => setExpiry(formatExpiry(value))}
                    placeholder="MM/YY"
                    placeholderTextColor={theme.textSecondary}
                    keyboardType="number-pad"
                    maxLength={5}
                    accessibilityLabel="Ngày hết hạn thẻ"
                    style={[
                      styles.input,
                      {
                        color: theme.text,
                        backgroundColor: theme.backgroundElement,
                        borderColor: theme.border,
                      },
                    ]}
                  />
                </View>

                <View style={[styles.field, styles.flexField]}>
                  <Text style={[styles.fieldLabel, { color: theme.text }]}>CVV</Text>
                  <TextInput
                    value={cvv}
                    onChangeText={setCvv}
                    placeholder="123"
                    placeholderTextColor={theme.textSecondary}
                    keyboardType="number-pad"
                    maxLength={4}
                    secureTextEntry
                    accessibilityLabel="Mã bảo mật CVV"
                    style={[
                      styles.input,
                      {
                        color: theme.text,
                        backgroundColor: theme.backgroundElement,
                        borderColor: theme.border,
                      },
                    ]}
                  />
                </View>
              </View>
            </View>
          ) : null}

          <SectionTitle>Chi tiết thanh toán</SectionTitle>

          <Card>
            <InfoRow label="Tạm tính" value={formatUsd(totals.subtotal)} />
            <InfoRow label={cartDiscountLabel} value={formatUsd(-totals.discount)} />
            <View style={[styles.divider, { backgroundColor: theme.borderStrong }]} />
            <InfoRow label="Tổng thanh toán" value={formatUsd(totals.total)} emphasis />
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
            label={loading ? 'Đang xử lý...' : `Xác nhận thanh toán · ${formatUsd(totals.total)}`}
            accessibilityLabel={`Xác nhận thanh toán ${formatUsd(totals.total)}`}
            onPress={handlePay}
            disabled={loading}
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
  methods: {
    gap: Spacing.two,
  },
  form: {
    gap: Spacing.two,
  },
  field: {
    gap: Spacing.one,
  },
  fieldLabel: {
    fontSize: FontSizes.xs,
    fontWeight: '600',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  input: {
    fontSize: FontSizes.md,
    borderWidth: 1,
    borderRadius: Radius.md,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  rowFields: {
    flexDirection: 'row',
    gap: Spacing.two,
  },
  flexField: {
    flex: 1,
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
