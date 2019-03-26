import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mobsms/mobsms.dart';

void main() {
  const MethodChannel channel = MethodChannel('mobsms');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Mobsms.platformVersion, '42');
  });
}
