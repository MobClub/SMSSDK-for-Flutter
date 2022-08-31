package android.src.main.java.com.mob.flutter.smssdk.impl;

import android.util.Log;

public class SMSSDKLog {
	private static final String TAG = "SMS_Flutter";

	public static int d(String msg) {
		return Log.d(TAG, msg);
	}

	public static int d(String msg, Throwable t) {
		return Log.d(TAG, msg, t);
	}

	public static int i(String msg) {
		return Log.i(TAG, msg);
	}

	public static int i(String msg, Throwable t) {
		return Log.i(TAG, msg, t);
	}

	public static int w(String msg) {
		return Log.w(TAG, msg);
	}

	public static int w(String msg, Throwable t) {
		return Log.w(TAG, msg, t);
	}

	public static int e(String msg) {
		return Log.e(TAG, msg);
	}

	public static int e(String msg, Throwable t) {
		return Log.e(TAG, msg, t);
	}
}
