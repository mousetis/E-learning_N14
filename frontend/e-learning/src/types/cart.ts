export type CartItem = {
  id: string;
  courseId?: string;
  title: string;
  author: string;
  price: number;
  thumbnailColor: string;
  thumbnailLabel: string;
  image?: string;
};

export type CartItemDraft = Omit<CartItem, 'id'>;
