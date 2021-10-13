package vn.ncsc.visafe.ui.authentication.forgotpassword

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseDialogFragment
import vn.ncsc.visafe.databinding.FragmentInputOtpBinding
import vn.ncsc.visafe.ui.custom.otp.OnChangeListener
import vn.ncsc.visafe.ui.custom.otp.OnCompleteListener
import vn.ncsc.visafe.utils.setSafeClickListener

class InputOTPFragment(
    var onInputOtpDialog: OnInputOtpDialog, var type: TypeOTP, var title: String?, var account: String?
) : BaseDialogFragment() {

    lateinit var viewBinding: FragmentInputOtpBinding
    private var otpValue: String = ""

    private val timeCountDown = object : CountDownTimer(30000, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            viewBinding.tvTime.text = "(${millisUntilFinished / 1000}s)"
        }

        override fun onFinish() {
            viewBinding.tvTime.visibility = View.GONE
        }
    }

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
        enableButton()
        viewBinding.tvTime.text = "30s"
        viewBinding.tvTime.visibility = View.VISIBLE
        timeCountDown.start()
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
        viewBinding.otpEditText.addTextChangedListener {
            enableButton()
        }
        viewBinding.otpEditText.setOnChangeListener(object : OnChangeListener {
            override fun onChange(value: String?) {
                viewBinding.tvOtpError.visibility = View.GONE
            }
        })

        viewBinding.btnNext.setOnClickListener {
            otpValue.let {
                if (otpValue.length == 6) {
                    onInputOtpDialog.onInputOTP(it)
                }
            }
        }

        viewBinding.ctrlResendOTP.setOnClickListener {
            onInputOtpDialog.onSendToOtp()
            viewBinding.tvTime.text = "30s"
            viewBinding.tvTime.visibility = View.VISIBLE
            timeCountDown.start()
        }
    }

    fun setErrorOtp(msg: String) {
        viewBinding.tvOtpError.visibility = View.VISIBLE
        viewBinding.tvOtpError.text = msg
        (activity as BaseActivity).showKeyboard()
    }

    private fun enableButton() {
        val pin = viewBinding.otpEditText.text.toString()
        if (pin.isNotBlank()) {
            with(viewBinding.btnNext) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, requireContext().theme)

                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                isEnabled = true
            }
        } else {
            with(viewBinding.btnNext) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_AAAAAA
                    )
                )
                isEnabled = false
            }
        }
    }

    interface OnInputOtpDialog {
        fun onInputOTP(otp: String)

        fun onSendToOtp()

    }

    enum class TypeOTP {
        FORGOT_PASSWORD, REGISTER
    }
}