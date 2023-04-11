package com.example.passkeys

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import kotlinx.coroutines.flow.MutableStateFlow

/** PasskeysPlugin */
class PasskeysPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private lateinit var activity: Activity
  private val signedInPasswordCredential = MutableStateFlow<PasswordCredential?>(null)


  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
     activity = binding.activity
  }
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "passkeys")
    context = flutterPluginBinding.applicationContext
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: io.flutter.plugin.common.MethodChannel.Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${Build.VERSION.RELEASE}")
      "signInOrSignUpWithEnteredCredential" -> signInOrSignUpWithEnteredCredential(call, result, activity)
      "signInWithSavedCredential" -> signInWithSavedCredential(call, result, activity)
      "simulateLogOut" -> simulateLogOut()
      else -> result.notImplemented()
    }
  }

  private val credentialManager by lazy {
    CredentialManager.create(context)
  }

  private fun signInOrSignUpWithEnteredCredential(call: MethodCall, result: io.flutter.plugin.common.MethodChannel.Result, activity: Activity) {
    val params = call.arguments as Map<*, *>
    val username = params["username"] as String
    val password = params["password"] as String
    CoroutineScope(Dispatchers.Main).launch  {
      val signInSuccess = true
      //do some sign in or sign up logic here
      // signInSuccess = doSomeSignInOrSignUpWork(username, password)

      //then if successful...
      if (signInSuccess) {
        //Set signedInPasswordCredential - this is a flag to indicate to the UI that we're now
        //signed in.
        signedInPasswordCredential.value = PasswordCredential(username, password)

        //...And offer to the user to save the credential to the store.
        saveCredential(activity, username, password)
      }
    }
  }

  private fun signInWithSavedCredential(call: MethodCall, result: io.flutter.plugin.common.MethodChannel.Result, activity: Activity) {
    CoroutineScope(Dispatchers.Main).launch  {
      try {
        val passwordCredential = getCredential(activity) ?: return@launch

        val signInSuccess = true
        //Run your app's sign in logic using the returned password credential
        // signInSuccess = doSomeSignInWork(username, password)

        //then if successful...
        if (signInSuccess) {
          //Indicate to the UI that we're now signed in.
          signedInPasswordCredential.value = passwordCredential
        }
      }
      catch (e: Exception) {
        println(e)
        Log.e("CredentialTest", "Error getting credential", e)
      }
    }
  }

  private suspend fun getCredential(activity: Activity): PasswordCredential? {
    try {
      //Tell the credential library that we're only interested in password credentials
      val getCredRequest = GetCredentialRequest(
        listOf(GetPasswordOption())
      )

      //Show the user a dialog allowing them to pick a saved credential
      val credentialResponse = credentialManager.getCredential(
        request = getCredRequest,
        activity = activity,
      )

      //Return the selected credential (as long as it's a username/password)
      val res = credentialResponse.credential as? PasswordCredential;
      println("username : " + res?.id.toString())
      println("password : " + res?.password.toString())
      return res
    }
    catch (e: GetCredentialCancellationException) {
      //User cancelled the request. Return nothing
      return null
    }
    catch (e: NoCredentialException) {
      //We don't have a matching credential
      return null
    }
    catch (e: GetCredentialException) {
      println(e)
      Log.e("CredentialTest", "Error getting credential", e)
      throw e
    }
  }

  //Typically you would run this function only after a successful sign-in. No point in saving
  //credentials that aren't correct.
  private suspend fun saveCredential(activity: Activity, username: String, password: String) {
    try {
      //Ask the user for permission to add the credentials to their store
      credentialManager.createCredential(
        request = CreatePasswordRequest(username, password),
        activity = activity,
      )
      Log.v("CredentialTest", "Credentials successfully added")

      //Note the new credentials
      signedInPasswordCredential.value = PasswordCredential(username, password)
    }
    catch (e: CreateCredentialCancellationException) {
      //do nothing, the user chose not to save the credential
      Log.v("CredentialTest", "User cancelled the save")
    }
    catch (e: CreateCredentialException) {
      Log.v("CredentialTest", "Credential save error", e)
    }
  }

  private fun simulateLogOut() {
    signedInPasswordCredential.value = null
  }


  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

}
