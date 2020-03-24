package cn.smssdk.demo;

import androidx.annotation.NonNull;

import cn.smssdk.flutter.MobsmsPlugin;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
	@Override
	public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
		GeneratedPluginRegistrant.registerWith(flutterEngine);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MobsmsPlugin.recycle();
	}
}
