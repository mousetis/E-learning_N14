import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { storage } from '@/storage';
import { authService, cartService, savedCourseService, notificationService } from '@/services';
import type { User, CartItem } from '@/types';

interface AppContextType {
  user: User | null;
  setUser: (user: User | null) => void;
  cart: CartItem[];
  addToCart: (item: CartItem) => Promise<void>;
  removeFromCart: (id: string) => Promise<void>;
  clearCart: () => void;
  savedCourseIds: string[];
  toggleSaveCourse: (id: string) => Promise<void>;
  isSaved: (id: string) => boolean;
  unreadCount: number;
  refreshUserData: () => Promise<void>;
  refreshCart: () => Promise<void>;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [savedCourseIds, setSavedCourseIds] = useState<string[]>(['30000000-0000-0000-0000-000000000001']);
  const [unreadCount, setUnreadCount] = useState<number>(0);

  const refreshCart = useCallback(async () => {
    try {
      const cartData = await cartService.getCart();
      if (cartData && cartData.items && cartData.items.length > 0) {
        setCart(cartData.items);
        await storage.setItem('app_cart', cartData.items);
        return;
      }
    } catch {
      // fallback to storage
    }
    const savedCart = await storage.getItem<CartItem[]>('app_cart');
    if (savedCart) setCart(savedCart);
  }, []);

  const refreshUserData = useCallback(async () => {
    try {
      const currentUser = await authService.getCurrentUser();
      if (currentUser) {
        setUser(currentUser);
        // Load saved courses
        try {
          const saved = await savedCourseService.getSavedCourses();
          if (Array.isArray(saved)) {
            setSavedCourseIds(saved.map((c) => c.id));
          }
        } catch {
          // ignore
        }
        // Load unread notifications count
        try {
          const count = await notificationService.getUnreadCount();
          setUnreadCount(count);
        } catch {
          // ignore
        }
        await refreshCart();
      } else {
        const cachedUser = await storage.getItem<User>('current_user');
        if (cachedUser) setUser(cachedUser);
      }
    } catch {
      const cachedUser = await storage.getItem<User>('current_user');
      if (cachedUser) setUser(cachedUser);
    }
  }, [refreshCart]);

  // Load saved state on mount
  useEffect(() => {
    refreshUserData();
  }, [refreshUserData]);

  const addToCart = async (item: CartItem) => {
    // Optimistic local update
    setCart((prev: CartItem[]) => {
      const courseId = item.courseId || item.id;
      if (prev.some((c: CartItem) => (c.courseId || c.id) === courseId)) return prev;
      const updated = [...prev, item];
      storage.setItem('app_cart', updated);
      return updated;
    });

    // Sync with backend if possible
    try {
      const courseId = item.courseId || item.id;
      await cartService.addToCart(courseId);
      await refreshCart();
    } catch (err) {
      console.warn('addToCart backend sync note:', err);
    }
  };

  const removeFromCart = async (id: string) => {
    setCart((prev: CartItem[]) => {
      const updated = prev.filter((item: CartItem) => item.id !== id && item.courseId !== id);
      storage.setItem('app_cart', updated);
      return updated;
    });

    try {
      await cartService.removeFromCart(id);
    } catch (err) {
      console.warn('removeFromCart backend sync note:', err);
    }
  };

  const clearCart = () => {
    setCart([]);
    storage.removeItem('app_cart');
  };

  const toggleSaveCourse = async (id: string) => {
    const isCurrentlySaved = savedCourseIds.includes(id);
    setSavedCourseIds((prev: string[]) =>
      isCurrentlySaved ? prev.filter((item: string) => item !== id) : [...prev, id]
    );

    try {
      if (isCurrentlySaved) {
        await savedCourseService.unsaveCourse(id);
      } else {
        await savedCourseService.saveCourse(id);
      }
    } catch (err) {
      console.warn('toggleSaveCourse backend note:', err);
    }
  };

  const isSaved = (id: string) => savedCourseIds.includes(id);

  return (
    <AppContext.Provider
      value={{
        user,
        setUser,
        cart,
        addToCart,
        removeFromCart,
        clearCart,
        savedCourseIds,
        toggleSaveCourse,
        isSaved,
        unreadCount,
        refreshUserData,
        refreshCart,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useAppStore() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppStore must be used within an AppProvider');
  }
  return context;
}
