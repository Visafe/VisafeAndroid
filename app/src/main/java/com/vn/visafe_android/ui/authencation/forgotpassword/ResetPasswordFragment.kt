package com.vn.visafe_android.ui.authencation.forgotpassword

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.rengwuxian.materialedittext.MaterialEditText
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentResetPasswordBinding
import com.vn.visafe_android.model.ResetPasswordRequest
import com.vn.visafe_android.utils.setSafeClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>() {

    private var isShowPassword: Boolean = false
    private var isShowPasswordAgain: Boolean = false
    private var listError: MutableList<View> = mutableListOf()
    private var email: String = ""
    private var otp: String = ""

    companion object {
        const val KEY_OTP = "KEY_OTP"
        const val KEY_EMAIL = "KEY_EMAIL"
        fun newInstance(email: String, otp: String): ResetPasswordFragment {
            val args = Bundle()
            args.putString(KEY_OTP, otp)
            args.putString(KEY_EMAIL, email)
            val fragment = ResetPasswordFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(KEY_EMAIL).toString()
            otp = it.getString(KEY_OTP).toString()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_reset_password
    }

    override fun initView() {
        setSafeClickListener(binding.btnShowHidePassword) { onShowHidePassword() }
        setSafeClickListener(binding.btnShowHidePasswordAgain) { onShowHidePasswordAgain() }
        setSafeClickListener(binding.btnBack) { backFragment() }
        setSafeClickListener(binding.btnConfirm) { doConfirm() }
    }

    private fun doConfirm() {
        if (!validateField() && listError.size > 0) {
            if (listError[0] is MaterialEditText) {
                listError[0].requestFocus()
                (activity as BaseActivity).showKeyboard()
            }
            return
        }
        showProgressDialog()
        val password = binding.edtInputPassword.text.toString()
        val rePassword = binding.edtInputPasswordAgain.text.toString()
        val resetPasswordRequest = ResetPasswordRequest(email, otp, password, rePassword)
        val client = NetworkClient()
        val call = context?.let { client.clientWithoutToken(context = it).doResetPassword(resetPasswordRequest) }
        call?.enqueue(
            BaseCallback(
                this, object : Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        dismissProgress()
                        if (response.body()?.status_code == NetworkClient.CODE_SUCCESS) {
                            response.body()?.msg?.let { Log.e("onResponse: ", it) }
                            (activity as ForgotPasswordActivity).finish()
                            //auto login with new pass
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        t.message?.let { Log.e("onFailure: ", it) }
                        dismissProgress()
                    }
                })
        )
    }

    private fun validateField(): Boolean {
        var isValidField = true
        listError.clear()
        if (binding.edtInputPassword.text.isNullOrEmpty()) {
            binding.edtInputPassword.error = "Vui lòng nhập mật khẩu!"
            listError.add(binding.edtInputPassword)
            isValidField = false
        }
        if (binding.edtInputPasswordAgain.text.isNullOrEmpty()) {
            binding.edtInputPasswordAgain.error = "Vui lòng nhập lại mật khẩu!"
            listError.add(binding.edtInputPasswordAgain)
            isValidField = false
        } else if (binding.edtInputPasswordAgain.text.toString() != binding.edtInputPassword.text.toString()) {
            binding.edtInputPassword.error = "Mật khẩu nhập lại không trùng khớp!"
            listError.add(binding.edtInputPasswordAgain)
            isValidField = false
        }
        return isValidField
    }


    private fun onShowHidePassword() {
        if (!isShowPassword) {
            isShowPassword = true
            binding.edtInputPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.btnShowHidePassword.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_eye_open
                    )
                }
            )
        } else {
            isShowPassword = false
            binding.edtInputPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.btnShowHidePassword.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_eye_close
                    )
                }
            )
        }
    }

    private fun onShowHidePasswordAgain() {
        if (!isShowPasswordAgain) {
            isShowPasswordAgain = true
            binding.edtInputPasswordAgain.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.btnShowHidePasswordAgain.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_eye_open
                    )
                }
            )
        } else {
            isShowPasswordAgain = false
            binding.edtInputPasswordAgain.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.btnShowHidePasswordAgain.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_eye_close
                    )
                }
            )
        }
    }
}