# smssdk for Flutter

为开发者提供全球通用的短信验证码工具，开发者可以用其在App植入短信验证码SDK、简单设置即可短信验证，集成快速便捷，且后期易于管理

## 开始

1.Flutter集成文档 [SMSSDK-For-Flutter 在线文档](https://pub.dartlang.org/packages/mob_smssdk#-installing-tab-)

2.iOS平台配置参考 [iOS集成文档](http://wiki.mob.com/快速集成-11/)

- 实现 "一、注册应用获取appKey 和 appSecret"
- 实现 "三、配置appkey和appSecret"

##Android平台集成
####导入SMSSDK相关依赖
1. 在项目根目录的build.gradle中添加以下代码：

```
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.1'
        **classpath 'com.mob.sdk:MobSDK:+'**
    }
```

2. 在app/build.gradle中添加以下代码：

```
apply plugin: 'com.android.application'
apply from: "$flutterRoot/packages/flutter_tools/gradle/flutter.gradle"
// 导入MobSDK
**apply plugin: 'com.mob.sdk'**
```

3. 在pubspec.yaml文件中添加smssdk flutter插件：

```
flutter:
  # This section identifies this Flutter project as a plugin project.
  # The androidPackage and pluginClass identifiers should not ordinarily
  # be modified. They are used by the tooling to maintain consistency when
  # adding or updating assets for this project.
  plugin:
    androidPackage: cn.smssdk.flutter
    pluginClass: SmssdkPlugin
```

####添加代码
1. 在MainActivity的onCreate中添加以下代码：

```
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    // 注册SMSSDK Flutter插件
    **SmssdkPlugin.registerWith(registrarFor(SmssdkPlugin.CHANNEL));**
    // 初始化SMSSDK
    **MobSDK.init(this, MOB_APPKEY, MOB_APPSECRET);**
  }
```

2. 在MainActivity的onDestory中添加以下代码：

```
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 执行回收操作
		**SmssdkPlugin.recycle();**
	}
```


