package vn.ncsc.visafe.ui.upgrade

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCurrentVersionBinding
import vn.ncsc.visafe.ui.adapter.UpgradeAdapter
import vn.ncsc.visafe.utils.*

class CurrentVersionFragment : BaseFragment<FragmentCurrentVersionBinding>() {

    companion object {
        fun newInstance(type: String): CurrentVersionFragment {
            val fragment = CurrentVersionFragment()
            fragment.arguments = bundleOf(
                Pair(UpgradeActivity.UPGRADE_KEY, type)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_current_version

    override fun initView() {
        val type = arguments?.getString(UpgradeActivity.UPGRADE_KEY, "") ?:""
        if (type.isNotEmpty()) {
            when (type) {
                UpgradeActivity.TYPE_PREMIUM -> {
                    binding.tvUpgradeStatus.text = getString(R.string.premium)
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FFB31F)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_0641448))
                    binding.tvUpgradeContent.text = getString(R.string.used_premium_content)
                    binding.rvData.adapter = UpgradeAdapter(premiumList(requireContext()))
                }
                UpgradeActivity.TYPE_FAMILY -> {
                    binding.tvUpgradeStatus.text = "FAMILY"
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_15A1FA)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.used_family_content)
                    binding.rvData.adapter = UpgradeAdapter(familyList(requireContext()))
                }
                UpgradeActivity.TYPE_BUSSINESS -> {
                    binding.tvUpgradeStatus.text = "BUSINESS"
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FF4451)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.used_bussiness_content)
                    binding.rvData.adapter = UpgradeAdapter(bussinessList(requireContext()))
                }
            }
        }
        binding.tvBuyMore.setOnSingClickListener {
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_VERSION_KEY, UpgradeActivity.TYPE_REGISTER)
            startActivity(intent)
        }
    }
}