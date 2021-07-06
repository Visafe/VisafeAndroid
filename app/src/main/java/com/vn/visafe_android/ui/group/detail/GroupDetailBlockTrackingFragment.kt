package com.vn.visafe_android.ui.group.detail

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDetailBlockTrackingBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.utils.setOnSingClickListener

class GroupDetailBlockTrackingFragment : BaseFragment<FragmentGroupDetailBlockTrackingBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_group_detail_block_tracking

    override fun initView() {
        binding.tvSave.setOnSingClickListener {
            activity?.finish()
        }
        binding.itemBlockAdsApp.setData(
            arrayListOf(
                Subject(
                    "Instagram",
                    "instagram",
                    R.drawable.ic_instagram
                ),
                Subject(
                    "Youtube",
                    "youtube",
                    R.drawable.ic_youtube
                ),
                Subject(
                    "Spotify",
                    "spotify",
                    R.drawable.ic_spotify
                ),
                Subject(
                    "Facebook",
                    "facebook",
                    R.drawable.ic_facebook
                )
            )
        )

        binding.itemBlockTrackingDevice.setData(
            arrayListOf(
                Subject(
                    "Apple",
                    "apple",
                    R.drawable.ic_apple
                ),
                Subject(
                    "Samsung",
                    "samsung",
                    R.drawable.ic_samsung
                ),
                Subject(
                    "Xiaomi",
                    "xiaomi",
                    R.drawable.ic_xiaomi
                ),
                Subject(
                    "Huawei",
                    "huawei",
                    R.drawable.ic_huawei
                ),
                Subject(
                    "Microsoft",
                    "microsoft",
                    R.drawable.ic_microsoft
                ),
            )
        )
    }
}