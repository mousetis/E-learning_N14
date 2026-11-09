// Safe cross-platform Storage Service
class LocalStorage {
  private memory = new Map<string, string>();

  async getItem<T = string>(key: string): Promise<T | null> {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        const item = window.localStorage.getItem(key);
        if (!item) return null;
        try {
          return JSON.parse(item) as T;
        } catch {
          return item as unknown as T;
        }
      }
    } catch {
      // Fallback to memory store
    }
    const val = this.memory.get(key);
    if (!val) return null;
    try {
      return JSON.parse(val) as T;
    } catch {
      return val as unknown as T;
    }
  }

  async setItem(key: string, value: any): Promise<void> {
    const serialized = typeof value === 'string' ? value : JSON.stringify(value);
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.setItem(key, serialized);
      }
    } catch {
      // Fallback
    }
    this.memory.set(key, serialized);
  }

  async removeItem(key: string): Promise<void> {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.removeItem(key);
      }
    } catch {
      // Fallback
    }
    this.memory.delete(key);
  }

  async clear(): Promise<void> {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.clear();
      }
    } catch {
      // Fallback
    }
    this.memory.clear();
  }
}

export const storage = new LocalStorage();
