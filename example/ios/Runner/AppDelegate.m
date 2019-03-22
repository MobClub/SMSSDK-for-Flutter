#include "AppDelegate.h"
#include "GeneratedPluginRegistrant.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.
    
    NSArray *languages = [NSBundle mainBundle].preferredLocalizations;
    if (languages.count)
    {
        NSString *firstStr = languages.firstObject;
        if([firstStr isEqualToString:@"zh"]
           || [firstStr isEqualToString:@"zh-Hans"])
        {
        }
    }
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
