package vn.ncsc.visafe.ui.authentication.changepass

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCreateNewPassBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class CreateNewPassFragment : BaseFragment<FragmentCreateNewPassBinding>() {

    private var isShowPassNew = false
    private var isShowPassNewAgain = false
    private var changePasswordActivity: ChangePasswordActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_create_new_pass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ChangePasswordActivity) {
            changePasswordActivity = context
        }
    }

    override fun initView() {
        enableButton()
        handleShowPasswordNew()
        handleShowPasswordNewAgain()
        binding.btnSave.setOnSingClickListener {
            if (binding.edtPassNew.text.toString() != binding.edtPassNewAgain.text.toString()) {
                (activity as BaseActivity).showToast("Mật khẩu nhập lại không trùng với mật khẩu mới, vui lòng kiểm tra lại")
                return@setOnSingClickListener
            }
            changePasswordActivity?.changePasswordRequest?.newPassword = binding.edtPassNew.text.toString()
            changePasswordActivity?.changePasswordRequest?.repeatPassword = binding.edtPassNewAgain.text.toString()
            changePasswordActivity?.doChangePassword()
        }
        setHideKeyboardFocus(binding.root)
        binding.edtPassNew.addTextChangedListener {
            if (binding.edtPassNew.length() in 6..32) {
                binding.rlPassNew.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext)
                binding.tvWarning.visibility = View.GONE
            } else {
                binding.rlPassNew.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_warning)
                binding.tvWarning.visibility = View.VISIBLE
            }
            enableButton()
        }
        binding.edtPassNewAgain.addTextChangedListener {
            if (binding.edtPassNewAgain.length() in 6..32) {
                binding.rlPassNewAgain.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext)
                binding.tvWarningAgain.visibility = View.GONE
            } else {
                binding.rlPassNewAgain.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_warning)
                binding.tvWarningAgain.visibility = View.VISIBLE
            }
            enableButton()
        }
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                backFragment()
            }
        })
        binding.ivPassNew.setOnClickListener {
            handleShowPasswordNew()
        }
        binding.ivPassNewAgain.setOnClickListener {
            handleShowPasswordNewAgain()
        }
        val userInfo = ViSafeApp().getPreference().getUserInfo()
        userInfo.let {
            binding.tvEmail.text = it.email
        }
    }

    private fun enableButton() {
        val pass = binding.edtPassNew.text.toString()
        val passAgain = binding.edtPassNewAgain.text.toString()
        if (pass.isNotBlank() && passAgain.isNotBlank()) {
            with(binding.btnSave) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_FFB31F,
                        requireContext().theme
                    )

                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                isEnabled = true
            }
        } else {
            with(binding.btnSave) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(),
                        R.color.color_AAAAAA
                    )
                )
                isEnabled = false
            }
        }
    }

    private fun handleShowPasswordNew() {
        try {
            if (!isShowPassNew) {
                isShowPassNew = true
                binding.edtPassNew.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivPassNew.setImageResource(R.drawable.ic_eye_off)
            } else {
                isShowPassNew = false
                binding.edtPassNew.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivPassNew.setImageResource(R.drawable.ic_eye_on)
            }
            binding.edtPassNew.setSelection(binding.edtPassNew.text.toString().trim().length)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleShowPasswordNewAgain() {
        try {
            if (!isShowPassNewAgain) {
                isShowPassNewAgain = true
                binding.edtPassNewAgain.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivPassNewAgain.setImageResource(R.drawable.ic_eye_off)
            } else {
                isShowPassNewAgain = false
                binding.edtPassNewAgain.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivPassNewAgain.setImageResource(R.drawable.ic_eye_on)
            }
            binding.edtPassNewAgain.setSelection(binding.edtPassNewAgain.text.toString().trim().length)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}