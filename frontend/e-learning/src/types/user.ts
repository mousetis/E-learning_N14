export interface User {
  id: string;
  name: string;
  fullName?: string;
  email: string;
  role: string;
  avatar?: string;
  avatarUrl?: string;
  avatarColor?: string;
  initials?: string;
  location?: string;
  level?: string;
  token?: string;
  refreshToken?: string;
  stats?: {
    saved: number;
    inProgress: number;
    completed: number;
  };
}
