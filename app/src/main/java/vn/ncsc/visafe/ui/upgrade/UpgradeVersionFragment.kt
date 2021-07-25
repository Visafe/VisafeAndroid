package vn.ncsc.visafe.ui.upgrade

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentUpgradeVersionBinding
import vn.ncsc.visafe.ui.adapter.UpgradeAdapter
import vn.ncsc.visafe.utils.bussinessList
import vn.ncsc.visafe.utils.familyList
import vn.ncsc.visafe.utils.premiumList
import vn.ncsc.visafe.utils.setBackgroundTint

class UpgradeVersionFragment : BaseFragment<FragmentUpgradeVersionBinding>() {

    companion object {
        fun newInstance(type: String): UpgradeVersionFragment {
            val fragment = UpgradeVersionFragment()
            fragment.arguments = bundleOf(
                Pair(UpgradeActivity.UPGRADE_KEY, type)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_upgrade_version

    override fun initView() {
        val type = arguments?.getString(UpgradeActivity.UPGRADE_KEY, "") ?:""
        if (type.isNotEmpty()) {
            when (type) {
                UpgradeActivity.TYPE_PREMIUM -> {
                    binding.tvUpgradeStatus.text = getString(R.string.premium)
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FFB31F)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_0641448))
                    binding.tvUpgradeContent.text = getString(R.string.premium_content)
                    binding.tvSave.text = getString(R.string.save_64)
                    binding.tvPriceYear.text = getString(R.string.price_year)
                    binding.tvPriceMonth.text = getString(R.string.price_month)
                    binding.rvData.adapter = UpgradeAdapter(premiumList(requireContext()))
                }
                UpgradeActivity.TYPE_FAMILY -> {
                    binding.tvUpgradeStatus.text = "FAMILY"
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_15A1FA)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.family_content)
                    binding.tvSave.text = getString(R.string.save_64)
                    binding.tvPriceYear.text = getString(R.string.price_year_family)
                    binding.tvPriceMonth.text = getString(R.string.price_month_family)
                    binding.rvData.adapter = UpgradeAdapter(familyList(requireContext()))
                }
                UpgradeActivity.TYPE_BUSSINESS -> {
                    binding.tvUpgradeStatus.text = "BUSINESS"
                    binding.tvUpgradeStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_subtract_white, 0, 0, 0)
                    binding.tvUpgradeStatus.setBackgroundTint(R.color.color_FF4451)
                    binding.tvUpgradeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    binding.tvUpgradeContent.text = getString(R.string.bussiness_content)
                    binding.tvSave.text = getString(R.string.tiet_kiem_hon)
                    binding.tvPriceYear.text = getString(R.string.price_bussiness)
                    binding.tvPriceMonth.text = getString(R.string.price_bussiness)
                    binding.rvData.adapter = UpgradeAdapter(bussinessList(requireContext()))
                }
            }
        }
    }

}