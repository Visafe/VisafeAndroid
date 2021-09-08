package vn.ncsc.visafe.ui.protect

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentScanActionBinding

class ScanActionFragment : BaseFragment<FragmentScanActionBinding>() {

    companion object {
        private const val TYPE_KEY = "TYPE_KEY"
        fun newInstance(type: String): ScanActionFragment {
            val fragment = ScanActionFragment()
            fragment.arguments = bundleOf(
                Pair(TYPE_KEY, type)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_scan_action

    override fun initView() {
        val type = arguments?.getString(TYPE_KEY, "") ?: ""
        if (type.isNotEmpty()) {
            when (type) {
                AdvancedScanActivity.TYPE_PROTECT_DEVICE -> {
                    binding.tvTitle.text = "CHẾ ĐỘ BẢO VỆ"
                    binding.tvDescription.text = "Đang bật chế độ bảo vệ ViSafe"
                    binding.ivLogo.setBackgroundResource(R.drawable.ic_group_protect_device)
                }
                AdvancedScanActivity.TYPE_PROTECT_WIFI -> {
                    binding.tvTitle.text = "BẢO VỆ WI-FI"
                    binding.tvDescription.text = "Đang kiểm tra “Pit Studio 5GHz”"
                    binding.ivLogo.setBackgroundResource(R.drawable.ic_group_protect_wifi)
                }
                AdvancedScanActivity.TYPE_BLOCK_ADS -> {
                    binding.tvTitle.text = "PHƯƠNG THỨC BẢO VỆ"
                    binding.tvDescription.text = "Đang thiết lập các phương thức bảo vệ"
                    binding.ivLogo.setBackgroundResource(R.drawable.ic_group_block_ads)
                }
                AdvancedScanActivity.TYPE_BLOCK_TRACKING -> {
                    binding.tvTitle.text = "HỆ ĐIỀU HÀNH"
                    binding.tvDescription.text = "Đang kiểm tra phiên bản hệ điều hành"
                    binding.ivLogo.setBackgroundResource(R.drawable.ic_group_block_tracking)
                }
            }
        }
    }
}