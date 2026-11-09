import { useNavigation, useRoute } from '@react-navigation/native';
import { resolveRoute } from './navigationAdapter';
import { navigate as globalNavigate, goBack as globalGoBack } from './navigationRef';

export const router = {
  push: (href: any) => {
    const { screen, params } = resolveRoute(href);
    globalNavigate(screen, params);
  },
  replace: (href: any) => {
    const { screen, params } = resolveRoute(href);
    globalNavigate(screen, params);
  },
  back: () => {
    globalGoBack();
  },
};

export function useRouter() {
  let nav: any = null;
  try {
    nav = useNavigation();
  } catch {
    // Navigation container not yet ready
  }

  return {
    push: (href: any) => {
      const { screen, params } = resolveRoute(href);
      if (nav && typeof nav.navigate === 'function') {
        nav.navigate(screen, params);
      } else {
        globalNavigate(screen, params);
      }
    },
    replace: (href: any) => {
      const { screen, params } = resolveRoute(href);
      if (nav && typeof nav.navigate === 'function') {
        nav.navigate(screen, params);
      } else {
        globalNavigate(screen, params);
      }
    },
    back: () => {
      if (nav && typeof nav.goBack === 'function' && nav.canGoBack()) {
        nav.goBack();
      } else {
        globalGoBack();
      }
    },
  };
}

export function useLocalSearchParams<T = Record<string, string>>(): T {
  try {
    const route = useRoute<any>();
    return (route?.params ?? {}) as T;
  } catch {
    return {} as T;
  }
}
