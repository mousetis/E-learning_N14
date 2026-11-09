import { router } from '@/navigation/routerHook';
import { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { AppButton } from '@/components/ui/app-button';
import { AuthScreen } from '@/components/ui/auth-screen';
import { BackButton } from '@/components/ui/back-button';
import { FooterLink } from '@/components/ui/footer-link';
import { FormField } from '@/components/ui/form-field';
import { OrDivider } from '@/components/ui/or-divider';
import { SocialButton } from '@/components/ui/social-button';
import { SurfaceCard } from '@/components/ui/surface-card';
import { Spacing } from '@/constants/theme';
import {
  authPlaceholders,
  authValidationMessages,
  loginCopy,
  passwordVisibilityLabels,
} from '@/data/auth';
import { useTheme } from '@/hooks/use-theme';
import { isValidEmail } from '@/utils/validation';

export default function LoginScreen() {
  const theme = useTheme();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [emailError, setEmailError] = useState<string | undefined>(undefined);
  const [passwordError, setPasswordError] = useState<string | undefined>(undefined);

  const handleSubmit = () => {
    const nextEmailError =
      email.trim().length === 0
        ? authValidationMessages.emailRequired
        : isValidEmail(email)
          ? undefined
          : authValidationMessages.emailInvalid;
    const nextPasswordError =
      password.length === 0 ? authValidationMessages.passwordRequired : undefined;

    setEmailError(nextEmailError);
    setPasswordError(nextPasswordError);

    if (nextEmailError || nextPasswordError) {
      return;
    }

    router.replace('/');
  };

  const handleForgotPassword = () => {
    // Màn hình khôi phục mật khẩu chưa nằm trong phạm vi thiết kế này.
  };

  const handleGoogleSignIn = () => {
    // Luồng đăng nhập Google sẽ được nối khi có API xác thực.
  };

  return (
    <AuthScreen testID="login-screen">
      <View style={styles.header}>
        <BackButton onPress={() => router.back()} />
      </View>

      <SurfaceCard>
        <ThemedText type="title" style={[styles.title, { color: theme.text }]}>
          {loginCopy.title}
        </ThemedText>
        <ThemedText style={[styles.subtitle, { color: theme.textSecondary }]}>
          {loginCopy.subtitle}
        </ThemedText>

        <View style={styles.fields}>
          <FormField
            label={loginCopy.emailLabel}
            value={email}
            onChangeText={(value) => {
              setEmail(value);
              setEmailError(undefined);
            }}
            placeholder={authPlaceholders.email}
            keyboardType="email-address"
            autoCapitalize="none"
            autoComplete="email"
            textContentType="emailAddress"
            returnKeyType="next"
            error={emailError}
          />
          <FormField
            label={loginCopy.passwordLabel}
            value={password}
            onChangeText={(value) => {
              setPassword(value);
              setPasswordError(undefined);
            }}
            placeholder={authPlaceholders.password}
            secureTextEntry={!showPassword}
            rightLabel={showPassword ? passwordVisibilityLabels.hide : passwordVisibilityLabels.show}
            rightLabelAccessibilityLabel={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
            onRightLabelPress={() => setShowPassword((current) => !current)}
            autoComplete="password"
            textContentType="password"
            returnKeyType="go"
            onSubmitEditing={handleSubmit}
            error={passwordError}
          />
        </View>

        <Pressable
          onPress={handleForgotPassword}
          accessibilityRole="link"
          accessibilityLabel={loginCopy.forgotPassword}
          hitSlop={Spacing.two}
          style={styles.forgot}
        >
          <Text style={[styles.forgotLabel, { color: theme.primary }]}>
            {loginCopy.forgotPassword}
          </Text>
        </Pressable>

        <AppButton label={loginCopy.submit} onPress={handleSubmit} />

        <View style={styles.divider}>
          <OrDivider label={loginCopy.divider} />
        </View>

        <SocialButton label={loginCopy.google} onPress={handleGoogleSignIn} />
      </SurfaceCard>

      <FooterLink
        question={loginCopy.footerQuestion}
        action={loginCopy.footerAction}
        onPress={() => router.replace('/register')}
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
  forgot: {
    alignSelf: 'flex-end',
    marginBottom: Spacing.three,
    marginTop: Spacing.two,
  },
  forgotLabel: {
    fontSize: 13,
    fontWeight: '600',
  },
  divider: {
    marginVertical: Spacing.four,
  },
});
