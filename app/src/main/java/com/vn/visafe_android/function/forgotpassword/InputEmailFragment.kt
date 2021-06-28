package com.vn.visafe_android.function.forgotpassword

import android.util.Log
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseResponse
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentInputEmailBinding
import com.vn.visafe_android.utils.isValidEmail
import com.vn.visafe_android.utils.setSafeClickListener
import retrofit2.Call
import retrofit2.Response

class InputEmailFragment : BaseFragment<FragmentInputEmailBinding>() {
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
        val email = binding.edtInputEmail.text.toString()
        val client = NetworkClient()
        val call = context?.let { client.clientWithoutToken(context = it).doRequestEmailForgotPassword(email) }
        call?.enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body()?.status_code == NetworkClient.CODE_SUCCESS) {
                    response.body()?.msg?.let { Log.e("onResponse: ", it) }
//                    (activity as ForgotPasswordActivity).handlerFragment(
//                        InputOTPFragment.newInstance(email),
//                        tag = "InputOTPFragment"
//                    )
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
            }
        })
        (activity as ForgotPasswordActivity).handlerFragment(
            InputOTPFragment.newInstance(email),
            tag = "InputOTPFragment"
        )
    }

}