package com.vn.visafe_android.function

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.rengwuxian.materialedittext.MaterialEditText
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityLoginBinding
import com.vn.visafe_android.function.forgotpassword.ForgotPasswordActivity
import com.vn.visafe_android.model.LoginRequest
import com.vn.visafe_android.model.RegisterRequest
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.utils.isValidEmail
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

    }

    private fun initControl() {
        viewBinding.btnBack.setOnClickListener { finish() }
        viewBinding.btnLogin.setOnClickListener {
            doLogin()
        }
        viewBinding.btnForgotPassword.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        viewBinding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        viewBinding.btnShowHidePassword.setOnClickListener { onShowHidePassword() }
    }

    private fun doLogin() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        if (!validateField() && listError.size > 0) {
//            if (listError[0] is MaterialEditText) {
//                listError[0].requestFocus()
//                showKeyboard()
//            }
//            return
//        }
//        val loginRequest = LoginRequest()
//        loginRequest.email = viewBinding.edtInputEmail.text.toString()
//        loginRequest.password = viewBinding.edtInputPassword.text.toString()
//        val client = NetworkClient()
//        val call = client.clientWithoutToken(context = applicationContext).doLogin(loginRequest)
//        call.enqueue(object : retrofit2.Callback<BaseResponse> {
//            override fun onResponse(
//                call: Call<BaseResponse>,
//                response: Response<BaseResponse>
//            ) {
//                if (response.body()?.status_code == NetworkClient.CODE_SUCCESS) {
//                    response.body()?.msg?.let { Log.e("onResponse: ", it) }
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
//                t.message?.let { Log.e("onFailure: ", it) }
//            }
//        })
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
}