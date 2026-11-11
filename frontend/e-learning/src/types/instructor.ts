export interface Instructor {
  id: string;
  name: string;
  role?: string;
  rating: string | number;
  avatarColor?: string;
  avatarUri?: string;
  studentCount?: number;
}
