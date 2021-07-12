package vn.ncsc.visafe.ui.create.group.protected_group

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.create.group.SetupCreateGroupFragment
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentProtectedGroupBinding

class ProtectedGroupFragment : BaseFragment<FragmentProtectedGroupBinding>() {
    companion object {
        fun newInstance(): ProtectedGroupFragment {
            val args = Bundle()

            val fragment = ProtectedGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_protected_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
        enableButton()
        binding.tvContent.text = HtmlCompat.fromHtml(getString(R.string.protect_group_content), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.editNameGroup.addTextChangedListener {
            enableButton()
        }

        binding.tvNext.setOnClickListener {
            createGroupActivity?.createGroupRequest?.name = binding.editNameGroup.text.toString()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.addFragment(SetupCreateGroupFragment.newInstance())
            hiddenKeyboard()
        }
    }

    private fun enableButton() {
        val groupName = binding.editNameGroup.text.toString()
        if (groupName.isNotBlank()) {
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