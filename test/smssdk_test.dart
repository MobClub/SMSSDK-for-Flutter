import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobsmssdk/smssdk.dart';

void main() {
  const MethodChannel channel = MethodChannel('com.mob.smssdk');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Smssdk.getVersion, '42');
  });
}
