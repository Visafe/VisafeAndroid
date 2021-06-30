package com.vn.visafe_android.ui.authencation

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.rengwuxian.materialedittext.MaterialEditText
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityRegisterBinding
import com.vn.visafe_android.model.ActiveAccountRequest
import com.vn.visafe_android.model.LoginRequest
import com.vn.visafe_android.model.RegisterRequest
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.authencation.forgotpassword.InputOTPFragment
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

    }

    private fun initControl() {
        setSafeClickListener(viewBinding.btnBack) { finish() }
        setSafeClickListener(viewBinding.btnRegister) {
            doRegister()
        }
        setSafeClickListener(viewBinding.btnCancelRegister) { finish() }
        setSafeClickListener(viewBinding.btnShowHidePassword) { onShowHidePassword() }
        setSafeClickListener(viewBinding.btnShowHidePasswordAgain) { onShowHidePasswordAgain() }
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
        registerRequest.repeatPassword = viewBinding.edtInputPasswordAgain.text.toString()
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
                    inputOTPFragment = InputOTPFragment(onInputOtpDialog = this@RegisterActivity)
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
                        inputOTPFragment = InputOTPFragment(onInputOtpDialog = this@RegisterActivity)
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
            viewBinding.edtInputEmail.error = "Vui lòng nhập email!"
            listError.add(viewBinding.edtInputEmail)
            isValidField = false
        } else {
            if (!isValidEmail(viewBinding.edtInputEmail.text.toString())) {
                viewBinding.edtInputEmail.error = "Email không hợp lệ, vui lòng nhập lại!"
                listError.add(viewBinding.edtInputEmail)
                isValidField = false
            }
        }
        if (viewBinding.edtInputPassword.text.isNullOrEmpty()) {
            viewBinding.edtInputPassword.error = "Vui lòng nhập mật khẩu!"
            listError.add(viewBinding.edtInputPassword)
            isValidField = false
        }
        if (viewBinding.edtInputPasswordAgain.text.isNullOrEmpty()) {
            viewBinding.edtInputPasswordAgain.error = "Vui lòng nhập lại mật khẩu!"
            listError.add(viewBinding.edtInputPasswordAgain)
            isValidField = false
        } else if (viewBinding.edtInputPasswordAgain.text.toString() != viewBinding.edtInputPassword.text.toString()) {
            viewBinding.edtInputPassword.error = "Mật khẩu nhập lại không trùng khớp!"
            listError.add(viewBinding.edtInputPasswordAgain)
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
                    R.drawable.ic_eye_open
                )
            )
        } else {
            isShowPassword = false
            viewBinding.edtInputPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            viewBinding.btnShowHidePassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_eye_close
                )
            )
        }
    }

    private fun onShowHidePasswordAgain() {
        if (!isShowPasswordAgain) {
            isShowPasswordAgain = true
            viewBinding.edtInputPasswordAgain.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            viewBinding.btnShowHidePasswordAgain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_eye_open
                )
            )
        } else {
            isShowPasswordAgain = false
            viewBinding.edtInputPasswordAgain.transformationMethod =
                PasswordTransformationMethod.getInstance()
            viewBinding.btnShowHidePasswordAgain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_eye_close
                )
            )
        }
    }

    override fun onInputOTP(otp: String) {
        showProgressDialog()
        val email = viewBinding.edtInputEmail.text.toString()
        val activeAccountRequest = ActiveAccountRequest(email = email, otp = otp)
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doActiveAccount(activeAccountRequest)
        call.enqueue(BaseCallback(this, object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                dismissProgress()
                inputOTPFragment?.dismiss()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val intent = Intent(
                        this@RegisterActivity,
                        MainActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(intent)
                    finish()
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