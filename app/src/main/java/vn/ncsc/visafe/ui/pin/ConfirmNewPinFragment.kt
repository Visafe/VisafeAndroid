package vn.ncsc.visafe.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentConfirmNewPinBinding
import vn.ncsc.visafe.utils.*

class ConfirmNewPinFragment : BaseFragment<FragmentConfirmNewPinBinding>() {

    companion object {
        const val PIN_KEY = "PIN_KEY"

        fun newInstance(pin: String): ConfirmNewPinFragment {
            val fragment = ConfirmNewPinFragment()
            fragment.arguments = bundleOf(
                Pair(PIN_KEY, pin)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_confirm_new_pin

    override fun initView() {
        val pin = arguments?.getString(PIN_KEY, "") ?: ""
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
                    if (pin == binding.etPinCode.text.toString().trim()) {
                        binding.etPinCode.setText("")
                        ViSafeApp().getPreference().putString(PreferenceKey.PIN_CODE, pin)
                        EventUtils.isCreatePass.value = true
                        (activity as UpdatePinActivity).finish()
                    } else {
                        showAlert("Thông báo", "Mã pin vừa nhập không trùng khớp!") {
                            binding.etPinCode.setText("")
                        }
                    }
                }
            }

        })
        binding.btnDeletePin.setOnSingClickListener {
            ViSafeApp().getPreference().putString(PreferenceKey.PIN_CODE, "")
            EventUtils.isCreatePass.value = false
            activity?.finish()
        }
        setHideKeyboardFocus(binding.root)
    }
}