package com.vn.visafe_android.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentCreateNewPinBinding
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.setOnSingClickListener
import com.vn.visafe_android.utils.updateOTPCode

class CreateNewPinFragment : BaseFragment<FragmentCreateNewPinBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_create_new_pin

    override fun initView() {
        setHideKeyboardFocus(binding.root)
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                activity?.onBackPressed()
            }
        })
        binding.etPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                updateOTPCode(
                    binding.etPinCode,
                    binding.tvOtp1,
                    binding.tvOtp2,
                    binding.tvOtp3,
                    binding.tvOtp4,
                    binding.tvOtp5,
                    binding.tvOtp6,
                    requireContext()
                )
                enableButton()
            }

        })
        binding.tvNext.setOnSingClickListener {
            (activity as UpdatePinActivity).handlerFragment(
                ConfirmNewPinFragment(),
                UpdatePinActivity.rootId,
                "ConfirmNewPinFragment"
            )
        }
        enableButton()
    }

    private fun enableButton() {
        val enable = binding.etPinCode.length() == 6
        if (enable) {
            with(binding.tvNext) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, requireContext().theme)

                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                isEnabled = true
            }
        } else {
            with(binding.tvNext) {
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
}