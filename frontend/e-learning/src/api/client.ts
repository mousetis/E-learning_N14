import { API_BASE_URL } from './endpoints';
import { storage } from '@/storage';

export interface BackendResponse<T = any> {
  success: boolean;
  code?: string;
  message?: string;
  data?: T;
  timestamp?: string;
}

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  private async getAuthHeaders(): Promise<Record<string, string>> {
    try {
      const token = await storage.getItem<string>('auth_token');
      if (token) {
        return {
          Authorization: `Bearer ${token}`,
        };
      }
    } catch {
      // ignore
    }
    return {};
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const authHeaders = await this.getAuthHeaders();
    const url = endpoint.startsWith('http') ? endpoint : `${this.baseUrl}${endpoint}`;

    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...authHeaders,
      ...((options.headers as Record<string, string>) || {}),
    };

    const res = await fetch(url, {
      ...options,
      headers,
    });

    let json: any = null;
    const text = await res.text();
    if (text) {
      try {
        json = JSON.parse(text);
      } catch {
        json = text;
      }
    }

    if (!res.ok) {
      const errorMsg =
        (json && typeof json === 'object' && json.message) ||
        `Lỗi kết nối máy chủ (${res.status})`;
      const err = new Error(errorMsg);
      (err as any).status = res.status;
      (err as any).response = json;
      throw err;
    }

    if (json && typeof json === 'object' && 'success' in json) {
      if (json.success === false) {
        throw new Error(json.message || 'Thao tác không thành công');
      }
      return json.data !== undefined ? json.data : json;
    }

    return json as T;
  }

  async get<T>(endpoint: string, headers: Record<string, string> = {}): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'GET',
      headers,
    });
  }

  async post<T>(endpoint: string, body?: any, headers: Record<string, string> = {}): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  }

  async put<T>(endpoint: string, body?: any, headers: Record<string, string> = {}): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  }

  async patch<T>(endpoint: string, body?: any, headers: Record<string, string> = {}): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PATCH',
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  }

  async delete<T>(endpoint: string, headers: Record<string, string> = {}): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'DELETE',
      headers,
    });
  }
}

export const apiClient = new ApiClient();
