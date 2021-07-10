package com.vn.visafe_android.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentCurrentPinBinding
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.updateOTPCode

class CurrentPinFragment : BaseFragment<FragmentCurrentPinBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_current_pin

    override fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                backFragment()
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
                if (binding.etPinCode.length() == 6) {
                    (activity as UpdatePinActivity).handlerFragment(
                        CreateNewPinFragment(),
                        UpdatePinActivity.rootId,
                        "CreateNewPinFragment"
                    )
                    binding.etPinCode.setText("")
                }
            }

        })
        setHideKeyboardFocus(binding.root)
    }

}