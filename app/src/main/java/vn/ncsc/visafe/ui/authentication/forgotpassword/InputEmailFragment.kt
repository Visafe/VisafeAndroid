package vn.ncsc.visafe.ui.authentication.forgotpassword

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentInputEmailBinding
import vn.ncsc.visafe.model.request.LoginRequest
import vn.ncsc.visafe.utils.*

class InputEmailFragment : BaseFragment<FragmentInputEmailBinding>(), InputOTPFragment.OnInputOtpDialog {

    private var username: String? = ""
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
        getOtpForgotPassword(false)
    }

    private fun getOtpForgotPassword(isResendOtp: Boolean) {
        if (!isResendOtp)
            showProgressDialog()
        username = if (isNumber(binding.edtInputEmail.text.toString()))
            formatMobileHead84(binding.edtInputEmail.text.toString()) else binding.edtInputEmail.text.toString()
        val client = NetworkClient()
        val call = context?.let {
            client.clientWithoutToken(context = it).doRequestEmailForgotPassword(LoginRequest(username = username))
        }
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (!isResendOtp)
                    dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    if (inputOTPFragment == null || inputOTPFragment?.isVisible == false) {
                        inputOTPFragment = InputOTPFragment(
                            onInputOtpDialog = this@InputEmailFragment,
                            InputOTPFragment.TypeOTP.FORGOT_PASSWORD,
                            "Xác thực tài khoản",
                            username
                        )
                        inputOTPFragment?.show(childFragmentManager, "inputOTPFragment")
                    } else {
                        (activity as BaseActivity).showToast("Mã xác nhận đã được gửi lại vào email/số điện thoại của bạn")
                    }
                } else if (response.code() == NetworkClient.CODE_EXISTS_ACCOUNT) {
                    (activity as BaseActivity).showToast(
                        "Email hoặc số điện thoại của bạn không hợp lệ, vui lòng kiểm tra lại"
                    )
                } else if (response.code() == NetworkClient.CODE_406) {
                    (activity as BaseActivity).showToast(
                        "OTP chỉ có thể được gửi lại sau 60 giây"
                    )
                } else {
                    response.errorBody()?.let {
                        val buffer = it?.source()?.buffer?.readByteArray()
                        val dataString = buffer?.decodeToString()
                        val jsonObject = Gson().fromJson(dataString, BaseResponse::class.java)
                        jsonObject.localMsg?.let { it1 -> (activity as BaseActivity).showToast(it1) }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                if (!isResendOtp)
                    dismissProgress()
            }
        })
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
        getOtpForgotPassword(true)
    }

}