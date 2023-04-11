import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'passkeys_method_channel.dart';

abstract class PasskeysPlatform extends PlatformInterface {
  /// Constructs a PasskeysPlatform.
  PasskeysPlatform() : super(token: _token);

  static final Object _token = Object();

  static PasskeysPlatform _instance = MethodChannelPasskeys();

  /// The default instance of [PasskeysPlatform] to use.
  ///
  /// Defaults to [MethodChannelPasskeys].
  static PasskeysPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PasskeysPlatform] when
  /// they register themselves.
  static set instance(PasskeysPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> signInOrSignUpWithEnteredCredential({
    required String username,
    required String password,
  }) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> signInWithSavedCredential() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
