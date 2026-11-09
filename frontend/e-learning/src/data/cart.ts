import type { CartItem } from '@/types/cart';

export const cartItems: CartItem[] = [
  {
    id: 'ux-foundation',
    title: 'UX Foundation',
    author: 'Sara Weise',
    price: 51,
    thumbnailColor: '#c7c2fe',
    thumbnailLabel: 'UX',
  },
  {
    id: 'website-design',
    title: 'Website Design',
    author: 'Ramono Wultschner',
    price: 59,
    thumbnailColor: '#ffd9a8',
    thumbnailLabel: 'WD',
  },
  {
    id: 'product-design',
    title: 'Product Design',
    author: 'Dennis Sweeney',
    price: 90,
    thumbnailColor: '#bce0fd',
    thumbnailLabel: 'PD',
  },
];

export const cartDiscountRate = 0.2;

export const cartDiscountLabel = 'Giảm 20%';

export const cartOrderId = '#ELN-20481';
