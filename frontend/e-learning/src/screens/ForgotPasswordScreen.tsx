import React, { useState } from 'react';
import { StyleSheet, Text, View, Alert } from 'react-native';
import { useRouter } from '@/navigation/routerHook';
import { useTheme } from '@/hooks/use-theme';
import { Screen } from '@/components/ui/screen';
import { FormField } from '@/components/ui/form-field';
import { PrimaryButton } from '@/components/ui/primary-button';
import { BackButton } from '@/components/ui/back-button';
import { FontSizes, Spacing } from '@/constants/theme';

export default function ForgotPasswordScreen() {
  const router = useRouter();
  const theme = useTheme();
  const [email, setEmail] = useState('');

  const handleReset = () => {
    if (!email) {
      Alert.alert('Lỗi', 'Vui lòng nhập địa chỉ email của bạn');
      return;
    }
    Alert.alert('Thành công', 'Link đặt lại mật khẩu đã được gửi đến email của bạn!', [
      { text: 'OK', onPress: () => router.push('/login') },
    ]);
  };

  return (
    <Screen>
      <BackButton onPress={() => router.back()} />
      <View style={styles.header}>
        <Text style={[styles.title, { color: theme.text }]}>Quên mật khẩu?</Text>
        <Text style={[styles.subtitle, { color: theme.textSecondary }]}>
          Nhập email đăng ký tài khoản để nhận hướng dẫn khôi phục mật khẩu.
        </Text>
      </View>

      <View style={styles.form}>
        <FormField
          label="Email"
          placeholder="name@example.com"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        <PrimaryButton label="Gửi yêu cầu" onPress={handleReset} style={{ marginTop: Spacing.three }} />
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    marginVertical: Spacing.four,
    gap: Spacing.one,
  },
  title: {
    fontSize: FontSizes.xxl,
    fontWeight: '800',
  },
  subtitle: {
    fontSize: FontSizes.sm,
    lineHeight: 20,
  },
  form: {
    gap: Spacing.three,
  },
});
