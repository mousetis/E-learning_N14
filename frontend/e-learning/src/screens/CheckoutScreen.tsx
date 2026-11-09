import { router } from '@/navigation/routerHook';
import { useMemo, useState } from 'react';
import { ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Card } from '@/components/ui/card';
import { InfoRow } from '@/components/ui/info-row';
import { PaymentMethodRow } from '@/components/ui/payment-method-row';
import { PrimaryButton } from '@/components/ui/primary-button';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionTitle } from '@/components/ui/section-title';
import { FontSizes, MaxContentWidth, Radius, Spacing } from '@/constants/theme';
import { cartDiscountLabel, cartDiscountRate, cartItems, cartOrderId } from '@/data/cart';
import { defaultPaymentMethodId, paymentMethods } from '@/data/payment-methods';
import { useTheme } from '@/hooks/use-theme';
import { formatCardNumber, formatExpiry, formatUsd, digitsOnly } from '@/utils/format';

const DEFAULT_METHOD_LABEL = 'Thẻ ngân hàng';

export default function CheckoutScreen() {
  const theme = useTheme();
  const insets = useSafeAreaInsets();

  const [selectedMethodId, setSelectedMethodId] = useState<string>(defaultPaymentMethodId);
  const [cardNumber, setCardNumber] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');

  const totals = useMemo(() => {
    const subtotal = cartItems.reduce((sum, item) => sum + item.price, 0);
    const discount = Math.round(subtotal * cartDiscountRate);
    return { subtotal, discount, total: subtotal - discount };
  }, []);

  const isCardSelected = selectedMethodId === defaultPaymentMethodId;
  const selectedMethod = paymentMethods.find((method) => method.id === selectedMethodId);
  const methodLabel = selectedMethod ? selectedMethod.label : DEFAULT_METHOD_LABEL;

  const handlePay = () => {
    router.replace({
      pathname: '/checkout-success',
      params: {
        orderId: cartOrderId,
        method: methodLabel,
        total: String(totals.total),
      },
    });
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

              <View style={styles.fieldRow}>
                <View style={[styles.field, styles.fieldHalf]}>
                  <Text style={[styles.fieldLabel, { color: theme.text }]}>Hết hạn</Text>
                  <TextInput
                    value={expiry}
                    onChangeText={(value) => setExpiry(formatExpiry(value))}
                    placeholder="MM/YY"
                    placeholderTextColor={theme.textSecondary}
                    keyboardType="number-pad"
                    maxLength={5}
                    accessibilityLabel="Ngày hết hạn"
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

                <View style={[styles.field, styles.fieldHalf]}>
                  <Text style={[styles.fieldLabel, { color: theme.text }]}>CVV</Text>
                  <TextInput
                    value={cvv}
                    onChangeText={(value) => setCvv(digitsOnly(value, 4))}
                    placeholder="•••"
                    placeholderTextColor={theme.textSecondary}
                    keyboardType="number-pad"
                    secureTextEntry
                    maxLength={4}
                    accessibilityLabel="CVV"
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

          <Card>
            <InfoRow label={`${cartItems.length} khóa học`} value={formatUsd(totals.subtotal)} />
            <InfoRow label={cartDiscountLabel} value={formatUsd(-totals.discount)} />
            <View style={[styles.divider, { backgroundColor: theme.borderStrong }]} />
            <InfoRow label="Tổng cộng" value={formatUsd(totals.total)} emphasis />
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
            label={`Thanh toán ${formatUsd(totals.total)}`}
            accessibilityLabel={`Thanh toán ${formatUsd(totals.total)}`}
            onPress={handlePay}
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
    gap: Spacing.two,
  },
  methods: {
    gap: Spacing.two,
    marginBottom: Spacing.two,
  },
  form: {
    gap: Spacing.three,
    marginBottom: Spacing.two,
  },
  field: {
    gap: Spacing.one,
  },
  fieldRow: {
    flexDirection: 'row',
    gap: Spacing.three,
  },
  fieldHalf: {
    flex: 1,
  },
  fieldLabel: {
    fontSize: FontSizes.sm,
    fontWeight: '600',
  },
  input: {
    height: 48,
    borderRadius: Radius.md,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    fontSize: FontSizes.md,
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
