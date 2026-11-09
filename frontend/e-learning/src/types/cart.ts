export type CartItem = {
  id: string;
  title: string;
  author: string;
  price: number;
  thumbnailColor: string;
  thumbnailLabel: string;
};

export type CartItemDraft = Omit<CartItem, 'id'>;
