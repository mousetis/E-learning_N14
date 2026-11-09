import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useTheme } from '@/hooks/use-theme';
import { navigationRef } from './navigationRef';
import type { RootStackParamList } from './types';

// Tab Navigator
import BottomTabNavigator from './BottomTabNavigator';

// Stack Screens
import CourseDetailScreen from '@/screens/CourseDetailScreen';
import CourseCurriculumScreen from '@/screens/CourseCurriculumScreen';
import CourseReviewsScreen from '@/screens/CourseReviewsScreen';
import CoursePlayerScreen from '@/screens/CoursePlayerScreen';
import CourseQaScreen from '@/screens/CourseQaScreen';
import InstructorProfileScreen from '@/screens/InstructorProfileScreen';
import CartScreen from '@/screens/CartScreen';
import CheckoutScreen from '@/screens/CheckoutScreen';
import CheckoutSuccessScreen from '@/screens/CheckoutSuccessScreen';
import NotificationsScreen from '@/screens/NotificationsScreen';
import SettingsScreen from '@/screens/SettingsScreen';
import SearchFilterScreen from '@/screens/SearchFilterScreen';
import SearchResultsScreen from '@/screens/SearchResultsScreen';
import WelcomeScreen from '@/screens/WelcomeScreen';
import LoginScreen from '@/screens/LoginScreen';
import RegisterScreen from '@/screens/RegisterScreen';
import ForgotPasswordScreen from '@/screens/ForgotPasswordScreen';

const Stack = createNativeStackNavigator<RootStackParamList>();

export function AppNavigator() {
  const theme = useTheme();

  return (
    <NavigationContainer ref={navigationRef}>
      <Stack.Navigator
        initialRouteName="MainTabs"
        screenOptions={{
          headerShown: false,
          contentStyle: { backgroundColor: theme.background },
          animation: 'slide_from_right',
          animationDuration: 220,
        }}
      >
        {/* Main 4 Tabs */}
        <Stack.Screen
          name="MainTabs"
          component={BottomTabNavigator}
          options={{ animation: 'fade' }}
        />

        {/* Details & Modal Screens */}
        <Stack.Screen name="CourseDetail" component={CourseDetailScreen} />
        <Stack.Screen name="CourseCurriculum" component={CourseCurriculumScreen} />
        <Stack.Screen name="CourseReviews" component={CourseReviewsScreen} />
        <Stack.Screen name="CoursePlayer" component={CoursePlayerScreen} />
        <Stack.Screen name="CourseQa" component={CourseQaScreen} />
        <Stack.Screen name="InstructorProfile" component={InstructorProfileScreen} />

        {/* E-Commerce Flow */}
        <Stack.Screen name="Cart" component={CartScreen} />
        <Stack.Screen name="Checkout" component={CheckoutScreen} />
        <Stack.Screen name="CheckoutSuccess" component={CheckoutSuccessScreen} options={{ animation: 'fade' }} />

        {/* Other Features */}
        <Stack.Screen name="Notifications" component={NotificationsScreen} />
        <Stack.Screen name="Settings" component={SettingsScreen} />
        <Stack.Screen name="SearchFilter" component={SearchFilterScreen} options={{ presentation: 'transparentModal' }} />
        <Stack.Screen name="SearchResults" component={SearchResultsScreen} />

        {/* Auth Screens */}
        <Stack.Screen name="Welcome" component={WelcomeScreen} options={{ animation: 'fade' }} />
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Register" component={RegisterScreen} />
        <Stack.Screen name="ForgotPassword" component={ForgotPasswordScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default AppNavigator;
