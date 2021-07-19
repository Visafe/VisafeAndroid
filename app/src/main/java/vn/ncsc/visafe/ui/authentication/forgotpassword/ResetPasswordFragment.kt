package vn.ncsc.visafe.ui.authentication.forgotpassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.rengwuxian.materialedittext.MaterialEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentResetPasswordBinding
import vn.ncsc.visafe.model.request.ResetPasswordRequest
import vn.ncsc.visafe.utils.setSafeClickListener

class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>() {

    private var isShowPassword: Boolean = false
    private var isShowPasswordAgain: Boolean = false
    private var listError: MutableList<View> = mutableListOf()
    private var email: String = ""
    private var otp: String = ""

    companion object {
        const val KEY_OTP = "KEY_OTP"
        const val KEY_EMAIL = "KEY_EMAIL"
        fun newInstance(email: String?, otp: String): ResetPasswordFragment {
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
        binding.tvContent.text = String.format(getString(R.string.text_input_pass_for_acc), email)
        setSafeClickListener(binding.btnShowHidePassword) { onShowHidePassword() }
        setSafeClickListener(binding.btnShowHidePasswordAgain) { onShowHidePasswordAgain() }
        setSafeClickListener(binding.btnBack) { backFragment() }
        setSafeClickListener(binding.btnConfirm) { doConfirm() }

        binding.edtInputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvInputPasswordError.visibility = View.GONE
                binding.edtInputPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.edtInputPasswordAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvInputPasswordErrorAgain.visibility = View.GONE
                binding.edtInputPasswordAgain.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_custom_edittext)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun doConfirm() {
        if (!validateField() && listError.size > 0) {
            if (listError[0] is MaterialEditText) {
                listError[0].requestFocus()
                (activity as BaseActivity).showKeyboard()
            }
            return
        }
        (activity as BaseActivity).hideKeyboard(activity)
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
            binding.edtInputPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_error)
            binding.tvInputPasswordError.visibility = View.VISIBLE
            binding.tvInputPasswordError.text = "Vui lòng nhập mật khẩu"
            listError.add(binding.edtInputPassword)
            isValidField = false
        } else {
            if (binding.edtInputPassword.text!!.length in 33 downTo 5) {
                binding.edtInputPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_error)
                binding.tvInputPasswordError.visibility = View.VISIBLE
                binding.tvInputPasswordError.text = "Mật khẩu phải có độ dài từ 6-32 ký tự"
                listError.add(binding.edtInputPassword)
                isValidField = false
            }
        }
        if (binding.edtInputPasswordAgain.text.isNullOrEmpty()) {
            binding.edtInputPasswordAgain.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_error)
            binding.tvInputPasswordErrorAgain.visibility = View.VISIBLE
            binding.tvInputPasswordErrorAgain.text = "Vui lòng nhập mật khẩu"
            listError.add(binding.edtInputPasswordAgain)
            isValidField = false
        } else {
            if (binding.edtInputPasswordAgain.text!!.length in 33 downTo 5) {
                binding.edtInputPasswordAgain.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_error)
                binding.tvInputPasswordErrorAgain.visibility = View.VISIBLE
                binding.tvInputPasswordErrorAgain.text = "Mật khẩu phải có độ dài từ 6-32 ký tự"
                listError.add(binding.edtInputPasswordAgain)
                isValidField = false
            }
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
                        R.drawable.ic_eye_on
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
                        R.drawable.ic_eye_off
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
                        R.drawable.ic_eye_on
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
                        R.drawable.ic_eye_off
                    )
                }
            )
        }
    }
}