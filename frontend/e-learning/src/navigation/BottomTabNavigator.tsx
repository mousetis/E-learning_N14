import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import type { MainTabParamList } from './types';

// Screens
import HomeScreen from '@/screens/HomeScreen';
import SearchScreen from '@/screens/SearchScreen';
import MyCoursesScreen from '@/screens/MyCoursesScreen';
import ProfileScreen from '@/screens/ProfileScreen';

const Tab = createBottomTabNavigator<MainTabParamList>();

export function BottomTabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: { display: 'none' }, // Using our Figma BottomNavBar component
      }}
    >
      <Tab.Screen name="Home" component={HomeScreen} />
      <Tab.Screen name="Search" component={SearchScreen} />
      <Tab.Screen name="MyCourses" component={MyCoursesScreen} />
      <Tab.Screen name="Profile" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

export default BottomTabNavigator;
