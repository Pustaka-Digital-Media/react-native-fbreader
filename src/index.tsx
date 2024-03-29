import { requireNativeComponent, ViewStyle, NativeModules } from 'react-native';

const _FBReader = NativeModules.FBReader;

type FBReaderViewProps = {
  background?: string;
  book?: string;
  colorProfile?: string;
  fontSize?: number;
  searchInText?: string;
  textColor?: string;
  tocReference?: number;
  page?: number;
  style?: ViewStyle;
};

export const FBReaderView =
  requireNativeComponent<FBReaderViewProps>('FBReaderView');

type FBReaderCoverViewProps = {
  book?: string;
  style?: ViewStyle;
}

export const FBReaderCoverView =
  requireNativeComponent<FBReaderCoverViewProps>('FBReaderCoverView');

class FBReaderImpl {
  constructor() {}

  async tableOfContents(book: string) {
    return await _FBReader.tableOfContents(book);
  }

  goToPage(path: string, page: number) {
    return _FBReader.goToPage(path, page)
  }
}

export const FBReader = new FBReaderImpl();

export default { FBReader, FBReaderView, FBReaderCoverView };
