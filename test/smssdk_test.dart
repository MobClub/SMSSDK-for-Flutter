import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:smssdk/smssdk.dart';

void main() {
  const MethodChannel channel = MethodChannel('smssdk');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
  });
}
