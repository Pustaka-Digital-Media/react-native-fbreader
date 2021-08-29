import { requireNativeComponent, ViewStyle } from 'react-native';

type FBReaderProps = {
  background?: string;
  book?: string;
  textColor?: string;
  fontSize?: number;
  style: ViewStyle;
};

export const FBReaderView =
  requireNativeComponent<FBReaderProps>('FBReaderView');

export default FBReaderView;
