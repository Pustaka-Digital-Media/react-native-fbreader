import { requireNativeComponent, ViewStyle } from 'react-native';

type FBReaderProps = {
  background?: string;
  book?: string;
  colorProfile?: string;
  fontSize?: number;
  searchInText?: string;
  style: ViewStyle;
};

export const FBReaderView =
  requireNativeComponent<FBReaderProps>('FBReaderView');

export default FBReaderView;
