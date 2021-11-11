package vn.ncsc.visafe.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCurrentPinBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.updateOTPCode

class CurrentPinFragment : BaseFragment<FragmentCurrentPinBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_current_pin

    override fun initView() {
        binding.etPinCode.requestFocus()
        showKeyboard()
        val pin = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
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
                        if (binding.etPinCode.text.toString().trim() == pin) {
                            (activity as UpdatePinActivity).handlerFragment(
                                CreateNewPinFragment(),
                                UpdatePinActivity.rootId,
                                "CreateNewPinFragment"
                            )
                            binding.etPinCode.setText("")
                        } else {
                            hiddenKeyboard()
                            showAlert("Thông báo", "Mã bảo vệ vừa nhập không đúng!") {
                                binding.etPinCode.setText("")
                            }
                        }
                }
            }

        })
        setHideKeyboardFocus(binding.root)
    }

}