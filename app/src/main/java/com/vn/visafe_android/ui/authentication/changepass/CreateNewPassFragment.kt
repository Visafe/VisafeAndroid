package com.vn.visafe_android.ui.authentication.changepass

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.vn.visafe_android.R
import com.vn.visafe_android.ViSafeApp
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentCreateNewPassBinding
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.setOnSingClickListener

class CreateNewPassFragment : BaseFragment<FragmentCreateNewPassBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_create_new_pass
    private var isShowPassNew = false

    override fun initView() {
        enableButton()
        handleShowPasswordNew()
        binding.btnSave.setOnSingClickListener {
            activity?.finish()
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
                        com.vn.visafe_android.R.color.color_FFB31F,
                        requireContext().theme
                    )

                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(),
                        com.vn.visafe_android.R.color.white
                    )
                )
                isEnabled = true
            }
        } else {
            with(binding.btnSave) {
                backgroundTintList =
                    resources.getColorStateList(
                        com.vn.visafe_android.R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                        requireContext(),
                        com.vn.visafe_android.R.color.color_AAAAAA
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
}