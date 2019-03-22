import 'dart:async';

import 'package:flutter/services.dart';


class Smssdk {

  static const MethodChannel _channel = const MethodChannel('com.mob.smssdk');

  static Future getTextCode(String phoneNumber,String zone,String tempCode, Function(dynamic ret,Map err)result) {

    Map args = {"phoneNumber": phoneNumber, "zone":zone, "tempCode":tempCode};

  	Future<dynamic> callback = _channel.invokeMethod('getTextCode', args);

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }

  static Future getVoiceCode(String phoneNumber,String zone,String tempCode, Function(dynamic ret,Map err)result) {

    Map args = {"phoneNumber": phoneNumber, "zone":zone, "tempCode":tempCode};

  	Future<dynamic> callback = _channel.invokeMethod('getVoiceCode', args);

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }

  static Future commitCode(String phoneNumber,String zone,String code, Function(dynamic ret,Map err)result) {

    Map args = {"phoneNumber": phoneNumber, "zone":zone, "code":code};

  	Future<dynamic> callback = _channel.invokeMethod('commitCode', args);

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }

  static Future getSupportedCountries(Function(dynamic ret,Map err)result) {


  	Future<dynamic> callback = _channel.invokeMethod('getSupportedCountries');

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }


  static Future getFriends(Function(dynamic ret,Map err)result) {


  	Future<dynamic> callback = _channel.invokeMethod('getFriends');

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }


  static Future submitUserInfo(Map userInfo,Function(dynamic ret,Map err)result) {


  	Future<dynamic> callback = _channel.invokeMethod('submitUserInfo');

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }

  static Future getVersion(Function(dynamic ret,Map err)result) {


  	Future<dynamic> callback = _channel.invokeMethod('getVersion');

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }

  static Future enableWarn(bool enableWarn,Function(dynamic ret,Map err)result) {

    Map args = {"isWarn": enableWarn};

  	Future<dynamic> callback = _channel.invokeMethod('enableWarn',args);

    callback.then((dynamic response){
      if(result != null)
      {
        if(response is Map)
        {
          result(response["ret"],response["err"]);
        }
        else
        {
          result(null,null);
        }
      }
    });

    return callback;
  }
}

