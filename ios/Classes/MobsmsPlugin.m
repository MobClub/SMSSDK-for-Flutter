#import "MobsmsPlugin.h"
#import <SMS_SDK/SMSSDK.h>
#import <SMS_SDK/SMSSDK+ContactFriends.h>


static FlutterMethodChannel* channel = nil;

@implementation MobsmsPlugin


+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  channel = [FlutterMethodChannel
      methodChannelWithName:@"com.mob.smssdk"
            binaryMessenger:[registrar messenger]];
  MobsmsPlugin* instance = [[MobsmsPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}


+ (NSDictionary *)errorToUZDict:(NSError *)error
{
    NSMutableDictionary *dict = nil;
    if(error)
    {
        dict = [NSMutableDictionary new];
        NSString *des = error.userInfo[@"description"];
        NSInteger code = error.code;
        
        if(des)
        {
            dict[@"msg"] = des;
        }
        else
        {
            dict[@"msg"] = error.userInfo[NSLocalizedDescriptionKey];
        }
        dict[@"code"] = @(code);
    }
    
    return dict;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    
  NSDictionary *arguments = [call arguments];

  if ([@"getTextCode" isEqualToString:call.method]) {
    
      NSLog(@"%@", arguments);
      [SMSSDK getVerificationCodeByMethod:SMSGetCodeMethodSMS phoneNumber:arguments[@"phoneNumber"] zone:arguments[@"zone"] template:arguments[@"tempCode"] result:^(NSError *error) {
          
         if(result)
         {
             NSMutableDictionary *dict = [NSMutableDictionary new];
             if(error)
             {
                 dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
             }
             result(dict);
         }
      }];
  }else if ([@"getVoiceCode" isEqualToString:call.method]) {
    
      [SMSSDK getVerificationCodeByMethod:SMSGetCodeMethodVoice phoneNumber:arguments[@"phoneNumber"] zone:arguments[@"zone"] template:arguments[@"tempCode"] result:^(NSError *error) {
          
         if(result)
         {
             NSMutableDictionary *dict = [NSMutableDictionary new];
             if(error)
             {
                 dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
             }
             result(dict);
             
         }
      }];
  }else if ([@"commitCode" isEqualToString:call.method]) {
      
      [SMSSDK commitVerificationCode:arguments[@"code"] phoneNumber:arguments[@"phoneNumber"] zone:arguments[@"zone"] result:^(NSError *error) {
          
          if(result)
          {
              NSMutableDictionary *dict = [NSMutableDictionary new];
              if(error)
              {
                  dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
              }
              result(dict);
              
          }
      }];
  }else if ([@"getSupportedCountries" isEqualToString:call.method]) {
      
      NSLog(@"%@", arguments);
      [SMSSDK getCountryZone:^(NSError *error, NSArray *zonesArray) {
          
          if(result)
          {
              NSMutableDictionary *dict = [NSMutableDictionary new];
              dict[@"err"] = [MobsmsPlugin errorToUZDict:error];

              if(zonesArray)
              {
                dict[@"ret"] = @{@"countries":zonesArray};
              }

              result(dict);
          }
      }];
      
  }else if ([@"getFriends" isEqualToString:call.method]) {
      
      NSLog(@"%@", arguments);
      [SMSSDK getAllContactFriends:^(NSError *error, NSArray *friendsArray) {
          
          if(result)
          {
              NSMutableDictionary *dict = [NSMutableDictionary new];
              dict[@"err"] = [MobsmsPlugin errorToUZDict:error];

              if(friendsArray)
                dict[@"ret"] = @{@"friends":friendsArray};
              
              result(dict);
          }
      }];
      
  }else if ([@"submitUserInfo" isEqualToString:call.method]) {
      
      NSLog(@"%@", arguments);
      
      
      SMSSDKUserInfo *userInfo = [[SMSSDKUserInfo alloc] init];
      if([arguments isKindOfClass:[NSDictionary class]] && arguments.count > 0)
      {
          userInfo.avatar = arguments[@"avatar"];
          userInfo.uid = arguments[@"uid"];
          userInfo.nickname = arguments[@"nickname"];
          userInfo.phone = arguments[@"phoneNumber"];
          userInfo.zone = arguments[@"zone"];
      }
      
      [SMSSDK submitUserInfo:userInfo result:^(NSError *error) {
          NSMutableDictionary *dict = [NSMutableDictionary new];
          if(error)
          {
              dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
          }
          result(dict);
      }];
      
  }else if ([@"getVersion" isEqualToString:call.method]) {
      
      NSDictionary *dict = @{@"ret":@{@"version":[SMSSDK sdkVersion]}};
      result(dict);

      
  }else if ([@"enableWarn" isEqualToString:call.method]) {
      
      BOOL isWarn = [arguments[@"isWarn"] boolValue];
      [SMSSDK enableAppContactFriends:isWarn];
      
      result(@{});
      
      
  }else {
    result(FlutterMethodNotImplemented);
  }
}

@end
