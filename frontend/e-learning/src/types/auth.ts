export type LoginFormValues = {
  email: string;
  password: string;
};

export type RegisterFormValues = {
  name: string;
  email: string;
  password: string;
  acceptedTerms: boolean;
};

export type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>;

export type RegisterFormErrors = Partial<Record<keyof RegisterFormValues, string>>;
