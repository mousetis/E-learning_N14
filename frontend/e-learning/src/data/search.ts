import { Tints } from '@/constants/theme';
import type { Category, Course, RatingOption, SortOption, Topic } from '@/types/course';

export const featuredTopics: Topic[] = [
  { id: 'java', label: 'Java' },
  { id: 'sql', label: 'SQL' },
  { id: 'javascript', label: 'Javascript' },
  { id: 'python', label: 'Python' },
  { id: 'digital-marketing', label: 'Marketing số' },
  { id: 'photoshop', label: 'Photoshop' },
  { id: 'watercolor', label: 'Màu nước' },
];

export const categories: Category[] = [
  {
    id: 'business',
    title: 'Kinh doanh',
    tint: Tints.lavender,
    icon: 'briefcase.fill',
    glyph: '💼',
    image: 'https://images.pexels.com/photos/3183150/pexels-photo-3183150.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
  {
    id: 'design',
    title: 'Thiết kế',
    tint: Tints.peach,
    icon: 'paintbrush.fill',
    glyph: '🎨',
    image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
  {
    id: 'programming',
    title: 'Lập trình',
    tint: Tints.mint,
    icon: 'chevron.left.forwardslash.chevron.right',
    glyph: '💻',
    image: 'https://images.pexels.com/photos/1181675/pexels-photo-1181675.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
  {
    id: 'film',
    title: 'Phim',
    tint: Tints.pink,
    icon: 'film.fill',
    glyph: '🎬',
    image: 'https://images.pexels.com/photos/1983037/pexels-photo-1983037.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
  {
    id: 'language',
    title: 'Ngôn ngữ',
    tint: Tints.sky,
    icon: 'globe',
    glyph: '🌐',
    image: 'https://images.pexels.com/photos/256417/pexels-photo-256417.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
];

export const filterCategories: Category[] = categories;

export const totalResults = 120;

export const sortOptions: SortOption[] = [
  { id: 'popular', label: 'Phổ biến' },
  { id: 'newest', label: 'Mới nhất' },
  { id: 'price-asc', label: 'Giá thấp → cao' },
];

export const ratingOptions: RatingOption[] = [
  { id: '4.5', stars: 4.5, label: '★ 4.5+' },
  { id: '4.0', stars: 4.0, label: '★ 4.0+' },
  { id: '3.0', stars: 3.0, label: '★ 3.0+' },
];

export const priceRange = { minimum: 0, maximum: 200 };

export const recommendedCourses: Course[] = [
  {
    id: 'website-design',
    title: 'Website Design Masterclass',
    author: 'Kelly Hamilton',
    price: 59,
    rating: 4.5,
    lessons: 12,
    tint: Tints.peach,
    image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'ux-research',
    title: 'UX Research & Testing',
    author: 'Sara Weise',
    price: 45,
    rating: 4.8,
    lessons: 10,
    tint: Tints.pink,
    image: 'https://images.pexels.com/photos/3183150/pexels-photo-3183150.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
];

export const searchResults: Course[] = [
  {
    id: 'ux-foundation',
    title: 'UX Foundation: Nhập môn UX',
    author: 'Sara Weise',
    price: 51,
    rating: 4.9,
    lessons: 12,
    tint: Tints.lavender,
    image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'design-basics',
    title: 'Design Basics for Beginners',
    author: 'Kelly Hamilton',
    price: 89,
    rating: 4.7,
    lessons: 12,
    tint: Tints.peach,
    image: 'https://images.pexels.com/photos/326503/pexels-photo-326503.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'digital-sketching',
    title: 'Digital Sketching with iPad',
    author: 'Ramono Wultschner',
    price: 49,
    rating: 4.5,
    lessons: 12,
    tint: Tints.mint,
    image: 'https://images.pexels.com/photos/1983037/pexels-photo-1983037.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'digital-portrait',
    title: 'Digital Portrait & Character Design',
    author: 'Ramono Wultschner',
    price: 67,
    rating: 4.8,
    lessons: 14,
    tint: Tints.pink,
    image: 'https://images.pexels.com/photos/261763/pexels-photo-261763.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'web-design',
    title: 'Web Design & Responsive Layouts',
    author: 'Ryan Meyers',
    price: 29,
    rating: 4.6,
    lessons: 12,
    tint: Tints.sky,
    image: 'https://images.pexels.com/photos/1181675/pexels-photo-1181675.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'mobile-app-design',
    title: 'Mobile App Design with Figma',
    author: 'Kelly Hamilton',
    price: 75,
    rating: 4.9,
    lessons: 18,
    tint: Tints.lemon,
    image: 'https://images.pexels.com/photos/1092671/pexels-photo-1092671.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'design-systems',
    title: 'Design Systems in Practice',
    author: 'Sara Weise',
    price: 95,
    rating: 4.9,
    lessons: 16,
    tint: Tints.mint,
    image: 'https://images.pexels.com/photos/1181244/pexels-photo-1181244.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'illustration-basics',
    title: 'Illustration Basics & Vector Art',
    author: 'Ramono Wultschner',
    price: 39,
    rating: 4.6,
    lessons: 9,
    tint: Tints.peach,
    image: 'https://images.pexels.com/photos/3183150/pexels-photo-3183150.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
];
