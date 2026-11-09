import { API_BASE_URL } from './endpoints';

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  async get<T>(endpoint: string, headers: Record<string, string> = {}): Promise<T> {
    const res = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
    });
    if (!res.ok) {
      throw new Error(`HTTP Error: ${res.status}`);
    }
    return res.json();
  }

  async post<T>(endpoint: string, body: any, headers: Record<string, string> = {}): Promise<T> {
    const res = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      throw new Error(`HTTP Error: ${res.status}`);
    }
    return res.json();
  }

  async put<T>(endpoint: string, body: any, headers: Record<string, string> = {}): Promise<T> {
    const res = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      throw new Error(`HTTP Error: ${res.status}`);
    }
    return res.json();
  }

  async delete<T>(endpoint: string, headers: Record<string, string> = {}): Promise<T> {
    const res = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
    });
    if (!res.ok) {
      throw new Error(`HTTP Error: ${res.status}`);
    }
    return res.json();
  }
}

export const apiClient = new ApiClient();
