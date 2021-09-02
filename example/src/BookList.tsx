import * as React from 'react';
import { useState, useEffect, useRef } from 'react';

import { StyleSheet, View, TextInput, Text, FlatList, Button, TouchableOpacity } from 'react-native';
import RNFS from 'react-native-fs';

export default function BookList({ navigation }) {
  const documentDirectoryPath = RNFS.DocumentDirectoryPath;
  const [text, setText] = useState("");
  const [books, setBooks] = useState([]);
  useEffect(() => {
    let exec = async () => {
      updateBooks(documentDirectoryPath);
    }
    exec();
  }, []);

  const updateBooks = async (dirpath: string) => {
    let books = await RNFS.readDir(dirpath);

    setBooks(books.filter((item) => {return item.name.endsWith(".epub")}));
  }

  const downloadBook = async (url: string, path: string) => {
    let filename = url.substring(url.lastIndexOf('/')+1);
    let destinationPath = `${path}/${filename}`;
    let options = {
      fromUrl: url,
      toFile: destinationPath,
    };
    let response = await RNFS.downloadFile(options);
    response.promise.then(async res => {
      console.log(res);
      updateBooks(documentDirectoryPath);
      setText("");
    });
  }

  return (
    <View style={styles.container}>
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        <TextInput 
          style={styles.input} 
          placeholder="epub URL to download" 
          onChangeText={setText}
          value={text} />
        <Button title="Download" onPress={()=>{
          downloadBook(text, documentDirectoryPath);
        }} />
      </View>

      <View style={{flex: 2, width: '100%', backgroundColor: '#ddd'}}>
        <FlatList
          data={books}
          renderItem={({item}) =>
            <TouchableOpacity onPress={()=>{
              navigation.navigate('Preview', {path: item.path})
            }}>
              <Text style={styles.item}>{item.name}</Text>
            </TouchableOpacity>
          }
          keyExtractor={(item) => item.name}
        />
      </View>
      
    </View>
  );
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
  item: {
    padding: 10,
    fontSize: 20,
    height: 44,
  },
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
    flex: 2,
  },
});
