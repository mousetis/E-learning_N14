import { router } from '@/navigation/routerHook';
import { useState } from 'react';
import { Alert, StyleSheet, Text, View } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { AppButton } from '@/components/ui/app-button';
import { AuthScreen } from '@/components/ui/auth-screen';
import { BackButton } from '@/components/ui/back-button';
import { Checkbox } from '@/components/ui/checkbox';
import { FooterLink } from '@/components/ui/footer-link';
import { FormField } from '@/components/ui/form-field';
import { OrDivider } from '@/components/ui/or-divider';
import { SocialButton } from '@/components/ui/social-button';
import { SurfaceCard } from '@/components/ui/surface-card';
import { Spacing } from '@/constants/theme';
import {
  authPlaceholders,
  authValidationMessages,
  passwordVisibilityLabels,
  registerCopy,
  termsCopy,
} from '@/constants/auth';
import { useTheme } from '@/hooks/use-theme';
import type { RegisterFormErrors } from '@/types/auth';
import { isValidEmail } from '@/utils/validation';
import { authService } from '@/services/authService';
import { useAppStore } from '@/store';

export default function RegisterScreen() {
  const theme = useTheme();
  const { setUser, refreshUserData } = useAppStore();
  const [step, setStep] = useState<'form' | 'otp'>('form');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otp, setOtp] = useState('123456');
  const [showPassword, setShowPassword] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(true);
  const [errors, setErrors] = useState<RegisterFormErrors>({});
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    const nextErrors: RegisterFormErrors = {};

    if (name.trim().length === 0) {
      nextErrors.name = authValidationMessages.nameRequired;
    }
    if (email.trim().length === 0) {
      nextErrors.email = authValidationMessages.emailRequired;
    } else if (!isValidEmail(email)) {
      nextErrors.email = authValidationMessages.emailInvalid;
    }
    if (password.length < 8) {
      nextErrors.password = 'Mật khẩu tối thiểu 8 ký tự, gồm cả chữ và số';
    }
    if (!acceptedTerms) {
      nextErrors.acceptedTerms = authValidationMessages.termsRequired;
    }

    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    try {
      setLoading(true);
      await authService.register(name.trim(), email.trim(), password);
      setStep('otp');
      Alert.alert(
        'Đăng ký thành công',
        'Mã OTP xác thực đã được gửi. Bạn có thể sử dụng mã mặc định 123456 để xác nhận ngay.',
      );
    } catch (err: any) {
      const msg = err?.message || 'Đăng ký không thành công, vui lòng thử lại';
      Alert.alert('Lỗi đăng ký', msg);
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (!otp.trim()) {
      Alert.alert('Lỗi', 'Vui lòng nhập mã OTP 6 số');
      return;
    }

    try {
      setLoading(true);
      await authService.verifyOtp(email.trim(), otp.trim());
      // Log in automatically
      const user = await authService.login(email.trim(), password);
      setUser(user);
      await refreshUserData();
      Alert.alert('Chào mừng', 'Tài khoản của bạn đã được kích hoạt thành công!', [
        { text: 'Bắt đầu học ngay', onPress: () => router.replace('/') },
      ]);
    } catch (err: any) {
      const msg = err?.message || 'Mã OTP không hợp lệ hoặc đã hết hạn';
      Alert.alert('Xác thực thất bại', msg);
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    try {
      await authService.resendOtp(email.trim(), 'REGISTER');
      Alert.alert('Thông báo', 'Đã gửi lại mã OTP');
    } catch (err: any) {
      Alert.alert('Lỗi', err?.message || 'Không thể gửi lại OTP lúc này');
    }
  };

  return (
    <AuthScreen testID="register-screen">
      <View style={styles.header}>
        <BackButton onPress={() => (step === 'otp' ? setStep('form') : router.back())} />
      </View>

      <SurfaceCard>
        <ThemedText type="title" style={[styles.title, { color: theme.text }]}>
          {step === 'form' ? registerCopy.title : 'Xác thực OTP'}
        </ThemedText>
        <ThemedText style={[styles.subtitle, { color: theme.textSecondary }]}>
          {step === 'form'
            ? registerCopy.subtitle
            : `Nhập mã 6 số đã gửi tới ${email}. (Mặc định: 123456)`}
        </ThemedText>

        {step === 'form' ? (
          <>
            <View style={styles.fields}>
              <FormField
                label={registerCopy.nameLabel}
                value={name}
                onChangeText={(value) => {
                  setName(value);
                  setErrors((current) => ({ ...current, name: undefined }));
                }}
                placeholder={authPlaceholders.name}
                autoCapitalize="words"
                autoComplete="name"
                textContentType="name"
                returnKeyType="next"
                error={errors.name}
              />
              <FormField
                label={registerCopy.emailLabel}
                value={email}
                onChangeText={(value) => {
                  setEmail(value);
                  setErrors((current) => ({ ...current, email: undefined }));
                }}
                placeholder={authPlaceholders.email}
                keyboardType="email-address"
                autoCapitalize="none"
                autoComplete="email"
                textContentType="emailAddress"
                returnKeyType="next"
                error={errors.email}
              />
              <FormField
                label={registerCopy.passwordLabel}
                value={password}
                onChangeText={(value) => {
                  setPassword(value);
                  setErrors((current) => ({ ...current, password: undefined }));
                }}
                placeholder="Ví dụ: Pass123456"
                secureTextEntry={!showPassword}
                rightLabel={showPassword ? passwordVisibilityLabels.hide : passwordVisibilityLabels.show}
                rightLabelAccessibilityLabel={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                onRightLabelPress={() => setShowPassword((current) => !current)}
                autoComplete="new-password"
                textContentType="newPassword"
                returnKeyType="done"
                onSubmitEditing={handleSubmit}
                error={errors.password}
              />
            </View>

            <View style={styles.terms}>
              <Checkbox
                checked={acceptedTerms}
                onCheckedChange={(checked) => {
                  setAcceptedTerms(checked);
                  setErrors((current) => ({ ...current, acceptedTerms: undefined }));
                }}
                accessibilityLabel="Tôi đồng ý với Điều khoản và Chính sách"
              >
                <Text style={[styles.termsText, { color: theme.textSecondary }]}>
                  {termsCopy.prefix}{' '}
                  <Text style={[styles.termsLink, { color: theme.primary }]}>{termsCopy.terms}</Text>{' '}
                  {termsCopy.conjunction}{' '}
                  <Text style={[styles.termsLink, { color: theme.primary }]}>{termsCopy.privacy}</Text>
                </Text>
              </Checkbox>
              {errors.acceptedTerms ? (
                <Text style={[styles.termsError, { color: theme.danger }]} accessibilityLiveRegion="polite">
                  {errors.acceptedTerms}
                </Text>
              ) : null}
            </View>

            <AppButton
              label={loading ? 'Đang đăng ký...' : registerCopy.submit}
              onPress={handleSubmit}
              disabled={loading}
            />

            <View style={styles.divider}>
              <OrDivider label={registerCopy.divider} />
            </View>

            <SocialButton
              label="Đăng ký nhanh bằng tài khoản thử nghiệm"
              onPress={() => {
                setName('Sinh Viên Mới');
                setEmail(`student${Date.now()}@elearning.local`);
                setPassword('Password123');
              }}
            />
          </>
        ) : (
          <View style={styles.fields}>
            <FormField
              label="Mã xác thực OTP (6 chữ số)"
              value={otp}
              onChangeText={setOtp}
              placeholder="123456"
              keyboardType="number-pad"
              maxLength={6}
              returnKeyType="done"
              onSubmitEditing={handleVerifyOtp}
            />

            <AppButton
              label={loading ? 'Đang xác thực...' : 'Xác thực & Bắt đầu học'}
              onPress={handleVerifyOtp}
              disabled={loading}
            />

            <Text
              onPress={handleResendOtp}
              style={[styles.termsLink, { textAlign: 'center', marginTop: Spacing.two }]}
            >
              Chưa nhận được mã? Gửi lại OTP
            </Text>
          </View>
        )}
      </SurfaceCard>

      <FooterLink
        question={registerCopy.footerQuestion}
        action={registerCopy.footerAction}
        onPress={() => router.replace('/login')}
      />
    </AuthScreen>
  );
}

const styles = StyleSheet.create({
  header: {
    alignItems: 'flex-start',
    marginBottom: Spacing.three,
  },
  title: {
    fontSize: 26,
    fontWeight: '700',
    letterSpacing: -0.4,
    lineHeight: 34,
  },
  subtitle: {
    fontSize: 14,
    lineHeight: 20,
    marginBottom: Spacing.four,
    marginTop: Spacing.two,
  },
  fields: {
    gap: Spacing.three,
  },
  terms: {
    marginVertical: Spacing.three,
  },
  termsText: {
    fontSize: 12,
    lineHeight: 18,
    flexShrink: 1,
  },
  termsLink: {
    fontWeight: '600',
  },
  termsError: {
    fontSize: 12,
    marginTop: Spacing.one,
  },
  divider: {
    marginVertical: Spacing.four,
  },
});
