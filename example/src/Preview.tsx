import * as React from 'react';
import { StyleSheet, View } from 'react-native';
import FBReaderView from 'react-native-fbreader';

type PreviewProps = {
  navigation: any,
  route: any
}

export default function Preview({navigation, route}: PreviewProps) {
  return (
    <View style={styles.container}>
      <FBReaderView style={styles.box} book={route.params.path} />
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
