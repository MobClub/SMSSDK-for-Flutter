package com.example.smssdk;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** SmssdkPlugin */
public class SmssdkPlugin implements MethodCallHandler {
	private static final String TAG = "SmssdkPlugin";
	public static final String CHANNEL = "com.mob.smssdk";
	private static final String KEY_CODE = "code";
	private static final String KEY_MSG = "msg";
	private static final int BRIDGE_ERR = 700;
	private static final String ERROR_INTERNAL = "Flutter bridge internal error: ";

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
  	SMSSDKLog.d("registerWith() called");
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
    channel.setMethodCallHandler(new SmssdkPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, final Result rst) {
	  SMSSDKLog.d("onMethodCall. method: " + call.method);
    if (call.method.equals("getPlatformVersion")) {
      rst.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getVersion")) {
		String version = SMSSDK.getVersion();
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		onSuccess(rst, map);
	}
	else if (call.method.equals("getTextCode")) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				SMSSDKLog.d("afterEvent");
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						boolean smart = (Boolean)data;
						// callback onSuccess
						Map<String, Object> map = new HashMap<>();
						map.put("smart", smart);
						onSuccess(rst, map);
					}
				} else {
					if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("getTextCode() internal error: " + msg);
							onInternalError(rst, msg);
						}
					}
				}
			}
		};
		// Flutter的Result对象只能返回一次数据，同一个Result对象如果再次提交数据会crash（错误信息：数据已被提交过），所以要把前一次的EventHandler注销掉
		// 否则重复调用统一个接口时，smssdk会针对所有EventHandler发送回调，旧的Result对象就会被触发，导致Flutter层crash
		SMSSDK.unregisterAllEventHandler();
		SMSSDK.registerEventHandler(callback);

    	String phoneNumber = call.argument("phoneNumber");
    	String zone = call.argument("zone");
    	String tempCode = call.argument("tempCode");
		SMSSDKLog.d("tempCode: " + tempCode);
		SMSSDKLog.d("zone: " + zone);
		SMSSDKLog.d("phoneNumber: " + phoneNumber);
		SMSSDK.getVerificationCode(tempCode, zone, phoneNumber);
	}
	else {
      rst.notImplemented();
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

  private void onSdkError(Result result, String error) {
	  try {
		  JSONObject errorJson = new JSONObject(error);
		  int code = errorJson.optInt("status");
		  String msg = errorJson.optString("detail");
		  if (TextUtils.isEmpty(msg)) {
			  msg = errorJson.optString("error");
		  }

		  Map<String, Object> errMap = new HashMap<>();
		  errMap.put(KEY_CODE, code);
		  errMap.put(KEY_MSG, msg);

		  Map<String, Object> map = new HashMap<>();
		  map.put("err", errMap);
		  result.success(map);
	  } catch (JSONException e) {
		  SMSSDKLog.e("Smssdk Flutter plugin internal error. msg= " + e.getMessage(), e);
		  onInternalError(result,"Generate JSONObject error");
	  }


  }

	private void onInternalError(Result result, String errMsg) {
		Map<String, Object> errMap = new HashMap<>();
		errMap.put(KEY_CODE, BRIDGE_ERR);
		errMap.put(KEY_MSG, ERROR_INTERNAL + errMsg);

		Map<String, Object> map = new HashMap<>();
		map.put("err", errMap);
		result.success(map);
	}
}
