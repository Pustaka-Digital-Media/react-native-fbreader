#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(FBReader, NSObject)

RCT_EXTERN_METHOD(tableOfContents:(NSString*)path
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

@end
