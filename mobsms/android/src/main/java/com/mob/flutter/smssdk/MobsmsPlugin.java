package com.mob.flutter.smssdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.mob.MobSDK;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SPHelper;
import cn.smssdk.wrapper.TokenVerifyResult;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.src.main.java.com.mob.flutter.smssdk.impl.SMSSDKLog;

import androidx.annotation.NonNull;

/** MobsmsPlugin */
public class MobsmsPlugin implements FlutterPlugin, MethodCallHandler {
	private static final String TAG = "MobsmsPlugin";
	public static final String CHANNEL = "com.mob.smssdk.channel";
	private static final String KEY_CODE = "code";
	private static final String KEY_MSG = "msg";
	private static final int BRIDGE_ERR = 700;
	private static final String ERROR_INTERNAL = "Flutter bridge internal error: ";
	private TokenVerifyResult tokenVerifyResult;
	private MethodChannel methodChannel;

	public MobsmsPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				MobSDK.setChannel(new com.mob.commons.SMSSDK(),MobSDK.CHANNEL_FLUTTER);
			}
		}).start();
	}

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
	  SMSSDKLog.d("registerWith() called");
	  final MobsmsPlugin instance = new MobsmsPlugin();
	  instance.onAttachedToEngine(registrar.context(), registrar.messenger());
  }

  @Override
  public void onMethodCall(MethodCall call, final Result rst) {
	  SMSSDKLog.d("onMethodCall. method: " + call.method);
	  switch (call.method) {
		  case "getTextCode":
			  getTextCode(call, rst);
		  	break;
		  case "getVoiceCode":
			  getVoiceCode(call, rst);
		  	break;
		  case "commitCode":
			  commitCode(call, rst);
		  	break;
		  case "getSupportedCountries":
			  getSupportedCountries(call, rst);
			  break;
		  case "login":
			  login(call, rst);
			  break;
		  case "getToken":
			  getToken(call, rst);
			  break;
		  case "submitUserInfo":
			  submitUserInfo(call, rst);
			  break;
		  case "getVersion":
			  getVersion(call, rst);
			  break;
		  case "enableWarn":
			  enableWarn(call, rst);
			  break;
		  case "uploadPrivacyStatus":
			  uploadPrivacyStatus(call, rst);
			  break;
		  default:
			  rst.notImplemented();
			break;
	  }
  }

	private void uploadPrivacyStatus(MethodCall call, Result rst) {
		if (call.hasArgument("status")) {
			Boolean grantResult = call.argument("status");
			MobSDK.submitPolicyGrantResult(grantResult, null);
		}
	}

	private void enableWarn(MethodCall call, Result rst) {
		boolean isWarn = call.argument("isWarn");
		SMSSDKLog.d("isWarn: " + isWarn);
		SPHelper.getInstance().setWarnWhenReadContact(isWarn);
		Map<String, Object> map = new HashMap<>();
		onSuccess(rst, map);
	}

	private void getVersion(MethodCall call, Result rst) {
		String version = SMSSDK.getVersion();
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		onSuccess(rst, map);
	}

	private void submitUserInfo(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
						// callback onSuccess
						// data示例：{}
						Map<String, Object> map = new HashMap<String, Object>();
						onSuccess(rst, map);
					}
				} else {
					if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("submitUserInfo() internal error: " + msg);
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

		String zone = call.argument("country");
		String phoneNumber = call.argument("phone");
		String uid = call.argument("uid");
		String nickname = call.argument("nickname");
		String avatar = call.argument("avatar");

		SMSSDKLog.d("zone: " + zone);
		SMSSDKLog.d("phoneNumber: " + phoneNumber);
		SMSSDKLog.d("uid: " + uid);
		SMSSDKLog.d("nickname: " + nickname);
		SMSSDKLog.d("avatar: " + avatar);
		SMSSDK.submitUserInfo(uid, nickname, avatar, zone, phoneNumber);
	}

	private void getToken(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE) {
						tokenVerifyResult = (TokenVerifyResult) data;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("opToken",tokenVerifyResult.getOpToken());
						map.put("token",tokenVerifyResult.getToken());
						map.put("operator",tokenVerifyResult.getOperator());
						onSuccess(rst,map);
					}
				} else {
					if (event == SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("getToken() internal error: " + msg);
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
		SMSSDK.getToken();
	}

	private void login(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_VERIFY_LOGIN) {
						tokenVerifyResult = null;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("success",true);
						onSuccess(rst,map);
					}
				} else {
					if (event == SMSSDK.EVENT_VERIFY_LOGIN) {
						// callback onError
						tokenVerifyResult = null;
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("login() internal error: " + msg);
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
		if (tokenVerifyResult == null){
			try {
				JSONObject errorJson = new JSONObject();
				errorJson.putOpt("detail","请先调用获取token方法");
				onSdkError(rst,errorJson.toString());
			} catch (JSONException e) {
			}
		} else {
			SMSSDK.login(phoneNumber,tokenVerifyResult);
		}
	}

	private void getSupportedCountries(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// callback onSuccess
						// data示例：[{zone=590, rule=^\d+}, {zone=680, rule=^\d+}]
						ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)data;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("countries", list);
						onSuccess(rst, map);
					}
				} else {
					if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("getSupportedCountries() internal error: " + msg);
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

		SMSSDK.getSupportedCountries();
	}

	private void commitCode(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// callback onSuccess
						// data示例：{country=86, phone=13362206853}
						HashMap<String, Object> dataMap = (HashMap<String, Object>)data;
						Map<String, Object> map = new HashMap<>();
						onSuccess(rst, map);
					}
				} else {
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("commitCode() internal error: " + msg);
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
		String code = call.argument("code");
		SMSSDKLog.d("zone: " + zone);
		SMSSDKLog.d("phoneNumber: " + phoneNumber);
		SMSSDKLog.d("code: " + code);
		SMSSDK.submitVerificationCode(zone, phoneNumber, code);
	}

	private void getVoiceCode(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
						// callback onSuccess
						// 此接口data=null
						Map<String, Object> map = new HashMap<>();
						onSuccess(rst, map);
					}
				} else {
					if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
						// callback onError
						if (data instanceof Throwable) {
							Throwable throwable = (Throwable) data;
							String msg = throwable.getMessage();
							onSdkError(rst, msg);
						} else {
							String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
							SMSSDKLog.e("getVoiceCode() internal error: " + msg);
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
		SMSSDKLog.d("zone: " + zone);
		SMSSDKLog.d("phoneNumber: " + phoneNumber);
		SMSSDK.getVoiceVerifyCode(zone, phoneNumber);
	}

	private void getTextCode(MethodCall call, final Result rst) {
		// 注册监听器
		EventHandler callback = new EventHandler() {
			@Override
			public void afterEvent(final int event, final int result, final Object data) {
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

	public static void recycle() {
  	SMSSDK.unregisterAllEventHandler();
  }

  private void onSuccess(final Result result, Map<String, Object> ret) {
	  final Map<String, Object> map = new HashMap<>();
	  map.put("ret", ret);
	  new Handler(Looper.getMainLooper()).post(new Runnable() {
		  @Override
		  public void run() {
			  try {
			  	result.success(map);
			  }catch (IllegalStateException e){
				  // ignore
				  e.printStackTrace();
			  }
		  }
	  });
  }

  private void onSdkError(final Result result, String error) {
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

		  final Map<String, Object> map = new HashMap<>();
		  map.put("err", errMap);
		  new Handler(Looper.getMainLooper()).post(new Runnable() {
			  @Override
			  public void run() {
				  try {
					  result.success(map);
				  }catch (IllegalStateException e){
					  // ignore
					  e.printStackTrace();
				  }
			  }
		  });
	  } catch (JSONException e) {
		  SMSSDKLog.e("Smssdk Flutter plugin internal error. msg= " + e.getMessage(), e);
		  onInternalError(result,"Generate JSONObject error");
	  }


  }

	private void onInternalError(final Result result, String errMsg) {
		Map<String, Object> errMap = new HashMap<>();
		errMap.put(KEY_CODE, BRIDGE_ERR);
		errMap.put(KEY_MSG, ERROR_INTERNAL + errMsg);

		final Map<String, Object> map = new HashMap<>();
		map.put("err", errMap);
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				try {
					result.success(map);
				}catch (IllegalStateException e){
					// ignore
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onAttachedToEngine(@NonNull @NotNull FlutterPluginBinding binding) {
		onAttachedToEngine(binding.getApplicationContext(),binding.getBinaryMessenger());
	}

	@Override
	public void onDetachedFromEngine(@NonNull @NotNull FlutterPluginBinding binding) {

	}

	private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger){
		methodChannel = new MethodChannel(messenger, CHANNEL);
		methodChannel.setMethodCallHandler(this);
	}
}
