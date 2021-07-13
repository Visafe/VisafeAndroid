package vn.ncsc.visafe.ui.authentication.forgotpassword

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.ncsc.visafe.ui.custom.otp.OnChangeListener
import vn.ncsc.visafe.ui.custom.otp.OnCompleteListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseDialogFragment
import vn.ncsc.visafe.databinding.FragmentInputOtpBinding
import vn.ncsc.visafe.utils.setSafeClickListener


class InputOTPFragment(var onInputOtpDialog: OnInputOtpDialog, var type: TypeOTP, var title: String, var account: String) : BaseDialogFragment() {

    lateinit var viewBinding: FragmentInputOtpBinding
    private var otpValue: String = ""

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
        when (type) {
            TypeOTP.FORGOT_PASSWORD -> {
                viewBinding.tvTitle.text = title
                viewBinding.tvDescription.text = String.format(getString(R.string.visafe_gui_ma_xac_thuc), account)
            }
            TypeOTP.REGISTER -> {
                viewBinding.tvTitle.text = title
                viewBinding.tvDescription.text = String.format(getString(R.string.text_input_pass_for_acc), account)
            }
        }

        (activity as BaseActivity).showKeyboard()
        setSafeClickListener(viewBinding.btnBack) {
            hideKeyboard(activity)
            dismiss()
        }
        viewBinding.otpEditText.requestFocus()
        viewBinding.otpEditText.setOnCompleteListener(object : OnCompleteListener {
            @SuppressLint("SetTextI18n")
            override fun onComplete(value: String?) {
                if (value != null) {
                    otpValue = value
                }
            }
        })
        viewBinding.otpEditText.setOnChangeListener(object : OnChangeListener {
            override fun onChange(value: String?) {
                viewBinding.tvOtpError.visibility = View.GONE
            }
        })

        viewBinding.btnNext.setOnClickListener {
            otpValue?.let {
                if (otpValue.length == 6) {
                    onInputOtpDialog.onInputOTP(it)
                } else {
                    viewBinding.tvOtpError.visibility = View.VISIBLE
                    viewBinding.tvOtpError.text = "Mã OTP không hợp lệ"
                }
            }
        }
    }

    fun setErrorOtp(msg: String) {
        viewBinding.tvOtpError.visibility = View.VISIBLE
        viewBinding.tvOtpError.text = msg
        (activity as BaseActivity).showKeyboard()
    }

    interface OnInputOtpDialog {
        fun onInputOTP(otp: String)

        fun onSendToOtp()

    }

    enum class TypeOTP {
        FORGOT_PASSWORD, REGISTER
    }
}