import type { SavedCourse } from '@/types/course';

export interface UserProfileFull {
  id: string;
  name: string;
  email: string;
  role: string;
  initials: string;
  avatar: string;
  avatarColor: string;
  location: string;
  bio: string;
  joinedDate: string;
  level: string;
  stats: {
    saved: number;
    inProgress: number;
    completed: number;
  };
  certificates: {
    id: string;
    title: string;
    issuer: string;
    date: string;
    icon: string;
  }[];
  skills: string[];
}

export const currentUser: UserProfileFull = {
  id: 'rosie-1',
  name: 'Martha Rosie',
  email: 'rosie@email.com',
  role: 'UX/UI Designer',
  initials: 'MR',
  avatar: 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=400',
  avatarColor: '#ffd9a8',
  location: 'Thành phố Hồ Chí Minh',
  bio: 'Nhà thiết kế sản phẩm số đam mê kiến tạo trải nghiệm người dùng tinh tế, thân thiện và ứng dụng thiết kế hệ thống (Design Systems).',
  joinedDate: 'Tháng 4, 2024',
  level: 'Học viên Chuyên nghiệp',
  stats: {
    saved: 25,
    inProgress: 24,
    completed: 98,
  },
  skills: ['Figma', 'UI Design', 'Design Systems', 'User Research', 'Prototyping', 'React Native'],
  certificates: [
    {
      id: 'cert-1',
      title: 'UX Foundation Certification',
      issuer: 'E-Learning Academy',
      date: '10/2025',
      icon: '🎓',
    },
    {
      id: 'cert-2',
      title: 'Advanced Figma & Components',
      issuer: 'Design Pro Institute',
      date: '01/2026',
      icon: '🏆',
    },
  ],
};

export const savedCourses: SavedCourse[] = [
  {
    id: 'product-design',
    title: 'Product Design Masterclass',
    author: 'Dennis Sweeney',
    meta: '$190 · ★ 4.8 · 18 bài',
    coverColor: '#bce0fd',
    image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'website-design',
    title: 'Website Design & Webflow Architecture',
    author: 'Ramono Wultschner',
    meta: '$59 · ★ 4.5 · 12 bài',
    coverColor: '#ffd9a8',
    image: 'https://images.pexels.com/photos/326503/pexels-photo-326503.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'mobile-ui-design',
    title: 'Mobile UI Design with Figma Pro',
    author: 'Ramono Wultschner',
    meta: '$320 · ★ 4.9 · 24 bài',
    coverColor: '#b8f1e0',
    image: 'https://images.pexels.com/photos/1092671/pexels-photo-1092671.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
  {
    id: 'ux-research-advanced',
    title: 'UX Research & Usability Testing',
    author: 'Sara Weise',
    meta: '$89 · ★ 4.7 · 14 bài',
    coverColor: '#c7c2fe',
    image: 'https://images.pexels.com/photos/3183150/pexels-photo-3183150.jpeg?auto=compress&cs=tinysrgb&w=600',
  },
];
