package com.vn.visafe_android.ui.authentication

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
import com.rengwuxian.materialedittext.MaterialEditText
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityRegisterBinding
import com.vn.visafe_android.model.request.ActiveAccountRequest
import com.vn.visafe_android.model.request.LoginRequest
import com.vn.visafe_android.model.request.RegisterRequest
import com.vn.visafe_android.ui.authentication.forgotpassword.InputOTPFragment
import com.vn.visafe_android.utils.isValidEmail
import com.vn.visafe_android.utils.setSafeClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity(), InputOTPFragment.OnInputOtpDialog {
    lateinit var viewBinding: ActivityRegisterBinding

    private var isShowPassword: Boolean = false
    private var isShowPasswordAgain: Boolean = false
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
        registerRequest.email = viewBinding.edtInputEmail.text.toString()
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
                    response.body()?.msg?.let {
                        Toast.makeText(
                            applicationContext,
                            "Vui lòng nhập mã OTP được gửi về mail của bạn để active tài khoản",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (inputOTPFragment == null || inputOTPFragment?.isVisible == false) {
                        inputOTPFragment = InputOTPFragment(
                            onInputOtpDialog = this@RegisterActivity, InputOTPFragment.TypeOTP.REGISTER, "Xác thực tài khoản",
                            viewBinding.edtInputEmail.text.toString()
                        )
                        inputOTPFragment?.show(supportFragmentManager, "inputOTPFragment")
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
                    finish()
//                    val intent = Intent(
//                        this@RegisterActivity,
//                        MainActivity::class.java
//                    )
//                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                            Intent.FLAG_ACTIVITY_NEW_TASK
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
//                    startActivity(intent)
//                    finish()
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

    override fun onSendToOtp() {
        doReSendOTP()
    }
}