package vn.ncsc.visafe.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.facebook.login.LoginManager
import com.rengwuxian.materialedittext.MaterialEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityRegisterBinding
import vn.ncsc.visafe.model.request.ActiveAccountRequest
import vn.ncsc.visafe.model.request.LoginRequest
import vn.ncsc.visafe.model.request.RegisterRequest
import vn.ncsc.visafe.model.response.LoginResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.authentication.forgotpassword.InputOTPFragment
import vn.ncsc.visafe.utils.*

class RegisterActivity : BaseAuthenticationActivity(), InputOTPFragment.OnInputOtpDialog {

    lateinit var viewBinding: ActivityRegisterBinding

    private var isShowPassword: Boolean = false
    private var listError: MutableList<View> = mutableListOf()
    private var inputOTPFragment: InputOTPFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        initControl()
    }

    private fun initView() {
        viewBinding.edtInputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.tvInputEmailError.visibility = View.GONE
                viewBinding.edtInputEmail.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
                viewBinding.btnClearTextInputEmail.visibility = if (p0.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

        })
        viewBinding.edtInputEmail.setOnFocusChangeListener { v, hasFocus ->
            viewBinding.btnClearTextInputEmail.visibility =
                if (hasFocus && !viewBinding.edtInputEmail.text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewBinding.edtInputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.tvInputPasswordError.visibility = View.GONE
                viewBinding.edtInputPassword.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        setupTextPolicyHandleClick()
        viewBinding.tvTerm.text = getString(R.string.note_term)
            .let { androidx.core.text.HtmlCompat.fromHtml(it, 0) }
    }

    private fun setupTextPolicyHandleClick() {
        val textSpan1 = getString(R.string.text_have_acc)
        val textSpan2 = getString(R.string.login)
        val stringSpan = SpannableString(textSpan1)
        val start = textSpan1.indexOf(textSpan2)
        val end = start + textSpan2.length
        stringSpan.setSpan(object : ClickableSpan() {

            override fun updateDrawState(@NonNull ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
            }

            override fun onClick(widget: View) {
                finish()
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewBinding.tvLogin.linksClickable = true
        viewBinding.tvLogin.isClickable = true
        viewBinding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.tvLogin.text = stringSpan
    }

    private fun initControl() {
        setSafeClickListener(viewBinding.btnBack) { finish() }
        setSafeClickListener(viewBinding.btnRegister) {
            doRegister()
        }
        setSafeClickListener(viewBinding.btnShowHidePassword) { onShowHidePassword() }
        viewBinding.btnClearTextInputEmail.setOnClickListener { viewBinding.edtInputEmail.setText("") }
        viewBinding.btnLoginSocialGoogle.setOnSingClickListener {
            doSignInGoogle()
        }
        viewBinding.btnLoginSocialFacebook.setOnSingClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("public_profile", "email")
            )
        }
    }

    private fun doRegister() {
        if (!validateField() && listError.size > 0) {
            if (listError[0] is MaterialEditText) {
                listError[0].requestFocus()
                showKeyboard()
            }
            return
        }
        showProgressDialog()
        val registerRequest = RegisterRequest()
        registerRequest.username = viewBinding.edtInputEmail.text.toString()
        registerRequest.email = if (isNumber(viewBinding.edtInputEmail.text.toString()))
            formatMobileHead84(viewBinding.edtInputEmail.text.toString()) else viewBinding.edtInputEmail.text.toString()
        registerRequest.password = viewBinding.edtInputPassword.text.toString()
        registerRequest.repeatPassword = viewBinding.edtInputPassword.text.toString()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doRegister(registerRequest)
        call.enqueue(BaseCallback(this, object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    dismissProgress()
                    response.body()?.msg?.let {
                        Toast.makeText(
                            applicationContext,
                            "Vui lòng nhập mã OTP được gửi về mail của bạn để active tài khoản",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    inputOTPFragment = InputOTPFragment(
                        onInputOtpDialog = this@RegisterActivity, InputOTPFragment.TypeOTP.REGISTER, "Xác thực tài khoản",
                        viewBinding.edtInputEmail.text.toString()
                    )
                    inputOTPFragment?.show(supportFragmentManager, "inputOTPFragment")
                } else if (response.code() == NetworkClient.CODE_EXISTS_ACCOUNT) {
                    doReSendOTP()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }

        }))
    }

    private fun doReSendOTP() {
        showProgressDialog()
        val email = viewBinding.edtInputEmail.text.toString()
        val reSendOTP = LoginRequest(username = email)
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doReActiveAccount(reSendOTP)
        call.enqueue(BaseCallback(this, object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    if (inputOTPFragment == null || inputOTPFragment?.isVisible == false) {
                        response.body()?.msg?.let {
                            showToast("Mã xác nhận đã được gửi lại vào email/số điện thoại của bạn")
                            Toast.makeText(
                                applicationContext,
                                "Vui lòng nhập mã OTP được gửi về mail của bạn để active tài khoản",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        inputOTPFragment = InputOTPFragment(
                            onInputOtpDialog = this@RegisterActivity, InputOTPFragment.TypeOTP.REGISTER, "Xác thực tài khoản",
                            viewBinding.edtInputEmail.text.toString()
                        )
                        inputOTPFragment?.show(supportFragmentManager, "inputOTPFragment")
                    } else {
                        showToast("Mã xác nhận đã được gửi lại vào email/số điện thoại của bạn")
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }

        }))
    }

    private fun validateField(): Boolean {
        var isValidField = true
        listError.clear()
        if (viewBinding.edtInputEmail.text.isNullOrEmpty()) {
            viewBinding.edtInputEmail.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_error)
            viewBinding.tvInputEmailError.visibility = View.VISIBLE
            viewBinding.tvInputEmailError.text = getString(R.string.warning_input_number_phone_email)
            listError.add(viewBinding.edtInputEmail)
            isValidField = false
        } else {
            if (!isNumber(viewBinding.edtInputEmail.text.toString()) && !isValidEmail(viewBinding.edtInputEmail.text.toString())) {
                viewBinding.edtInputEmail.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_error)
                viewBinding.tvInputEmailError.visibility = View.VISIBLE
                viewBinding.tvInputEmailError.text = "Email không hợp lệ, vui lòng nhập lại!"
                listError.add(viewBinding.edtInputEmail)
                isValidField = false
            } else if (isNumber(viewBinding.edtInputEmail.text.toString()) && !validatePhone(viewBinding.edtInputEmail.text.toString())) {
                viewBinding.edtInputEmail.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_error)
                viewBinding.tvInputEmailError.visibility = View.VISIBLE
                viewBinding.tvInputEmailError.text = "Số điện thoại không hợp lệ, vui lòng nhập lại!"
                listError.add(viewBinding.edtInputEmail)
                isValidField = false
            }
        }
        if (viewBinding.edtInputPassword.text.isNullOrEmpty()) {
            viewBinding.edtInputPassword.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_error)
            viewBinding.tvInputPasswordError.visibility = View.VISIBLE
            viewBinding.tvInputPasswordError.text = "Vui lòng nhập mật khẩu!"
            listError.add(viewBinding.edtInputPassword)
            isValidField = false
        }
        return isValidField
    }

    private fun onShowHidePassword() {
        if (!isShowPassword) {
            isShowPassword = true
            viewBinding.edtInputPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            viewBinding.btnShowHidePassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_eye_on
                )
            )
        } else {
            isShowPassword = false
            viewBinding.edtInputPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            viewBinding.btnShowHidePassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_eye_off
                )
            )
        }
    }

    override fun onInputOTP(otp: String) {
        showProgressDialog()
        val username = viewBinding.edtInputEmail.text.toString()
        val activeAccountRequest = ActiveAccountRequest(username = username, otp = otp)
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doActiveAccount(activeAccountRequest)
        call.enqueue(BaseCallback(this, object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    inputOTPFragment?.dismiss()
                    response.body()?.msg?.let {
                        Toast.makeText(
                            applicationContext,
                            "Đăng ký tài khoản thành công",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    autoLogin()
                } else {
                    inputOTPFragment?.setErrorOtp("Mã xác thực không chính xác")
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }

        }))
    }

    private fun autoLogin() {
        showProgressDialog()
        val loginRequest = LoginRequest()
        loginRequest.username = viewBinding.edtInputEmail.text.toString()
        loginRequest.password = viewBinding.edtInputPassword.text.toString()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doLogin(loginRequest)
        call.enqueue(BaseCallback(this@RegisterActivity, object : Callback<LoginResponse> {
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
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    override fun onSendToOtp() {
        doReSendOTP()
    }
}