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
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.facebook.login.LoginManager
import com.google.gson.Gson
import com.rengwuxian.materialedittext.MaterialEditText
import kotlinx.android.synthetic.main.item_config.*
import retrofit2.Call
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityLoginBinding
import vn.ncsc.visafe.model.request.LoginRequest
import vn.ncsc.visafe.model.response.LoginResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.authentication.forgotpassword.ForgotPasswordActivity
import vn.ncsc.visafe.utils.*

class LoginActivity : BaseAuthenticationActivity() {

    lateinit var viewBinding: ActivityLoginBinding
    private var isShowPassword: Boolean = false
    private var listError: MutableList<View> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        initControl()
    }

    var filter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }

    private fun initView() {
        viewBinding.edtInputEmail.filters = arrayOf(filter, InputFilter.LengthFilter(50))
        viewBinding.edtInputPassword.filters = arrayOf(filter, InputFilter.LengthFilter(50))
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

    }

    private fun initControl() {
        viewBinding.btnBack.setOnClickListener { finish() }
        viewBinding.btnLogin.setOnClickListener {
            doLogin()
        }
        viewBinding.btnForgotPassword.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        viewBinding.btnShowHidePassword.setOnClickListener { onShowHidePassword() }
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

    private fun setupTextPolicyHandleClick() {
        val textSpan1 = getString(R.string.text_have_not_acc_need_register)
        val textSpan2 = getString(R.string.register)
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
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewBinding.tvRegister.linksClickable = true
        viewBinding.tvRegister.isClickable = true
        viewBinding.tvRegister.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.tvRegister.text = stringSpan
    }

    private fun doLogin() {
        if (!validateField() && listError.size > 0) {
            if (listError[0] is MaterialEditText) {
                listError[0].requestFocus()
                showKeyboard()
            }
            return
        }
        hideKeyboard(this)
        showProgressDialog()
        val loginRequest = LoginRequest()
        loginRequest.username =
            if (isNumber(viewBinding.edtInputEmail.text.toString())) formatMobileHead84(viewBinding.edtInputEmail.text.toString())
            else viewBinding.edtInputEmail.text.toString()
        loginRequest.password = viewBinding.edtInputPassword.text.toString()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doLogin(loginRequest)
        call.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                when {
                    response.code() == NetworkClient.CODE_SUCCESS -> {
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
                    response.code() == NetworkClient.CODE_NOT_EXISTS_ACCOUNT -> {
                        response.body()?.msg?.let { showToast(it) }
                    }
                    response.code() == NetworkClient.CODE_TIMEOUT_SESSION -> {
                        response.errorBody()?.let {
                            val buffer = it?.source()?.buffer?.readByteArray()
                            val dataString = buffer?.decodeToString()
                            val jsonObject = Gson().fromJson(dataString, BaseResponse::class.java)
                            jsonObject.localMsg?.let { it1 -> showToast(it1) }
                        }
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        })
    }

    private fun validateField(): Boolean {
        var isValidField = true
        listError.clear()
        if (viewBinding.edtInputEmail.text.isNullOrBlank()) {
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
}