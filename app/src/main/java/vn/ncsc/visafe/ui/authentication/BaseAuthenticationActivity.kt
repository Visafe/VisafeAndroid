package vn.ncsc.visafe.ui.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.model.request.LoginSocialRequest
import vn.ncsc.visafe.model.response.LoginResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import java.security.MessageDigest

open class BaseAuthenticationActivity : BaseActivity() {

    private val googleSignIn: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
    }

    private val mGoogleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(this, googleSignIn)
    }

    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    private var resultLauncherRegisterGoogle =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // There are no request codes
            if (result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val tokenId = account.idToken
                    doLoginWithGoogle(tokenId)
                    Log.e("tokenId", "" + tokenId)
                } catch (e: ApiException) {
                    Log.e("ex", "" + e.message)
                }
            }
        }

    @SuppressLint("PackageManagerGetSignatures")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("KeyHash:", e.toString())
        } catch (e: java.security.NoSuchAlgorithmException) {
            Log.d("KeyHash:", e.toString())
        }

        mGoogleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        listenerFacebookLogin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun listenerFacebookLogin() {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        handleFacebookAccessToken(it.accessToken)
                    }
                }

                override fun onCancel() {
                    Log.e("onCancel: ", "onCancel")
                }

                override fun onError(error: FacebookException?) {
                    showToast("Tài khoản không chính xác. Vui lòng thử lại")
                }
            })
    }

    fun handleFacebookAccessToken(accessToken: AccessToken) {
        doLoginWithFacebook(accessToken.token)
    }

    open fun doSignInGoogle() {
        val signInGoogleIntent = mGoogleSignInClient.signInIntent
        resultLauncherRegisterGoogle.launch(signInGoogleIntent)
    }

    open fun doLoginWithGoogle(tokenId: String?) {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doLoginWithGoogle(LoginSocialRequest(tokenId))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.msg?.let { Log.e("onResponse: ", it) }
                    val token = response.body()?.token
                    token?.let {
                        SharePreferenceKeyHelper.getInstance(application).putString(
                            PreferenceKey.AUTH_TOKEN, it
                        )
                        SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.ISLOGIN, true)
                    }
                    setResult(RESULT_OK)
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }

        })
    }

    open fun doLoginWithFacebook(tokenId: String?) {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doLoginWithFacebook(LoginSocialRequest(tokenId))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.msg?.let { Log.e("onResponse: ", it) }
                    val token = response.body()?.token
                    token?.let {
                        SharePreferenceKeyHelper.getInstance(application).putString(
                            PreferenceKey.AUTH_TOKEN, it
                        )
                        SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.ISLOGIN, true)
                    }
                    setResult(RESULT_OK)
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }

        })
    }
}