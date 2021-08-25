import { requireNativeComponent, ViewStyle } from 'react-native';

type FbreaderProps = {
  color: string;
  style: ViewStyle;
};

export const FbreaderViewManager = requireNativeComponent<FbreaderProps>(
'FbreaderView'
);

export default FbreaderViewManager;
