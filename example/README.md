# mobsms_example

**supported original mob_smssdk minimum version:**

-  iOS - v3.0.0
-  Android - v3.0.0

## Getting Started


-  import library

```
import 'package:mobsms/mobsms.dart';
```

-  getTextCode

```
Smssdk.getTextCode("手机号","区号(中国填写86)","模板id", (dynamic ret, Map err){
   if(err!=null){
   
   }
   else
   {
                    
   }
});
```

-  getVoiceCode

```
Smssdk.getVoiceCode("手机号","区号(中国填写86)", (dynamic ret, Map err){
    if(err!=null)
    {
    }
    else
    {
                    
    }
});
```


-  commitCode

```
Smssdk.commitCode("手机号","区号(中国填写86)","验证码", (dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
                    
   }
});
```

-  getSupportedCountries

```
Smssdk.getSupportedCountries((dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
   }
});
```

-  getFriends

```
Smssdk.getFriends((dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
   }
});
```

-  submitUserInfo

```
Smssdk.submitUserInfo("3241241", "SmsSDK_Flutter_User_3241241",
              "http://download.sdk.mob.com/510/deb/0c0731ac543eb71311c482a2e2.png",
                    "区号(中国填写86)", "手机号", (dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
   }
});
```

-  getVersion

```

Smssdk.getVersion((dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
   }
});
```

-  enableWarn

```
Smssdk.enableWarn(true,(dynamic ret, Map err){
   if(err!=null)
   {
   }
   else
   {
   }
});
```