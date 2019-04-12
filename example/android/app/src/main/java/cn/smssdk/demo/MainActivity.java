package cn.smssdk.demo;

import android.os.Bundle;

import com.mob.MobSDK;

import cn.smssdk.flutter.MobsmsPlugin;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
	// 通过Mob console申请获得
	private static final String MOB_APPKEY = "moba6b6c6d6";
	private static final String MOB_APPSECRET = "b89d2427a3bc7ad1aea1e1e8c1d36bf3";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    // 注册SMSSDK Flutter插件
    MobsmsPlugin.registerWith(registrarFor(MobsmsPlugin.CHANNEL));
    // 初始化SMSSDK
	MobSDK.init(this, MOB_APPKEY, MOB_APPSECRET);
  }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 执行回收操作
		MobsmsPlugin.recycle();
	}
}
