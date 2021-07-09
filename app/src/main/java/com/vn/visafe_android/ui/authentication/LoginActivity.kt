package com.vn.visafe_android.ui.authentication

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
import com.rengwuxian.materialedittext.MaterialEditText
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityLoginBinding
import com.vn.visafe_android.model.request.LoginRequest
import com.vn.visafe_android.model.response.LoginResponse
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.authentication.forgotpassword.ForgotPasswordActivity
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.utils.SharePreferenceKeyHelper
import com.vn.visafe_android.utils.isValidEmail
import kotlinx.android.synthetic.main.item_config.*
import retrofit2.Call
import retrofit2.Response

class LoginActivity : BaseActivity() {

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

    private fun initView() {
        viewBinding.edtInputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.tvInputEmailError.visibility = View.GONE
                viewBinding.edtInputEmail.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
                viewBinding.btnClearTextInputEmail.visibility = if (p0.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

        })
        viewBinding.edtInputEmail.setOnFocusChangeListener { v, hasFocus ->
            viewBinding.btnClearTextInputEmail.visibility = if (hasFocus && !viewBinding.edtInputEmail.text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewBinding.edtInputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewBinding.tvInputPasswordError.visibility = View.GONE
                viewBinding.edtInputPassword.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_custom_edittext)
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
        showProgressDialog()
        val loginRequest = LoginRequest()
        loginRequest.username = viewBinding.edtInputEmail.text.toString()
        loginRequest.password = viewBinding.edtInputPassword.text.toString()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doLogin(loginRequest)
        call.enqueue(BaseCallback(this@LoginActivity, object : retrofit2.Callback<LoginResponse> {
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
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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
            viewBinding.tvInputEmailError.text = "Vui lòng nhập email!"
            listError.add(viewBinding.edtInputEmail)
            isValidField = false
        } else {
            if (!isValidEmail(viewBinding.edtInputEmail.text.toString())) {
                viewBinding.edtInputEmail.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_error)
                viewBinding.tvInputEmailError.visibility = View.VISIBLE
                viewBinding.tvInputEmailError.text = "Email không hợp lệ, vui lòng nhập lại!"
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