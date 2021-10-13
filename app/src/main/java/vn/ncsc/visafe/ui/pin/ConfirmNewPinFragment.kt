package vn.ncsc.visafe.ui.pin

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentConfirmNewPinBinding
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.utils.*

class ConfirmNewPinFragment : BaseFragment<FragmentConfirmNewPinBinding>() {

    companion object {
        const val PIN_KEY = "PIN_KEY"
        const val TYPE_CONFIRM_PIN = "TYPE_CONFIRM_PIN"

        fun newInstance(pin: String): ConfirmNewPinFragment {
            val fragment = ConfirmNewPinFragment()
            fragment.arguments = bundleOf(
                Pair(PIN_KEY, pin)
            )
            return fragment
        }

        /*
        * type = 1 : delete pin
        * type = 2 : confirm pin
        * */
        fun newInstance(type: Int): ConfirmNewPinFragment {
            val fragment = ConfirmNewPinFragment()
            fragment.arguments = bundleOf(
                Pair(TYPE_CONFIRM_PIN, type)
            )
            return fragment
        }
    }

    private var mType: Int = 0
    private var pinInput = ""

    override fun layoutRes(): Int = R.layout.fragment_confirm_new_pin

    override fun initView() {
        binding.etPinCode.requestFocus()
        showKeyboard()
        arguments?.let {
            pinInput = it.getString(PIN_KEY, "") ?: ""
            mType = it.getInt(TYPE_CONFIRM_PIN)
        }

        when (mType) {
            1 -> {
                binding.toolbar.setTitleToolbar("Xóa mã PIN bảo vệ")
                binding.tvTitle.text = "Nhập mã PIN hiện tại"
                binding.btnDeletePin.visibility = View.GONE
            }
            2 -> {
                binding.toolbar.setTitleToolbar("Xác nhận PIN bảo vệ")
                binding.tvTitle.text = "Nhập mã PIN để xác nhận"
                binding.btnDeletePin.visibility = View.GONE
            }
            else -> {
                val pinSaved = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
                binding.toolbar.setTitleToolbar(if (pinSaved.isEmpty()) "Cài đặt mã pin bảo vệ" else "Đổi mã pin bảo vệ")
                binding.tvTitle.text =
                    (if (pinSaved.isEmpty()) getString(R.string.input_new_pin_again) else "Thay đổi mã PIN bảo vệ")
                binding.btnDeletePin.visibility = if (pinSaved.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                if (mType == 2) {
                    activity?.let {
                        it.setResult(AppCompatActivity.RESULT_CANCELED)
                    }
                }
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
                    when (mType) {
                        1 -> {
                            showDialogDeleteComplete("Xóa mã pin thành công")
                        }
                        2 -> {
                            if (binding.etPinCode.text.toString() == ViSafeApp().getPreference()
                                    .getString(PreferenceKey.PIN_CODE)
                            ) {
                                activity?.let {
                                    it.setResult(AppCompatActivity.RESULT_OK)
                                    (it as UpdatePinActivity).finish()
                                }
                            } else {
                                //báo lỗi sai pin
                            }
                        }
                        else -> {
                            if (pinInput == binding.etPinCode.text.toString().trim()) {
                                binding.etPinCode.setText("")
                                ViSafeApp().getPreference().putString(PreferenceKey.PIN_CODE, pinInput)
                                EventUtils.isCreatePass.value = true
                                showDialogComplete(getString(R.string.create_new_pin_success))
                            } else {
                                showAlert("Thông báo", "Mã pin vừa nhập không trùng khớp!") {
                                    binding.etPinCode.setText("")
                                }
                            }
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

    private fun showDialogComplete(title: String) {
        val dialog = SuccessDialogFragment.newInstance(
            title,
            ""
        )
        dialog.show(parentFragmentManager, "")
        dialog.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    (activity as UpdatePinActivity).finish()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogDeleteComplete(title: String) {
        val dialog = SuccessDialogFragment.newInstance(
            title,
            ""
        )
        dialog.show(parentFragmentManager, "")
        dialog.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    ViSafeApp().getPreference().putString(PreferenceKey.PIN_CODE, "")
                    EventUtils.isCreatePass.value = false
                    activity?.finish()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }
}