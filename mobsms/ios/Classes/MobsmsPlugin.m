#import "MobsmsPlugin.h"
#import <SMS_SDK/SMSSDK.h>
#import <MobFoundation/MobFoundation.h>
#import <SMS_SDK/SMSSDKAuthToken.h>
#import <SMS_SDK/SMSSDK+ContactFriends.h>


static FlutterMethodChannel* channel = nil;
static SMSSDKAuthToken *authToken = nil;

@implementation MobsmsPlugin


+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    channel = [FlutterMethodChannel methodChannelWithName:@"com.mob.smssdk.channel"
                                          binaryMessenger:[registrar messenger]];
    MobsmsPlugin* instance = [[MobsmsPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}


+ (NSDictionary *)errorToUZDict:(NSError *)error
{
    NSMutableDictionary *dict = nil;
    if(error) {
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
        [SMSSDK getVerificationCodeByMethod:SMSGetCodeMethodSMS
                                phoneNumber:arguments[@"phoneNumber"]
                                       zone:arguments[@"zone"]
                                   template:arguments[@"tempCode"]
                                     result:^(NSError *error) {
            if(result) {
                NSMutableDictionary *dict = [NSMutableDictionary new];
                if(error) {
                    dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
                }
                result(dict);
            }
        }];
    }else if ([@"getVoiceCode" isEqualToString:call.method]) {
        [SMSSDK getVerificationCodeByMethod:SMSGetCodeMethodVoice
                                phoneNumber:arguments[@"phoneNumber"]
                                       zone:arguments[@"zone"]
                                   template:arguments[@"tempCode"]
                                     result:^(NSError *error) {
            
            if(result) {
                NSMutableDictionary *dict = [NSMutableDictionary new];
                if(error) {
                    dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
                }
                result(dict);
            }
        }];
    }else if ([@"commitCode" isEqualToString:call.method]) {
        [SMSSDK commitVerificationCode:arguments[@"code"]
                           phoneNumber:arguments[@"phoneNumber"]
                                  zone:arguments[@"zone"]
                                result:^(NSError *error) {
            
            if(result) {
                NSMutableDictionary *dict = [NSMutableDictionary new];
                if(error) {
                    dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
                }
                result(dict);
            }
        }];
    }else if ([@"getSupportedCountries" isEqualToString:call.method]) {
        NSLog(@"%@", arguments);
        [SMSSDK getCountryZone:^(NSError *error, NSArray *zonesArray) {
            if(result) {
                NSMutableDictionary *dict = [NSMutableDictionary new];
                dict[@"err"] = [MobsmsPlugin errorToUZDict:error];
                if(zonesArray) {
                    dict[@"ret"] = @{@"countries":zonesArray};
                }
                result(dict);
            }
        }];
        
    }else if ([@"getFriends" isEqualToString:call.method]) {
        NSLog(@"%@", arguments);
        [SMSSDK getAllContactFriends:^(NSError *error, NSArray *friendsArray) {
            if(result) {
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
        if([arguments isKindOfClass:[NSDictionary class]]
           && arguments.count > 0) {
            userInfo.avatar = arguments[@"avatar"];
            userInfo.uid = arguments[@"uid"];
            userInfo.nickname = arguments[@"nickname"];
            userInfo.phone = arguments[@"phoneNumber"];
            userInfo.zone = arguments[@"zone"];
        }
        
        [SMSSDK submitUserInfo:userInfo
                        result:^(NSError *error) {
            NSMutableDictionary *dict = [NSMutableDictionary new];
            if(error) {
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
    } else if ([@"getToken" isEqualToString:call.method]) {
        [SMSSDK getMobileAuthTokenWith:^(SMSSDKAuthToken *model, NSError *error) {
            NSMutableDictionary *ret = [NSMutableDictionary dictionary];
            
            if (authToken) authToken = nil;
            
            if (model
                && [model isKindOfClass:[SMSSDKAuthToken class]]) {
                NSDictionary *result = @{
                    @"token": model.token,
                    @"opToken": model.opToken,
                    @"operator": model.operatorType
                };
                
                authToken = model;
                [ret setObject:result forKey:@"ret"];
            } else {
                NSDictionary *errDict = [MobsmsPlugin errorToUZDict:error];
                if (errDict) [ret setObject:errDict forKey:@"err"];
            }
            result(ret);
        }];
    } else if ([@"login" isEqualToString:call.method]) {
        NSLog(@"%@", arguments);
        if (!authToken
            || ![authToken isKindOfClass:[SMSSDKAuthToken class]]) {
            NSError *locErr = [NSError errorWithDomain:@"SMSSDKErrorDomain"
                                                  code:6119165
                                              userInfo:@{@"description": @"Mobile AuthToken is empty!"}];
            return result(@{@"err": [MobsmsPlugin errorToUZDict:locErr]});
        }
        
        if (![[arguments allKeys] containsObject:@"phoneNumber"]
            || ![[arguments objectForKey:@"phoneNumber"] isKindOfClass:[NSString class]]) {
            NSError *locErr = [NSError errorWithDomain:@"SMSSDKErrorDomain"
                                                  code:6119165
                                              userInfo:@{@"description": @"MobileVerify PhoneNumber is empty!"}];
            return result(@{@"err": [MobsmsPlugin errorToUZDict:locErr]});
        }
        
        NSString *phoneNumber = [arguments objectForKey:@"phoneNumber"];
        [SMSSDK verifyMobileWithPhone:phoneNumber
                                token:authToken
                           completion:^(BOOL isValid, NSError *error) {
            // Set AuthToken To Nil
            authToken = nil;
            
            NSMutableDictionary *mDict = [NSMutableDictionary dictionary];
            if (error
                && [error isKindOfClass:[NSError class]]) {
                [mDict setObject:[MobsmsPlugin errorToUZDict:error]
                          forKey:@"err"];
            } else {
                [mDict setObject:@{@"success": @(isValid)}
                          forKey:@"ret"];
            }
            
            result([mDict copy]);
        }];
    } else if ([@"uploadPrivacyStatus" isEqualToString:call.method]) {
        NSLog(@"%@", arguments);
        BOOL status = NO;
        if ([[arguments allKeys] containsObject:@"status"]
            || [[arguments objectForKey:@"status"] respondsToSelector:@selector(boolValue)]) {
            status = [[arguments objectForKey:@"status"] boolValue];
        }

        [MobSDK uploadPrivacyPermissionStatus:status onResult:^(BOOL success) {
            result(@{@"success": @(success)});
        }];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

@end
