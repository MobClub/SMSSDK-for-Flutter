# SMSSDK For Flutter

为开发者提供全球通用的短信验证码工具，开发者可以用其在App植入短信验证码SDK、简单设置即可短信验证，集成快速便捷，且后期易于管理

## 开始

1.Flutter集成文档 [SMSSDK-For-Flutter 在线文档](https://pub.dartlang.org/packages/mobsms#-installing-tab-)

2.iOS平台配置参考 [iOS集成文档](http://wiki.mob.com/快速集成-11/)

- 实现 "一、注册应用获取appKey 和 appSecret"
- 实现 "三、配置appkey和appSecret"

3.Android平台集成

#####导入SMSSDK相关依赖
1. 在项目根目录的build.gradle中添加以下代码：

```
    dependencies {
        classpath 'com.android.tools.build:gradle:3.2.1'
        **classpath 'com.mob.sdk:MobSDK:+'**
    }
```

2. 在/android/app/build.gradle中添加以下代码：

```
apply plugin: 'com.android.application'
apply from: "$flutterRoot/packages/flutter_tools/gradle/flutter.gradle"
// 导入MobSDK
**apply plugin: 'com.mob.sdk'**
```

3. 在根路径下的pubspec.yaml文件中添加smssdk flutter插件：

```
dependencies:
  mobsms:
```

在你项目的Dart中添加以下代码：

```
 import 'package:mobsms/mobsms.dart'
```

这样，就可以使用plugin中定义的dart api了。

4. 平台相关集成
在项目的/android/app/build.gradle中添加:

```
android {
    // lines skipped
    dependencies {
        provided rootProject.findProject(":mobsms")
    }
}
```

这样就可以在你的`project/android/src`下的类中`import cn.smssdk.flutter.MobsmsPlugin`并使用`MobsmsPlugin`中的api了。


######添加代码
1. 在MainActivity的onCreate中添加以下代码：

```
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    // 注册SMSSDK Flutter插件
    **MobsmsPlugin.registerWith(registrarFor(MobsmsPlugin.CHANNEL));**
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
		**MobsmsPlugin.recycle();**
	}
```

## 技术支持
如有问题请联系技术支持:
```
服务电话:   400-685-2216     
QQ:        4006852216
节假日值班电话:
    iOS：185-1664-1951
Android: 185-1664-1950
电子邮箱:   support@mob.com
市场合作:   021-54623100
```