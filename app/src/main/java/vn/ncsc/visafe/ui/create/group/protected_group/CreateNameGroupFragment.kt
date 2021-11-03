package vn.ncsc.visafe.ui.create.group.protected_group

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.create.group.SetupCreateGroupFragment
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentProtectedGroupBinding

class CreateNameGroupFragment : BaseFragment<FragmentProtectedGroupBinding>() {
    companion object {
        fun newInstance(): CreateNameGroupFragment {
            val args = Bundle()

            val fragment = CreateNameGroupFragment()
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
            if (binding.editNameGroup.length() in 4..99) {
                binding.editNameGroup.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext)
                binding.tvWarning.visibility = View.GONE
            } else {
                binding.editNameGroup.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_warning)
                binding.tvWarning.visibility = View.VISIBLE
            }
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
        if (groupName.isNotBlank() && binding.editNameGroup.length() in 4..99) {
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