package com.vn.visafe_android.ui.create.group.block_tracking

import android.content.Context
import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentBlockTracingAndAdsBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.access_manager.AccessManagerFragment

class BlockTracingAndAdsFragment : BaseFragment<FragmentBlockTracingAndAdsBinding>() {
    companion object {
        fun newInstance(): BlockTracingAndAdsFragment {
            val args = Bundle()

            val fragment = BlockTracingAndAdsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_block_tracing_and_ads

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }


    override fun initView() {
        binding.tvNext.setOnClickListener {
            createGroupActivity?.addFragment(AccessManagerFragment.newInstance())
        }
        binding.itemBlockAdsApp.setData(
            arrayListOf(
                Subject(
                    "Instagram",
                    R.drawable.ic_instagram
                ),
                Subject(
                    "Youtube",
                    R.drawable.ic_youtube
                ),
                Subject(
                    "Spotify",
                    R.drawable.ic_spotify
                ),
                Subject(
                    "Facebook",
                    R.drawable.ic_facebook
                )
            )
        )

        binding.itemBlockTrackingDevice.setData(
            arrayListOf(
                Subject(
                    "Apple",
                    R.drawable.ic_apple
                ),
                Subject(
                    "Samsung",
                    R.drawable.ic_samsung
                ),
                Subject(
                    "Xiaomi",
                    R.drawable.ic_xiaomi
                ),
                Subject(
                    "Huawei",
                    R.drawable.ic_huawei
                ),
                Subject(
                    "Microsoft",
                    R.drawable.ic_microsoft
                ),
            )
        )
    }

}