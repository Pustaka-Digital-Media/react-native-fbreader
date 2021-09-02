# react-native-fbreader

RN plugin for FBReader SDK

## Installation

```sh
npm install bakhtiyork/react-native-fbreader
```

## Usage

```js
import {
  FBReader,
  FBReaderView, 
  FBReaderCoverView
} from "react-native-fbreader";

...
// epub rendering view
<FBReaderView  
  book={filePathToBook}
  textColor="#333"
  fontSize="12"
  background="wood"/>

...
// epub cover view
<FBReaderCoverView
  book={filePathToBook} />

...
// epub table of contents - JSON object
let content = FBReader.tableOfContents(filePathToBook);
```

## FBReaderView

`FBReaderView` properties definition:
```js
type FBReaderViewProps = {
  background?: string; // hardpaper, leather, paper, sand, sepia, wood
  book?: string; // path to ebook
  colorProfile?: string;
  fontSize?: number;
  searchInText?: string;
  tocReference?: number; // reference of TOC (see FBReader.tableOfContents)
  page?: number;
  style?: ViewStyle;
};
```
You can set `book` (or other any property) using state variables or programmatically via references.

Using state variables:
```js
const [book, setBook] = useState();

...

<FBReaderView book={book} />
```

Using references:
```js
const viewRef = useRef();

...

<FBReaderView ref={viewRef}>

...

viewRef.current.setNativeProps({
  book: '/path/to/the/epub/file',
  page: 20  
});

```

## Download eBooks
It seems that `react-native-fs` is most convenient way to download **epub** files, but you can use any other alternative way to do it (`axios`, etc).

```js
import RNFS from 'react-native-fs';
...
  const downloadBook = async (url: string) => {
    const documentDirectoryPath = RNFS.DocumentDirectoryPath; // `RNFS.DocumentDirectoryPath` exists on both platforms and is writable
    let filename = url.substring(url.lastIndexOf('/')+1);
    let destinationPath = `${documentDirectoryPath}/${filename}`;
    let options = {
      fromUrl: url,
      toFile: destinationPath,
    };
    let response = await RNFS.downloadFile(options);
    response.promise.then(async res => {
      if (res.statusCode == 200) {
        fbreaderViewRef.current.setNativeProps({
          book: destinationPath  
        });
      }
    });
  }

```

## License

PRIVATE
