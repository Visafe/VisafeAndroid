package vn.ncsc.visafe.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCreateNewPinBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.setOnSingClickListener
import vn.ncsc.visafe.utils.updateOTPCode

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
                ConfirmNewPinFragment.newInstance(binding.etPinCode.text.toString().trim()),
                UpdatePinActivity.rootId,
                "ConfirmNewPinFragment"
            )
        }
        binding.btnDeletePin.setOnSingClickListener {
            ViSafeApp().getPreference().putString(PreferenceKey.PIN_CODE, "")
            activity?.finish()
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