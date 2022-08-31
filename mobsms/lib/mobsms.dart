import 'dart:async';
import 'package:flutter/services.dart';

class Smssdk {
  static const MethodChannel _channel =
      const MethodChannel('com.mob.smssdk.channel');

  static void submitPrivacyGrantResult(bool status) {
    final Map<String, bool> params = {'status': status};
    _channel.invokeMethod("uploadPrivacyStatus", params);
  }

  static Future getTextCode(String phoneNumber, String zone, String tempCode,
      Function(dynamic ret, Map? err) result) {
    Map args = {"phoneNumber": phoneNumber, "zone": zone, "tempCode": tempCode};

    Future<dynamic> callback = _channel.invokeMethod('getTextCode', args);

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future getVoiceCode(
      String phoneNumber, String zone, Function(dynamic ret, Map? err) result) {
    Map args = {"phoneNumber": phoneNumber, "zone": zone};

    Future<dynamic> callback = _channel.invokeMethod('getVoiceCode', args);
    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future commitCode(String phoneNumber, String zone, String code,
      Function(dynamic ret, Map? err) result) {
    Map args = {"phoneNumber": phoneNumber, "zone": zone, "code": code};
    Future<dynamic> callback = _channel.invokeMethod('commitCode', args);

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future getSupportedCountries(Function(dynamic ret, Map? err) result) {
    Future<dynamic> callback = _channel.invokeMethod('getSupportedCountries');
    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future getToken(Function(dynamic ret, Map? err) result) {
    Future<dynamic> callback = _channel.invokeMethod('getToken');
    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future login(
      String phoneNumber, Function(dynamic ret, Map? err) result) {
    Map args = {"phoneNumber": phoneNumber};

    Future<dynamic> callback = _channel.invokeMethod('login', args);

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future getFriends(Function(dynamic ret, Map? err) result) {
    Future<dynamic> callback = _channel.invokeMethod('getFriends');

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future submitUserInfo(String uid, String nickname, String avatar,
      String zone, String phoneNumber, Function(dynamic ret, Map? err) result) {
    Map userInfo = {
      "country": zone,
      "phone": phoneNumber,
      "uid": uid,
      "nickname": nickname,
      "avatar": avatar
    };

    Future<dynamic> callback =
        _channel.invokeMethod('submitUserInfo', userInfo);

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future getVersion(Function(dynamic ret, Map? err) result) {
    Future<dynamic> callback = _channel.invokeMethod('getVersion');

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }

  static Future enableWarn(
      bool enableWarn, Function(dynamic ret, Map? err) result) {
    Map args = {"isWarn": enableWarn};

    Future<dynamic> callback = _channel.invokeMethod('enableWarn', args);

    callback.then((dynamic response) {
      if (response is Map) {
        result(response["ret"], response["err"]);
      } else {
        result(null, null);
      }
    });

    return callback;
  }
}
