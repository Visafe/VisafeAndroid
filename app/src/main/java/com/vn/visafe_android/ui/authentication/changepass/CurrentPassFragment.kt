package com.vn.visafe_android.ui.authentication.changepass

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.vn.visafe_android.R
import com.vn.visafe_android.ViSafeApp
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentCurrentPassBinding
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.setOnSingClickListener

class CurrentPassFragment : BaseFragment<FragmentCurrentPassBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_current_pass

    override fun initView() {
        enableButton()
        setHideKeyboardFocus(binding.root)
        binding.edtPass.addTextChangedListener {
            enableButton()
        }
        binding.btnNext.setOnSingClickListener {
            (activity as ChangePasswordActivity).handlerFragment(
                CreateNewPassFragment(),
                ChangePasswordActivity.rootId,
                "CreateNewPassFragment"
            )
        }
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                backFragment()
            }
        })

        val userInfo = ViSafeApp().getPreference().getUserInfo()
        userInfo.let {
            binding.tvEmail.text = it.email
        }
    }

    private fun enableButton() {
        val pass = binding.edtPass.text.toString()
        if (pass.isNotBlank()) {
            with(binding.btnNext) {
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
            with(binding.btnNext) {
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
}