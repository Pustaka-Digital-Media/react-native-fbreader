import * as React from 'react';
import { useEffect  } from 'react';
import { StyleSheet, View, Button } from 'react-native';
import { FBReaderView } from 'react-native-fbreader';
import { useNavigation } from '@react-navigation/native'

type PreviewProps = {
  navigation: any,
  route: any
}

export default function Preview({navigation, route}: PreviewProps) {
  const nav = useNavigation();
  useEffect(() => {
    nav.setOptions({
      headerRight: () => <Button title="TOC" onPress={()=>{
        navigation.navigate('TableOfContents', {path: route.params.path})
      }} />,
    });
  })
  return (
    <View style={styles.container}>
      <FBReaderView 
        style={styles.box} 
        book={route.params.path}
        background="hardpaper"
        colorProfile="einkLight"
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: '100%',
    height: '100%',
    marginVertical: 20,
  },
});
