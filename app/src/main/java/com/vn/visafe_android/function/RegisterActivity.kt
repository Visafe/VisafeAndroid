package com.vn.visafe_android.function

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
import com.vn.visafe_android.databinding.ActivityRegisterBinding
import com.vn.visafe_android.model.RegisterRequest
import com.vn.visafe_android.utils.isValidEmail
import com.vn.visafe_android.utils.setSafeClickListener
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    lateinit var viewBinding: ActivityRegisterBinding

    private var isShowPassword: Boolean = false
    private var isShowPasswordAgain: Boolean = false
    private var listError: MutableList<View> = mutableListOf()

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
        val registerRequest = RegisterRequest()
        registerRequest.username = viewBinding.edtInputEmail.text.toString()
        registerRequest.email = viewBinding.edtInputEmail.text.toString()
        registerRequest.password = viewBinding.edtInputPassword.text.toString()
        registerRequest.passwordagain = viewBinding.edtInputPasswordAgain.text.toString()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doRegister(registerRequest)
        call.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body()?.status_code == NetworkClient.CODE_CREATED) {
                    response.body()?.msg?.let { Log.e("onResponse: ", it) }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
            }

        })
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
}