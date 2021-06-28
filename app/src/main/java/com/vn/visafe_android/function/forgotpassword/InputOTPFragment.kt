package com.vn.visafe_android.function.forgotpassword

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentInputOtpBinding
import com.vn.visafe_android.ui.custom.otp.OnChangeListener
import com.vn.visafe_android.ui.custom.otp.OnCompleteListener
import com.vn.visafe_android.utils.setSafeClickListener


class InputOTPFragment : BaseFragment<FragmentInputOtpBinding>() {

    private var email: String = ""

    companion object {
        const val KEY_EMAIL = "KEY_EMAIL"
        fun newInstance(email: String): InputOTPFragment {
            val args = Bundle()
            args.putString(KEY_EMAIL, email)
            val fragment = InputOTPFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(KEY_EMAIL).toString()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_input_otp
    }

    override fun initView() {
        setSafeClickListener(binding.btnBack) { backFragment() }
        binding.otpEditText.requestFocus()
        binding.otpEditText.setOnCompleteListener(object : OnCompleteListener {
            @SuppressLint("SetTextI18n")
            override fun onComplete(value: String?) {
                value?.let {
                    Log.e("onComplete: ", it)
                    (activity as ForgotPasswordActivity).handlerFragment(
                        ResetPasswordFragment.newInstance(email, it),
                        "ResetPasswordFragment"
                    )
                    binding.otpEditText.setText("")
                }
            }
        })

        binding.otpEditText.setOnChangeListener(object : OnChangeListener {
            override fun onChange(value: String?) {
                value?.let { Log.e("onChange: ", it) }
            }
        })
    }
}