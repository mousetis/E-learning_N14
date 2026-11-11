import { createNavigationContainerRef } from '@react-navigation/native';
import type { RootStackParamList } from './types';

export const navigationRef = createNavigationContainerRef<RootStackParamList>();

export function navigate<RouteName extends keyof RootStackParamList>(
  name: RouteName,
  params?: any
) {
  if (navigationRef.isReady()) {
    if (name === ('Home' as any) || name === ('Search' as any) || name === ('MyCourses' as any) || name === ('Profile' as any)) {
      // @ts-ignore
      navigationRef.navigate('MainTabs', { screen: name });
      return;
    }
    // @ts-ignore
    navigationRef.navigate(name, params);
  }
}

export function goBack() {
  if (navigationRef.isReady()) {
    if (navigationRef.canGoBack()) {
      navigationRef.goBack();
    } else {
      // @ts-ignore
      navigationRef.navigate('MainTabs', { screen: 'Home' });
    }
  }
}
