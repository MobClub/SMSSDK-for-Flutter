# smssdk_example

**supported original mob_smssdk minimum version:**

-  iOS - v3.0.0
-  Android - v3.0.0

## Getting Started


-  import library

```
import 'package:mob_smssdk/smssdk.dart';
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
Smssdk.getVoiceCode("手机号","区号(中国填写86)","模板id", (dynamic ret, Map err){
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
Map userInfo = {
                  "country":"区号",
                  "phone":"您的手机号",
                  "uid":"用户id",
                  "nickname": "昵称",
                  "avatar":"http://download.sdk.mob.com/510/deb/0c0731ac543eb71311c482a2e2.png",
                  };
Smssdk.submitUserInfo(userInfo,(dynamic ret, Map err){
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