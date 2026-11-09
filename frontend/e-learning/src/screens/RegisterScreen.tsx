import { router } from '@/navigation/routerHook';
import { useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';

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
} from '@/data/auth';
import { useTheme } from '@/hooks/use-theme';
import type { RegisterFormErrors } from '@/types/auth';
import { isStrongEnoughPassword, isValidEmail } from '@/utils/validation';

export default function RegisterScreen() {
  const theme = useTheme();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(true);
  const [errors, setErrors] = useState<RegisterFormErrors>({});

  const handleSubmit = () => {
    const nextErrors: RegisterFormErrors = {};

    if (name.trim().length === 0) {
      nextErrors.name = authValidationMessages.nameRequired;
    }
    if (email.trim().length === 0) {
      nextErrors.email = authValidationMessages.emailRequired;
    } else if (!isValidEmail(email)) {
      nextErrors.email = authValidationMessages.emailInvalid;
    }
    if (!isStrongEnoughPassword(password)) {
      nextErrors.password = authValidationMessages.passwordTooShort;
    }
    if (!acceptedTerms) {
      nextErrors.acceptedTerms = authValidationMessages.termsRequired;
    }

    setErrors(nextErrors);

    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    router.replace('/');
  };

  const handleGoogleSignIn = () => {
    // Luồng đăng ký bằng Google sẽ được nối khi có API xác thực.
  };

  return (
    <AuthScreen testID="register-screen">
      <View style={styles.header}>
        <BackButton onPress={() => router.back()} />
      </View>

      <SurfaceCard>
        <ThemedText type="title" style={[styles.title, { color: theme.text }]}>
          {registerCopy.title}
        </ThemedText>
        <ThemedText style={[styles.subtitle, { color: theme.textSecondary }]}>
          {registerCopy.subtitle}
        </ThemedText>

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
            placeholder={authPlaceholders.passwordHint}
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

        <AppButton label={registerCopy.submit} onPress={handleSubmit} />

        <View style={styles.divider}>
          <OrDivider label={registerCopy.divider} />
        </View>

        <SocialButton label={registerCopy.google} onPress={handleGoogleSignIn} />
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
    gap: Spacing.two,
    marginBottom: Spacing.four,
    marginTop: Spacing.three,
  },
  termsText: {
    fontSize: 13,
    lineHeight: 20,
  },
  termsLink: {
    fontWeight: '600',
  },
  termsError: {
    fontSize: 12,
    lineHeight: 16,
  },
  divider: {
    marginVertical: Spacing.four,
  },
});
