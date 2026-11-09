import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { useColorScheme, View } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

import { AppNavigator } from '@/navigation';
import { AppProvider } from '@/store';
import { Colors } from '@/theme';

export default function App() {
  const scheme = useColorScheme();
  const palette = scheme === 'dark' ? Colors.dark : Colors.light;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <AppProvider>
          <View style={{ flex: 1, backgroundColor: palette.background }}>
            <StatusBar style={scheme === 'dark' ? 'light' : 'dark'} />
            <AppNavigator />
          </View>
        </AppProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}
