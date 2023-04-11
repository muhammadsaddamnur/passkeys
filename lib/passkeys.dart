import 'passkeys_platform_interface.dart';

class Passkeys {
  Future<String?> getPlatformVersion() {
    return PasskeysPlatform.instance.getPlatformVersion();
  }

  Future<void> signInOrSignUpWithEnteredCredential({
    required String username,
    required String password,
  }) {
    return PasskeysPlatform.instance.signInOrSignUpWithEnteredCredential(
      username: username,
      password: password,
    );
  }

  Future<void> signInWithSavedCredential() {
    return PasskeysPlatform.instance.signInWithSavedCredential();
  }
}
