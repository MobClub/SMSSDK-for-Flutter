import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:mobsms/mobsms.dart' ;

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

var zoneController = new TextEditingController();
var phoneController = new TextEditingController();
var tempIDController = new TextEditingController();
var codeController = new TextEditingController();

  @override
  void initState() {
    super.initState();

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('SMSSDK Flutter'),
        ),
        body: new Padding(
          padding: const EdgeInsets.all(10.0),
          child: Container(
            child: new ListView.builder(
              itemCount: 1,
              itemBuilder: (context, i)=> renderRow(i,context),
            ),
          ),
        ),
      ),
    );
  }

  void showAlert(String text, BuildContext context) {
    showDialog(
        context: context,
        builder: (BuildContext context) => AlertDialog(
                title: new Text("提示"),
                content: new Text(text),
                actions: <Widget>[
                  new TextButton(
                    child: new Text("OK"),
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                  )
                ]));
  }

void showPrivacyAlert(String text, BuildContext context) {
  showDialog(
      context: context,
      builder: (BuildContext context) => AlertDialog(
          title: new Text("隐私协议"),
          content: new Text(text),
          actions: <Widget>[
            new TextButton(
              child: new Text("同意"),
              onPressed: () {
                // 关闭弹框
                Navigator.of(context).pop();
                //Mobcommonlib.submitPolicyGrantResult(true, (dynamic ret, Map err) => {});
                print('smssdk: Smssdk.submitPrivacyGrantResult true');
                Smssdk.submitPrivacyGrantResult(true);
              },
            ),
            new TextButton(
              child: new Text("拒绝"),
              onPressed: () {
                Navigator.of(context).pop();
                //Mobcommonlib.submitPolicyGrantResult(false, (dynamic ret, Map err) => {});
                print('smssdk: Smssdk.submitPrivacyGrantResult false');
                Smssdk.submitPrivacyGrantResult(false);
              },
            )
          ]));
}

  Widget renderRow(i, BuildContext context){
    return new Column(
        
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Container(
            height: 30,
          ),
          Text('请先输入手机号'),
          TextField(
            controller: zoneController,
            decoration: InputDecoration(
              contentPadding: EdgeInsets.all(15.0),
              labelText: '请输入国家 如:86',
            ),
          ),
          TextField(
            controller: phoneController,
            keyboardType: TextInputType.number,
            decoration: InputDecoration(
              contentPadding: EdgeInsets.all(10.0),
              labelText: '请输入手机号'
            ),
          ),
          TextField(
            controller: tempIDController,
            decoration: InputDecoration(
              contentPadding: EdgeInsets.all(10.0),
              labelText: '模板编号（没有则不填）：'
            ),
          ),
          Container(
            height: 30,
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child:new TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('请求文本验证码'),
            onPressed: (){
                Smssdk.getTextCode(phoneController.text, zoneController.text, tempIDController.text, (dynamic ret, Map? err){

                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    String rst = ret.toString();
                    if (rst == "") {
                      rst = '获取验证码成功!';
                    }
                    showAlert(rst,context);
                  }
                });
                
              },
            ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child:new TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('请求语音验证码'),
            onPressed: (){
                 Smssdk.getVoiceCode(phoneController.text, zoneController.text, (dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert('获取验证码成功!',context);
                  }
                });
            },
          ),
          ),

          TextField(
            controller: codeController,
            decoration: InputDecoration(
              contentPadding: EdgeInsets.all(10.0),
              labelText: '请输入验证码'
            ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: new TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('提交验证码'),
            onPressed: (){
                Smssdk.commitCode(phoneController.text, zoneController.text, codeController.text, (dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert('提交验证码成功!',context);
                  }
                });
            },
          ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: new   TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('获取国家列表'),
            onPressed: (){
              Smssdk.getSupportedCountries((dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert(ret.toString(),context);
                  }
                });
            },
          ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: new   TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
              child: new Text('本机号码获取token'),
              onPressed: (){
                Smssdk.getToken((dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert(ret.toString(),context);
                  }
                });
              },
            ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: new   TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
              child: new Text('登陆'),
              onPressed: (){
                Smssdk.login(phoneController.text,(dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert(ret.toString(),context);
                  }
                });
              },
            ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity), 
            child: new TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('获取版本号'),
            onPressed: (){

              Smssdk.getVersion((dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert(ret.toString(),context);
                  }
                });
            },
          ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity), 
            child: new TextButton(
              style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.resolveWith((states){
                    if (states.contains(MaterialState.pressed)) {
                      return Colors.blueGrey[700];
                    }
                    return Colors.blueGrey;
                  }),
                  foregroundColor: MaterialStateProperty.resolveWith((states){
                    return Colors.white;
                  })
              ),
            child: new Text('开启提示框'),
            onPressed: (){
              Smssdk.enableWarn(true,(dynamic ret, Map? err){
                  if(err!=null)
                  {
                    showAlert(err.toString(),context);
                  }
                  else
                  {
                    showAlert('开启提示框 成功!',context);
                  }
                });
            },
          ),
          ),
          ConstrainedBox(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: new TextButton(
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.resolveWith((states){
                  if (states.contains(MaterialState.pressed)) {
                    return Colors.blueGrey[700];
                  }
                  return Colors.blueGrey;
                }),
                foregroundColor: MaterialStateProperty.resolveWith((states){
                  return Colors.white;
                }),
              ),
              child: new Text('打开隐私协议弹框'),
              onPressed: (){
                showPrivacyAlert('是否同意隐私协议？',context);
              },
            ),
          ),
        ],
      );
  }
}
