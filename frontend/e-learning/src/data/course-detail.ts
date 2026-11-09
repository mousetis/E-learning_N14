import type {
  Chapter,
  CourseDetail,
  Project,
  ProjectFile,
  QaThread,
  Review,
} from '@/types/course';

export const course: CourseDetail = {
  id: 'ux-foundation',
  shortTitle: 'UX Foundation',
  category: 'UX Foundations',
  headline: ['Giới thiệu về', 'thiết kế UX'],
  title: 'UX Foundation: Nhập môn thiết kế trải nghiệm người dùng',
  rating: 4.5,
  reviewCount: 1233,
  lessonCount: 12,
  price: '$259',
  discount: 'Giảm 80% · $1020',
  description: 'Convallis in semper laoreet nibh leo. Vivamus malesuada ipsum pulvinar non rutrum risus dui...',
  descriptionMore:
    'Convallis in semper laoreet nibh leo. Vivamus malesuada ipsum pulvinar non rutrum risus dui. Exercitation enim eiusmod elit sint laborum, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
  benefits: [
    '14 giờ video theo yêu cầu',
    'Giảng viên bản ngữ',
    'Tài liệu miễn phí 100%',
    'Truy cập trọn đời',
    'Có chứng chỉ hoàn thành',
    'Hỗ trợ 24/7',
  ],
  instructor: {
    id: 'sara-weise',
    name: 'Sara Weise',
    role: 'UI/UX Designer',
    tone: 'pink',
    avatarUri: 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=400',
  },
  related: [
    {
      id: 'product-design',
      title: 'Product Design',
      author: 'Dennis Sweeney',
      meta: '$90 · ★ 4.5 · 12 bài',
      tone: 'blue',
      image: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
  ],
  stats: { likes: 231, shares: 16 },
  coverImage: 'https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=800',
};

export const chapters: Chapter[] = [
  {
    id: 'chapter-1',
    title: 'I – Giới thiệu',
    lessons: [
      {
        id: 'lesson-01',
        index: '01',
        title: 'Amet adipisicing consectetur',
        duration: '01:23 phút',
        status: 'completed',
      },
      {
        id: 'lesson-02',
        index: '02',
        title: 'Adipisicing dolor amet occaeca',
        duration: '01:23 phút',
        status: 'in-progress',
      },
    ],
  },
  {
    id: 'chapter-2',
    title: 'II – Lập kế hoạch nghiên cứu UX',
    lessons: [
      {
        id: 'lesson-03',
        index: '03',
        title: 'Exercitation elit incididunt esse',
        duration: '01:23 phút',
        status: 'locked',
      },
      {
        id: 'lesson-04',
        index: '04',
        title: 'Duis esse ipsum laboru',
        duration: '01:23 phút',
        status: 'locked',
      },
    ],
  },
];

export const courseReviews: Review[] = [
  {
    id: 'review-1',
    author: 'Jinny Oslin',
    tone: 'purple',
    timeAgo: '1 ngày trước',
    rating: 5,
    body: 'Nostrud excepteur magna id est quis in aliqua consequat. Exercitation enim eiusmod elit sint laborum.',
  },
  {
    id: 'review-2',
    author: 'Jane Barry',
    tone: 'peach',
    timeAgo: '1 ngày trước',
    rating: 4,
    body: 'Nostrud excepteur magna id est quis in aliqua consequat. Exercitation enim eiusmod elit sint laborum.',
  },
];

export const courseProjects: Project[] = [
  { id: 'project-1', title: 'Wireframe', author: 'Ramono W.', tone: 'purple' },
  { id: 'project-2', title: 'Personal', author: 'Thomas Carlson', tone: 'mint' },
];

export const courseFiles: ProjectFile[] = [
  { id: 'file-1', name: 'Document 1.txt', size: '612 Kb' },
  { id: 'file-2', name: 'Document 2.txt', size: '612 Kb' },
];

export const qaThreads: QaThread[] = [
  {
    id: 'qa-1',
    author: 'Jane Barry',
    tone: 'purple',
    timeAgo: '1 ngày trước',
    body: 'Deserunt minim incididunt cillum nostrud do voluptate excepteur minim ex minim est.',
    likes: 23,
    comments: 5,
  },
  {
    id: 'qa-2',
    author: 'Thomas',
    tone: 'peach',
    timeAgo: '1 ngày trước',
    body: 'Deserunt minim incididunt cillum nostrud do voluptate excepteur minim ex minim est.',
    likes: 23,
    comments: 5,
  },
  {
    id: 'qa-3',
    author: 'Jenny Barry',
    tone: 'mint',
    timeAgo: '1 ngày trước',
    body: 'Deserunt minim incididunt cillum nostrud do voluptate excepteur minim ex minim est.',
    likes: 23,
    comments: 5,
  },
];
