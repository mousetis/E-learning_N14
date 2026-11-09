export type SettingsItemTone = 'default' | 'danger';

export type SettingsItem = {
  id: string;
  label: string;
  tone?: SettingsItemTone;
};

export type SettingsGroup = {
  id: string;
  items: SettingsItem[];
};

export type PaymentMethod = {
  id: string;
  label: string;
  icon: string;
  iconBackground: string;
};

export type UserProfile = {
  name: string;
  email: string;
  initials: string;
  avatarColor: string;
};
