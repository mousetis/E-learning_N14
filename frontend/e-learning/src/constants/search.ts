import type { RatingOption, SortOption } from '@/types/course';

export const sortOptions: SortOption[] = [
  { id: 'popular', label: 'Phổ biến' },
  { id: 'newest', label: 'Mới nhất' },
  { id: 'rating', label: 'Đánh giá cao' },
  { id: 'price_asc', label: 'Giá thấp → cao' },
  { id: 'price_desc', label: 'Giá cao → thấp' },
];

export const ratingOptions: RatingOption[] = [
  { id: '4.5', stars: 4.5, label: '★ 4.5+' },
  { id: '4.0', stars: 4.0, label: '★ 4.0+' },
  { id: '3.0', stars: 3.0, label: '★ 3.0+' },
];

export const priceRange = { minimum: 0, maximum: 5000000 };
