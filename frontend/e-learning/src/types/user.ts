export interface User {
  name: string;
  role: string;
  stats: {
    saved: number;
    inProgress: number;
    completed: number;
  };
}
