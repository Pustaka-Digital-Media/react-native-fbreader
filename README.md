# react-native-fbreader

RN plugin for FBReader SDK

## Installation

```sh
npm install Pustaka-Digital-Media/react-native-fbreader
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

**fontSize** sample using states:
```javascript

const [fontSize, setFontSize] = useState(10);

...

<FBReaderView fontSize={fontSize} />

<Button title="Increase Font Size" onPress={ ()=>{ setFontSize(fontSize + 2) } } />
<Button title="Decrease Font Size" onPress={ ()=>{ setFontSize(fontSize - 2) } } />
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

## Events
There is a single `FBReaderViewContentUpdateEvent` event emitted via `DeviceEventEmitter` which sends current page, total pages and chapter info.
```js
  const nav = useNavigation();
  useEffect(() => {
    DeviceEventEmitter.addListener('FBReaderViewContentUpdateEvent', (map: any) => {
      nav.setOptions({
        title: `Page ${map.page}, Total: ${map.total}`
      })
    });
  })

```
Chapter info is list of current chapter hierarchy ordered by level (subtitle x -> subtitle y -> ... -> root), e.g:
```
{"chapter":[{"level":1,"name":"II The Pool of Tears"},{"level":0,"name":"..."}]
```
means:
* Level 1 chapter "II Pool of Tears"
* Level 0 chapter - root of TOC.


## FBReader license key
To provide license key for Android app add to your app `gradle.build` file:
```
...
buildTypes {
  ...
  all {
    resValue 'string', 'fbreader_sdk_key', '<put your fbreader sdk key here>'
  }
}
...
```

## Android Setup
Add to your `android/build.gradle` file in `allprojects\repositories` section new maven URL `https://sdk.fbreader.org/maven`:
```
allprojects {
    repositories {
        maven {
            url "https://sdk.fbreader.org/maven"
        }
```

## iOS Setup
Go to iOS project folder:
```
cd ios
```
Edit Podfile file:
* Comment out Flipper usage
* Add new pod dependency
```
pod 'FBReaderSDK', :git => 'https://sdk.fbreader.org/git/FBReaderSDK.git'
```

And install all Cocoapods dependencies:
```
pod install
```

Open your Xcode workspace project:
```
open <your project>.xcworkspace
```

Copy and paste from sample iOS Xcode workspace project to your Xcode workspace project:
* `defaults` file folder
* `backgroud` image folder in `Images.xcassets` to your project `Images.xcassets`

**LICENSE KEY**: Put your key into into Info.plist file with key 'fbreader_sdk_key'.

## License

PRIVATE
