import React, { createContext, useContext, useState, useEffect } from 'react';
import { storage } from '@/storage';
import type { User, CartItem } from '@/types';

interface AppContextType {
  user: User | null;
  setUser: (user: User | null) => void;
  cart: CartItem[];
  addToCart: (item: CartItem) => void;
  removeFromCart: (id: string) => void;
  clearCart: () => void;
  savedCourseIds: string[];
  toggleSaveCourse: (id: string) => void;
  isSaved: (id: string) => boolean;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [savedCourseIds, setSavedCourseIds] = useState<string[]>(['website-design', 'ux-research']);

  // Load saved state on mount
  useEffect(() => {
    (async () => {
      const savedCart = await storage.getItem<CartItem[]>('app_cart');
      if (savedCart) setCart(savedCart);

      const savedUser = await storage.getItem<User>('current_user');
      if (savedUser) setUser(savedUser);
    })();
  }, []);

  const addToCart = (item: CartItem) => {
    setCart((prev) => {
      if (prev.some((c) => c.courseId === item.courseId)) return prev;
      const updated = [...prev, item];
      storage.setItem('app_cart', updated);
      return updated;
    });
  };

  const removeFromCart = (id: string) => {
    setCart((prev) => {
      const updated = prev.filter((item) => item.id !== id && item.courseId !== id);
      storage.setItem('app_cart', updated);
      return updated;
    });
  };

  const clearCart = () => {
    setCart([]);
    storage.removeItem('app_cart');
  };

  const toggleSaveCourse = (id: string) => {
    setSavedCourseIds((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
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
