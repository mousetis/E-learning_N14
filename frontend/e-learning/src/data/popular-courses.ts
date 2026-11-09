import type { PopularCourse } from '@/types/course';

export const popularCourses: PopularCourse[] = [
  {
    id: 'php',
    title: 'PHP in One Click',
    author: 'Ramono Wultschner',
    price: '$59',
    coverColor: '#7B61FF',
    image: 'https://images.pexels.com/photos/1181675/pexels-photo-1181675.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'python',
    title: 'Python Introduction',
    author: 'Ramono Wultschner',
    price: '$39',
    coverColor: '#00BFA6',
    badge: 'Bán chạy',
    image: 'https://images.pexels.com/photos/1181244/pexels-photo-1181244.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'intro',
    title: 'UI/UX Design Essentials',
    author: 'Olivia Wang',
    price: '$29',
    coverColor: '#3B82F6',
    image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'web-mastery',
    title: 'Fullstack Web Mastery',
    author: 'David Miller',
    price: '$79',
    coverColor: '#8B5CF6',
    badge: 'Mới nhất',
    image: 'https://images.pexels.com/photos/326503/pexels-photo-326503.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
];
