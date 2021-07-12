package com.vn.visafe_android.ui.authentication.forgotpassword

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentInputEmailBinding
import com.vn.visafe_android.utils.isNumber
import com.vn.visafe_android.utils.isValidEmail
import com.vn.visafe_android.utils.setSafeClickListener
import com.vn.visafe_android.utils.validatePhone
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
        binding.edtInputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvInputEmailError.visibility = View.GONE
                binding.edtInputEmail.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnClearTextInputEmail.visibility = if (p0.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

        })
        binding.edtInputEmail.setOnFocusChangeListener { v, hasFocus ->
            binding.btnClearTextInputEmail.visibility =
                if (hasFocus && !binding.edtInputEmail.text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        binding.btnClearTextInputEmail.setOnClickListener { binding.edtInputEmail.setText("") }
        setupTextPolicyHandleClick()
        setSafeClickListener(binding.btnBack) { backFragment() }
        setSafeClickListener(binding.btnSendRequest) {
            doSendRequest()
        }
    }

    private fun setupTextPolicyHandleClick() {
        val textSpan1 = getString(R.string.text_remember_password)
        val textSpan2 = getString(R.string.login)
        val stringSpan = SpannableString(textSpan1)
        val start = textSpan1.indexOf(textSpan2)
        val end = start + textSpan2.length
        stringSpan.setSpan(object : ClickableSpan() {

            override fun updateDrawState(@NonNull ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            }

            override fun onClick(widget: View) {
                (activity as ForgotPasswordActivity).finish()
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLogin.linksClickable = true
        binding.tvLogin.isClickable = true
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLogin.text = stringSpan
    }

    private fun doSendRequest() {
        if (binding.edtInputEmail.text.isNullOrEmpty()) {
            binding.edtInputEmail.background = context?.let { ContextCompat.getDrawable(it, R.drawable.bg_edittext_error) }
            binding.tvInputEmailError.visibility = View.VISIBLE
            binding.tvInputEmailError.text = getString(R.string.warning_input_number_phone_email)
            binding.edtInputEmail.requestFocus()
            return
        } else {
            if (!isNumber(binding.edtInputEmail.text.toString()) && !isValidEmail(binding.edtInputEmail.text.toString())) {
                binding.edtInputEmail.background = context?.let { ContextCompat.getDrawable(it, R.drawable.bg_edittext_error) }
                binding.tvInputEmailError.visibility = View.VISIBLE
                binding.tvInputEmailError.text = "Email không hợp lệ, vui lòng nhập lại!"
                binding.edtInputEmail.requestFocus()
                return
            } else if (isNumber(binding.edtInputEmail.text.toString()) && !validatePhone(binding.edtInputEmail.text.toString())) {
                binding.edtInputEmail.background =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.bg_edittext_error) }
                binding.tvInputEmailError.visibility = View.VISIBLE
                binding.tvInputEmailError.text = "Số điện thoại không hợp lệ, vui lòng nhập lại!"
                binding.edtInputEmail.requestFocus()
                return
            }
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
                            inputOTPFragment = InputOTPFragment(
                                onInputOtpDialog = this@InputEmailFragment,
                                InputOTPFragment.TypeOTP.FORGOT_PASSWORD,
                                "Xác thực tài khoản",
                                username
                            )
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