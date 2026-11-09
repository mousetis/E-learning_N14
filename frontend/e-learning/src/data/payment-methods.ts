import type { PaymentMethod } from '@/types/settings';

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
