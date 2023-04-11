import 'package:flutter_test/flutter_test.dart';
import 'package:passkeys/passkeys.dart';
import 'package:passkeys/passkeys_platform_interface.dart';
import 'package:passkeys/passkeys_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPasskeysPlatform
    with MockPlatformInterfaceMixin
    implements PasskeysPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<void> signInOrSignUpWithEnteredCredential(
      {required String username, required String password}) {
    // TODO: implement signInOrSignUpWithEnteredCredential
    throw UnimplementedError();
  }

  @override
  Future<void> signInWithSavedCredential() {
    // TODO: implement signInWithSavedCredential
    throw UnimplementedError();
  }
}

void main() {
  final PasskeysPlatform initialPlatform = PasskeysPlatform.instance;

  test('$MethodChannelPasskeys is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPasskeys>());
  });

  test('getPlatformVersion', () async {
    Passkeys passkeysPlugin = Passkeys();
    MockPasskeysPlatform fakePlatform = MockPasskeysPlatform();
    PasskeysPlatform.instance = fakePlatform;

    expect(await passkeysPlugin.getPlatformVersion(), '42');
  });
}
