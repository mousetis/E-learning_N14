import type { PaymentMethod, SettingsGroup } from '@/types/settings';

export const accountSettings: SettingsGroup = {
  id: 'account',
  items: [
    { id: 'personal-info', label: 'Thông tin cá nhân' },
    { id: 'payment-methods', label: 'Phương thức thanh toán' },
    { id: 'order-history', label: 'Lịch sử đơn hàng' },
    { id: 'notifications', label: 'Thông báo' },
  ],
};

export const supportSettings: SettingsGroup = {
  id: 'support',
  items: [
    { id: 'help', label: 'Trợ giúp & hỗ trợ' },
    { id: 'logout', label: 'Đăng xuất', tone: 'danger' },
  ],
};

export const defaultLanguageLabel = 'Tiếng Việt';

export const paymentMethods: PaymentMethod[] = [
  {
    id: 'card',
    label: 'Thẻ ngân hàng',
    icon: '💳',
    iconBackground: '#b8f1e0',
  },
  {
    id: 'momo',
    label: 'Ví MoMo',
    icon: '📱',
    iconBackground: '#c7c2fe',
  },
  {
    id: 'zalopay',
    label: 'ZaloPay',
    icon: '💰',
    iconBackground: '#c7c2fe',
  },
];

export const defaultPaymentMethodId = 'card';
