import React, { useState } from 'react';
import { StyleSheet, Text, View, Alert } from 'react-native';
import { useRouter } from '@/navigation/routerHook';
import { useTheme } from '@/hooks/use-theme';
import { Screen } from '@/components/ui/screen';
import { FormField } from '@/components/ui/form-field';
import { PrimaryButton } from '@/components/ui/primary-button';
import { BackButton } from '@/components/ui/back-button';
import { FontSizes, Spacing } from '@/constants/theme';
import { authService } from '@/services/authService';

export default function ForgotPasswordScreen() {
  const router = useRouter();
  const theme = useTheme();
  const [step, setStep] = useState<'request' | 'reset'>('request');
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('123456');
  const [newPassword, setNewPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleRequest = async () => {
    if (!email.trim()) {
      Alert.alert('Lỗi', 'Vui lòng nhập địa chỉ email của bạn');
      return;
    }
    try {
      setLoading(true);
      await authService.forgotPassword(email.trim());
      setStep('reset');
      Alert.alert(
        'Đã gửi mã xác nhận',
        'Vui lòng nhập mã OTP và mật khẩu mới. Bạn có thể sử dụng mã OTP mặc định: 123456.',
      );
    } catch (err: any) {
      Alert.alert('Lỗi', err?.message || 'Không thể gửi yêu cầu đặt lại mật khẩu');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async () => {
    if (!otp.trim()) {
      Alert.alert('Lỗi', 'Vui lòng nhập mã OTP');
      return;
    }
    if (newPassword.length < 8) {
      Alert.alert('Lỗi', 'Mật khẩu mới tối thiểu 8 ký tự');
      return;
    }

    try {
      setLoading(true);
      await authService.resetPassword(email.trim(), otp.trim(), newPassword);
      Alert.alert('Thành công', 'Mật khẩu đã được cập nhật thành công! Vui lòng đăng nhập lại.', [
        { text: 'Đăng nhập', onPress: () => router.replace('/login') },
      ]);
    } catch (err: any) {
      Alert.alert('Lỗi', err?.message || 'Không thể đặt lại mật khẩu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Screen>
      <BackButton onPress={() => (step === 'reset' ? setStep('request') : router.back())} />
      <View style={styles.header}>
        <Text style={[styles.title, { color: theme.text }]}>
          {step === 'request' ? 'Quên mật khẩu?' : 'Đặt lại mật khẩu'}
        </Text>
        <Text style={[styles.subtitle, { color: theme.textSecondary }]}>
          {step === 'request'
            ? 'Nhập email đăng ký tài khoản để nhận hướng dẫn khôi phục mật khẩu.'
            : `Nhập mã OTP 6 số và mật khẩu mới cho ${email}.`}
        </Text>
      </View>

      <View style={styles.form}>
        {step === 'request' ? (
          <>
            <FormField
              label="Email"
              placeholder="student@elearning.local"
              keyboardType="email-address"
              autoCapitalize="none"
              value={email}
              onChangeText={setEmail}
            />

            <PrimaryButton
              label={loading ? 'Đang gửi...' : 'Gửi yêu cầu'}
              onPress={handleRequest}
              disabled={loading}
              style={{ marginTop: Spacing.three }}
            />
          </>
        ) : (
          <>
            <FormField
              label="Mã OTP (6 chữ số)"
              placeholder="123456"
              keyboardType="number-pad"
              maxLength={6}
              value={otp}
              onChangeText={setOtp}
            />

            <FormField
              label="Mật khẩu mới"
              placeholder="Tối thiểu 8 ký tự (chữ và số)"
              secureTextEntry
              value={newPassword}
              onChangeText={setNewPassword}
            />

            <PrimaryButton
              label={loading ? 'Đang cập nhật...' : 'Xác nhận đổi mật khẩu'}
              onPress={handleResetPassword}
              disabled={loading}
              style={{ marginTop: Spacing.three }}
            />
          </>
        )}
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
