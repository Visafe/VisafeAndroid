package com.vn.visafe_android.ui.authencation.forgotpassword

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vn.visafe_android.base.BaseDialogFragment
import com.vn.visafe_android.databinding.FragmentInputOtpBinding
import com.vn.visafe_android.ui.custom.otp.OnChangeListener
import com.vn.visafe_android.ui.custom.otp.OnCompleteListener
import com.vn.visafe_android.utils.setSafeClickListener


class InputOTPFragment(var onInputOtpDialog: OnInputOtpDialog) : BaseDialogFragment() {

    lateinit var viewBinding: FragmentInputOtpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentInputOtpBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        setSafeClickListener(viewBinding.btnBack) { dismiss() }
        viewBinding.otpEditText.requestFocus()
        viewBinding.otpEditText.setOnCompleteListener(object : OnCompleteListener {
            @SuppressLint("SetTextI18n")
            override fun onComplete(value: String?) {
                value?.let {
                    Log.e("onComplete: ", it)
                    onInputOtpDialog.onInputOTP(it)

                }
            }
        })
        viewBinding.otpEditText.setOnChangeListener(object : OnChangeListener {
            override fun onChange(value: String?) {
            }
        })
    }

    interface OnInputOtpDialog {
        fun onInputOTP(otp: String)

        fun onSendToOtp()

    }
}