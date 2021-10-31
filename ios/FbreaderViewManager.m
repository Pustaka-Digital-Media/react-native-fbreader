#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(FBReaderViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(book, NSString)

RCT_EXPORT_VIEW_PROPERTY(background, NSString)

RCT_EXPORT_VIEW_PROPERTY(colorProfile, NSString)

RCT_EXPORT_VIEW_PROPERTY(fontSize, NSInteger)

RCT_EXPORT_VIEW_PROPERTY(searchInText, NSString)

RCT_EXPORT_VIEW_PROPERTY(tocReference, NSInteger)

RCT_EXPORT_VIEW_PROPERTY(page, NSInteger)


@end
