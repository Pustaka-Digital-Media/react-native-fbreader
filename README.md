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

// epub rendering view
<FBReaderView  
  book={filePathToBook}
  textColor="#333"
  fontSize="12"
  background="wood"/>

// epub cover view
<FBReaderCoverView
  book={filePathToBook} />


// epub table of contents - JSON object
let content = FBReader.tableOfContents(filePathToBook);
```

## FBReaderView

`FBReaderView` properties definition:
```js
type FBReaderViewProps = {
  background?: string; // hardpaper, leather, paper, sand, sepia, wood
  book?: string;
  colorProfile?: string;
  fontSize?: number;
  searchInText?: string;
  tocReference?: number; // reference of TOC (see FBReader.tableOfContents)
  style?: ViewStyle;
};
```

## License

PRIVATE
