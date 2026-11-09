import { useNavigation } from '@react-navigation/native';
import { navigate, goBack } from './navigationRef';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import type { RootStackParamList } from './types';

export function resolveRoute(href: string | { pathname: string; params?: any }): { screen: keyof RootStackParamList; params?: any } {
  let path = typeof href === 'string' ? href : href.pathname;
  let params = typeof href === 'object' ? href.params : undefined;

  if (path === '/' || path === '/index') return { screen: 'MainTabs', params: { screen: 'Home' } };
  if (path === '/search') return { screen: 'MainTabs', params: { screen: 'Search' } };
  if (path === '/search/results') return { screen: 'SearchResults', params };
  if (path === '/search/filter') return { screen: 'SearchFilter' };
  if (path === '/my-courses') return { screen: 'MainTabs', params: { screen: 'MyCourses' } };
  if (path === '/profile') return { screen: 'MainTabs', params: { screen: 'Profile' } };
  if (path === '/cart') return { screen: 'Cart' };
  if (path === '/checkout') return { screen: 'Checkout' };
  if (path === '/checkout-success') return { screen: 'CheckoutSuccess' };
  if (path === '/notifications') return { screen: 'Notifications' };
  if (path === '/settings') return { screen: 'Settings' };
  if (path === '/welcome') return { screen: 'Welcome' };
  if (path === '/login') return { screen: 'Login' };
  if (path === '/register') return { screen: 'Register' };
  if (path === '/forgot-password') return { screen: 'ForgotPassword' };

  // Course routes
  if (path.startsWith('/course/')) {
    const parts = path.split('/');
    // e.g. /course/php
    const id = parts[2];
    const sub = parts[3];
    if (sub === 'curriculum') return { screen: 'CourseCurriculum', params: { id } };
    if (sub === 'reviews') return { screen: 'CourseReviews', params: { id } };
    if (sub === 'player') return { screen: 'CoursePlayer', params: { id } };
    if (sub === 'qa') return { screen: 'CourseQa', params: { id } };
    return { screen: 'CourseDetail', params: { id } };
  }

  // Instructor routes
  if (path.startsWith('/instructor/')) {
    const parts = path.split('/');
    const id = parts[2];
    return { screen: 'InstructorProfile', params: { id } };
  }

  return { screen: 'MainTabs', params };
}

export function useAppRouter() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  return {
    push: (href: string | { pathname: string; params?: any }) => {
      const { screen, params } = resolveRoute(href);
      // @ts-ignore
      navigation.navigate(screen, params);
    },
    replace: (href: string | { pathname: string; params?: any }) => {
      const { screen, params } = resolveRoute(href);
      // @ts-ignore
      navigation.navigate(screen, params);
    },
    back: () => {
      if (navigation.canGoBack()) navigation.goBack();
    },
  };
}
