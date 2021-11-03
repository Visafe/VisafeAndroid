package vn.ncsc.visafe.ui.upgrade

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentCurrentVersionBinding
import vn.ncsc.visafe.model.response.AllPackageResponse
import vn.ncsc.visafe.ui.adapter.UpgradeAdapter
import vn.ncsc.visafe.utils.*

class CurrentVersionFragment : BaseFragment<FragmentCurrentVersionBinding>() {

    companion object {
        fun newInstance(type: String, response: AllPackageResponse?): CurrentVersionFragment {
            val fragment = CurrentVersionFragment()
            fragment.arguments = bundleOf(
                Pair(UpgradeActivity.UPGRADE_KEY, type),
                Pair(UpgradeActivity.DATA_PACKAGE, response)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_current_version

    override fun initView() {
        val type = arguments?.getString(UpgradeActivity.UPGRADE_KEY, "") ?: ""
        if (type.isNotEmpty()) {
            when (type) {
                UpgradeActivity.TYPE_PREMIUM -> {
                    binding.tvUpgradeStatus.text = type
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FFB31F)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_0641448))
                    binding.tvUpgradeContent.text = getString(R.string.used_premium_content)
                    binding.rvData.adapter = UpgradeAdapter(premiumList(requireContext()))
                }
                UpgradeActivity.TYPE_FAMILY -> {
                    binding.tvUpgradeStatus.text = type
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_15A1FA)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.used_family_content)
                    binding.rvData.adapter = UpgradeAdapter(familyList(requireContext()))
                }
                UpgradeActivity.TYPE_BUSINESS -> {
                    binding.tvUpgradeStatus.text = type
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FF4451)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.used_bussiness_content)
                    binding.rvData.adapter = UpgradeAdapter(businessList(requireContext()))
                }
            }
        }
        binding.tvBuyMore.setOnSingClickListener {
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_PACKAGE, UpgradeActivity.TYPE_REGISTER)
            startActivity(intent)
        }
    }
}