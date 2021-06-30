package com.vn.visafe_android.ui.authentication.forgotpassword

import android.util.Log
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentInputEmailBinding
import com.vn.visafe_android.utils.isValidEmail
import com.vn.visafe_android.utils.setSafeClickListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InputEmailFragment : BaseFragment<FragmentInputEmailBinding>(), InputOTPFragment.OnInputOtpDialog {

    private var username = ""
    private var inputOTPFragment: InputOTPFragment? = null
    override fun layoutRes(): Int {
        return R.layout.fragment_input_email
    }

    override fun initView() {
        setSafeClickListener(binding.btnBack) { backFragment() }
        setSafeClickListener(binding.btnSendRequest) {
            doSendRequest()
        }
    }

    private fun doSendRequest() {
        if (binding.edtInputEmail.text.isNullOrEmpty()) {
            binding.edtInputEmail.error = "Vui lòng nhập email"
            binding.edtInputEmail.requestFocus()
            return
        }
        if (!isValidEmail(binding.edtInputEmail.text.toString())) {
            binding.edtInputEmail.error = "Email không hợp lệ, vui lòng nhập lại!"
            binding.edtInputEmail.requestFocus()
            return
        }
        showProgressDialog()
        username = binding.edtInputEmail.text.toString()
        val client = NetworkClient()
        val call = context?.let { client.clientWithoutToken(context = it).doRequestEmailForgotPassword(username) }
        call?.enqueue(
            BaseCallback(
                this, object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        dismissProgress()
                        if (response.code() == NetworkClient.CODE_SUCCESS) {
                            inputOTPFragment = InputOTPFragment(this@InputEmailFragment)
                            inputOTPFragment?.show(childFragmentManager, "inputOTPFragment")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.message?.let { Log.e("onFailure: ", it) }
                        dismissProgress()
                    }
                })
        )

    }

    override fun onInputOTP(otp: String) {
        (activity as ForgotPasswordActivity).handlerFragment(
            ResetPasswordFragment.newInstance(username, otp),
            ForgotPasswordActivity.rootId,
            "ResetPasswordFragment"
        )
        inputOTPFragment?.dismiss()
    }

    override fun onSendToOtp() {
    }

}