import { apiClient, ENDPOINTS } from '@/api';
import { storage } from '@/storage';
import type { User } from '@/types';

export const authService = {
  async login(email: string, password: string): Promise<User> {
    try {
      const user = await apiClient.post<User>(ENDPOINTS.AUTH.LOGIN, { email, password });
      if (user.token) {
        await storage.setItem('auth_token', user.token);
      }
      return user;
    } catch {
      // Mock login for offline / demo mode
      const mockUser: User = {
        id: 'usr-demo',
        name: 'Martha Rosie',
        email,
        role: 'Học viên',
        avatar: 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=400',
        token: 'mock-jwt-token-xyz',
      };
      await storage.setItem('auth_token', mockUser.token!);
      return mockUser;
    }
  },

  async logout(): Promise<void> {
    await storage.removeItem('auth_token');
    await storage.removeItem('current_user');
  },

  async getCurrentUser(): Promise<User | null> {
    const token = await storage.getItem('auth_token');
    if (!token) return null;
    return storage.getItem<User>('current_user');
  },
};
