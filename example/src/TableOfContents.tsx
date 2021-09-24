import React from 'react'
import { useEffect, useState } from 'react';
import { View, StyleSheet, TextInput, Text } from 'react-native'
import { FBReader, FBReaderCoverView } from 'react-native-fbreader';

type TableOfContentsProps = {
  navigation: any,
  route: any
}

const TableOfContents = ({route}: TableOfContentsProps) => {
  const [toc, setToc] = useState("");
  useEffect(() => {
    const updateToc = async () => {
      let value = await FBReader.tableOfContents(route.params.path);
      setToc(JSON.stringify(value));
    };
    updateToc();
  }, []);
  return (
    <View style={styles.container}>
      <FBReaderCoverView 
        style={styles.coverImage} 
        book={route.params.path} />
      <Text>Table Of Contents</Text>
      <TextInput 
        style={styles.textinput}
        value={toc}
        editable={false}
        multiline={true} />
    </View>
  )
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  coverImage: {
    width: 160, 
    height: 160,
  },
  textinput: {
    flex: 2,
    width: '100%',
    margin: 12,
    borderWidth: 1,
    padding: 10,
    color: '#333',
  }
});

export default TableOfContents;
