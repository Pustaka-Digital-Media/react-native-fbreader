import * as React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
const Stack = createNativeStackNavigator();

import BookList from './BookList';
import Preview from './Preview';
import TableOfContents from './TableOfContents';


export default function App() {

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="BookList" component={BookList} />
        <Stack.Screen name="Preview" component={Preview} />
        <Stack.Screen name="TableOfContents" component={TableOfContents} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

