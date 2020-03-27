package cn.smssdk.flutter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SPHelper;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * MobsmsPlugin
 */
public class MobsmsPlugin implements FlutterPlugin, MethodCallHandler {
    private static final String TAG = "MobsmsPlugin";
    public static final String CHANNEL = "com.mob.smssdk";
    private static final String KEY_CODE = "code";
    private static final String KEY_MSG = "msg";
    private static final int BRIDGE_ERR = 700;
    private static final String ERROR_INTERNAL = "Flutter bridge internal error: ";
    private MethodChannel methodChannel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        this.onAttachedToEngine(flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(BinaryMessenger messenger) {
        methodChannel = new MethodChannel(messenger, CHANNEL);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
        SMSSDK.unregisterAllEventHandler();
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        SMSSDKLog.d("registerWith() called");
        MobsmsPlugin mobsmsPlugin = new MobsmsPlugin();
        mobsmsPlugin.onAttachedToEngine(registrar.messenger());
    }

    @Override
    public void onMethodCall(MethodCall call, final Result rst) {
        SMSSDKLog.d("onMethodCall. method: " + call.method);
        switch (call.method) {
            case "getTextCode": {
                // 注册监听器
                EventHandler callback = new EventHandler() {
                    @Override
                    public void afterEvent(final int event, final int result, final Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                boolean smart = (Boolean) data;
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
                break;
            }
            case "getVoiceCode": {
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
                break;
            }
            case "commitCode": {
                // 注册监听器
                EventHandler callback = new EventHandler() {
                    @Override
                    public void afterEvent(final int event, final int result, final Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                // callback onSuccess
                                // data示例：{country=86, phone=13362206853}
                                HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
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
                break;
            }
            case "getSupportedCountries": {
                // 注册监听器
                EventHandler callback = new EventHandler() {
                    @Override
                    public void afterEvent(final int event, final int result, final Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                // callback onSuccess
                                // data示例：[{zone=590, rule=^\d+}, {zone=680, rule=^\d+}]
                                ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) data;
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
                break;
            }
            case "getFriends": {
                // 注册监听器
                EventHandler callback = new EventHandler() {
                    @Override
                    public void afterEvent(final int event, final int result, final Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_FRIENDS_IN_APP) {
                                // callback onSuccess
                                /* data示例：[{uid=1155310877, phone=17301652905, nickname=SmsSDK_User_1155310877,
                                 *     avatar=http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg, isnew=true}]
                                 */
                                ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) data;
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("friends", list);
                                onSuccess(rst, map);
                            }
                        } else {
                            if (event == SMSSDK.EVENT_GET_FRIENDS_IN_APP) {
                                // callback onError
                                if (data instanceof Throwable) {
                                    Throwable throwable = (Throwable) data;
                                    String msg = throwable.getMessage();
                                    onSdkError(rst, msg);
                                } else {
                                    String msg = "Sdk returned 'RESULT_ERROR', but the data is NOT an instance of Throwable";
                                    SMSSDKLog.e("getFriends() internal error: " + msg);
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

                SMSSDK.getFriendsInApp();
                break;
            }
            case "submitUserInfo": {
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
                break;
            }
            case "getVersion": {
                String version = SMSSDK.getVersion();
                Map<String, Object> map = new HashMap<>();
                map.put("version", version);
                onSuccess(rst, map);
                break;
            }
            case "enableWarn": {
                boolean isWarn = call.argument("isWarn");
                SMSSDKLog.d("isWarn: " + isWarn);
                SPHelper.getInstance().setWarnWhenReadContact(isWarn);
                Map<String, Object> map = new HashMap<>();
                onSuccess(rst, map);
                break;
            }
            default:
                rst.notImplemented();
                break;
        }
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
                result.success(map);
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
                    result.success(map);
                }
            });
        } catch (JSONException e) {
            SMSSDKLog.e("Smssdk Flutter plugin internal error. msg= " + e.getMessage(), e);
            onInternalError(result, "Generate JSONObject error");
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
                result.success(map);
            }
        });
    }
}
