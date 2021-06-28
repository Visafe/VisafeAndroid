package com.vn.visafe_android.function

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    lateinit var viewBinding: ActivityLoginBinding

    private var isShowPassword: Boolean = false

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
            if (!validateField())
                return@setOnClickListener
        }
        viewBinding.btnForgotPassword.setOnClickListener { }
        viewBinding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        viewBinding.btnShowHidePassword.setOnClickListener { onShowHidePassword() }
    }

    private fun validateField(): Boolean {
        var isValidField = true
        if (viewBinding.edtInputEmail.text.isNullOrEmpty()) {
            viewBinding.edtInputEmail.error = "Vui lòng nhập Email"
            isValidField = false
        }
        if (viewBinding.edtInputPassword.text.isNullOrEmpty()) {
            viewBinding.edtInputEmail.error = "Vui lòng nhập Password"
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