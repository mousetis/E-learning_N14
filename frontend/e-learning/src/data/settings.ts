import type { SettingsGroup } from '@/types/settings';

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
