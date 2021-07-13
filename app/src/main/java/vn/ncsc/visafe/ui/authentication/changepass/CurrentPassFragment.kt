package vn.ncsc.visafe.ui.authentication.changepass

import android.content.Context
import android.view.View
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCurrentPassBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class CurrentPassFragment : BaseFragment<FragmentCurrentPassBinding>() {

    private var changePasswordActivity: ChangePasswordActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_current_pass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ChangePasswordActivity) {
            changePasswordActivity = context
        }
    }

    override fun initView() {
        enableButton()
        setHideKeyboardFocus(binding.root)
        binding.edtPass.addTextChangedListener {
            enableButton()
        }
        binding.btnNext.setOnSingClickListener {
            changePasswordActivity?.changePasswordRequest?.currentPassword = binding.edtPass.text.toString()
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
            binding.tvInputContent.text = String.format(
                getString(R.string.input_pass_for_account), if (it.email.isNullOrEmpty())
                    it.phoneNumber else it.email
            )
        }
    }

    private fun enableButton() {
        val pass = binding.edtPass.text.toString()
        if (pass.isNotBlank()) {
            with(binding.btnNext) {
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
            with(binding.btnNext) {
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
}