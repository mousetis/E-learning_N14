import { apiClient, ENDPOINTS } from '@/api';
import { storage } from '@/storage';
import type { User } from '@/types/user';

function extractInitials(name: string): string {
  if (!name) return 'U';
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function mapRoleName(role?: string): string {
  if (!role) return 'Học viên';
  switch (role.toUpperCase()) {
    case 'ADMIN':
      return 'Quản trị viên';
    case 'INSTRUCTOR':
      return 'Giảng viên';
    case 'STUDENT':
    default:
      return 'Học viên';
  }
}

export const authService = {
  async login(email: string, password: string): Promise<User> {
    const data = await apiClient.post<any>(ENDPOINTS.AUTH.LOGIN, { email, password });
    
    if (data.accessToken) {
      await storage.setItem('auth_token', data.accessToken);
    }
    if (data.refreshToken) {
      await storage.setItem('refresh_token', data.refreshToken);
    }

    const rawUser = data.user || {};
    const name = rawUser.fullName || rawUser.name || 'Người dùng';
    
    let user: User = {
      id: rawUser.id,
      name,
      fullName: name,
      email: rawUser.email || email,
      role: mapRoleName(rawUser.role),
      avatar: rawUser.avatarUrl || 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=400',
      initials: extractInitials(name),
      location: 'Việt Nam',
      level: 'Học viên tích cực',
      token: data.accessToken,
      refreshToken: data.refreshToken,
      stats: {
        saved: 0,
        inProgress: 0,
        completed: 0,
      },
    };

    // Attempt to load full profile stats
    try {
      const profile = await apiClient.get<any>(ENDPOINTS.USER.ME);
      if (profile) {
        user = {
          ...user,
          fullName: profile.fullName || user.fullName,
          name: profile.fullName || user.name,
          role: mapRoleName(profile.role),
          stats: {
            saved: profile.savedCount || 0,
            inProgress: profile.inProgressCount || 0,
            completed: profile.completedCount || 0,
          },
        };
      }
    } catch {
      // keep basic user if profile fails
    }

    await storage.setItem('current_user', user);
    return user;
  },

  async register(fullName: string, email: string, password: string): Promise<any> {
    return apiClient.post(ENDPOINTS.AUTH.REGISTER, {
      fullName,
      email,
      password,
      confirmPassword: password,
      acceptedTerms: true,
    });
  },

  async verifyOtp(email: string, otp: string): Promise<any> {
    return apiClient.post(ENDPOINTS.AUTH.VERIFY_OTP, {
      email,
      otp,
    });
  },

  async resendOtp(email: string, purpose: 'REGISTER' | 'RESET_PASSWORD' = 'REGISTER'): Promise<any> {
    return apiClient.post(ENDPOINTS.AUTH.RESEND_OTP, {
      email,
      purpose,
    });
  },

  async forgotPassword(email: string): Promise<any> {
    return apiClient.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, { email });
  },

  async resetPassword(email: string, otp: string, newPassword: string): Promise<any> {
    return apiClient.post(ENDPOINTS.AUTH.RESET_PASSWORD, {
      email,
      otp,
      newPassword,
      confirmPassword: newPassword,
    });
  },

  async logout(): Promise<void> {
    try {
      const refreshToken = await storage.getItem<string>('refresh_token');
      if (refreshToken) {
        await apiClient.post(ENDPOINTS.AUTH.LOGOUT, { refreshToken });
      }
    } catch {
      // Ignore network errors on logout
    } finally {
      await storage.removeItem('auth_token');
      await storage.removeItem('refresh_token');
      await storage.removeItem('current_user');
    }
  },

  async getCurrentUser(): Promise<User | null> {
    const token = await storage.getItem<string>('auth_token');
    if (!token) return null;

    try {
      const profile = await apiClient.get<any>(ENDPOINTS.USER.ME);
      const name = profile.fullName || 'Người dùng';
      const user: User = {
        id: profile.id,
        name,
        fullName: name,
        email: profile.email,
        role: mapRoleName(profile.role),
        avatar: profile.avatarUrl || 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=400',
        initials: extractInitials(name),
        location: 'Việt Nam',
        level: 'Học viên tích cực',
        token,
        stats: {
          saved: profile.savedCount || 0,
          inProgress: profile.inProgressCount || 0,
          completed: profile.completedCount || 0,
        },
      };
      await storage.setItem('current_user', user);
      return user;
    } catch {
      return storage.getItem<User>('current_user');
    }
  },

  async updateProfile(fullName: string, jobTitle?: string): Promise<User> {
    const profile = await apiClient.put<any>(ENDPOINTS.USER.UPDATE_PROFILE, {
      fullName,
      jobTitle,
    });
    const current = (await storage.getItem<User>('current_user')) || ({} as User);
    const updated: User = {
      ...current,
      name: profile.fullName || current.name,
      fullName: profile.fullName || current.fullName,
    };
    await storage.setItem('current_user', updated);
    return updated;
  },

  async changePassword(oldPassword: string, newPassword: string): Promise<any> {
    return apiClient.put(ENDPOINTS.USER.CHANGE_PASSWORD, {
      oldPassword,
      newPassword,
      confirmPassword: newPassword,
    });
  },
};
