package com.example.smssdk;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.SMSSDK;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** SmssdkPlugin */
public class SmssdkPlugin implements MethodCallHandler {
	private static final String TAG = "SmssdkPlugin";
	public static final String CHANNEL = "com.yoozoo.com/smssdk";

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
  	Log.d(TAG, "registerWith() called");
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
    channel.setMethodCallHandler(new SmssdkPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
  	Log.d(TAG, "onMethodCall. method: " + call.method);
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getVersion")) {
		String version = SMSSDK.getVersion();
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		onSuccess(result, map);
	} else {
      result.notImplemented();
    }
  }

  public static void recycle() {
  	SMSSDK.unregisterAllEventHandler();
  }

  private void onSuccess(Result result, Map<String, Object> ret) {
	  Map<String, Object> map = new HashMap<>();
	  map.put("ret", ret);
	  result.success(map);
  }
}
