import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'passkeys_platform_interface.dart';

/// An implementation of [PasskeysPlatform] that uses method channels.
class MethodChannelPasskeys extends PasskeysPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('passkeys');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> signInOrSignUpWithEnteredCredential({
    required String username,
    required String password,
  }) async {
    await methodChannel.invokeMethod<void>(
      'signInOrSignUpWithEnteredCredential',
      {
        'username': username,
        'password': password,
      },
    );
  }

  @override
  Future<void> signInWithSavedCredential() async {
    await methodChannel.invokeMethod<void>('signInWithSavedCredential');
  }
}
