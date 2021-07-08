package com.vn.visafe_android.ui.create.group

import android.content.Context
import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentSetupCreateGroupBinding
import com.vn.visafe_android.ui.adapter.SetupCreateGroupAdapter
import com.vn.visafe_android.ui.create.group.access_manager.AccessManagerFragment
import com.vn.visafe_android.ui.create.group.block_tracking.BlockTracingAndAdsFragment

class SetupCreateGroupFragment : BaseFragment<FragmentSetupCreateGroupBinding>() {
    companion object {
        fun newInstance(): SetupCreateGroupFragment {
            val args = Bundle()

            val fragment = SetupCreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_setup_create_group

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
        val adapter = SetupCreateGroupAdapter(requireContext())
        binding.rvSetup.adapter = adapter
        adapter.onClickSetupGroup = object : SetupCreateGroupAdapter.OnClickSetupGroup {
            override fun onClickSetupGroup(data: SetupCreateGroup) {
                when (data) {
                    SetupCreateGroup.CHAN_QUANG_CAO -> {
                        createGroupActivity?.addFragment(BlockTracingAndAdsFragment.newInstance())
                    }
                    SetupCreateGroup.CHAN_THEO_DOI -> {
                        createGroupActivity?.addFragment(BlockFollowCreateGroupFragment.newInstance())
                    }
                    SetupCreateGroup.CHAN_TRUY_CAP -> {
                        createGroupActivity?.addFragment(AccessManagerFragment.newInstance())
                    }
                    SetupCreateGroup.CHAN_NOI_DUNG -> {
                        createGroupActivity?.addFragment(BlockContentCreateGroupFragment.newInstance())
                    }
                    else -> return
                }
            }

        }
        //Lấy data
//        adapter.dataList
        binding.btnComplete.setOnClickListener {
            createGroupActivity?.doCreateGroup()
        }
    }
}

enum class SetupCreateGroup(
    var image: Int,
    var title: Int,
    var content: Int,
    var isSelected: Boolean,
    var isHighSetup: Boolean
) {
    CHAN_QUANG_CAO(
        R.drawable.ic_chan_quang_cao,
        R.string.chan_quang_cao,
        R.string.chan_quang_cao_content,
        true,
        true
    ),
    CHAN_THEO_DOI(
        R.drawable.ic_chan_theo_doi,
        R.string.chan_theo_doi,
        R.string.chan_theo_doi_content,
        true,
        true
    ),
    CHAN_TRUY_CAP(
        R.drawable.ic_chan_truy_cap,
        R.string.chan_truy_cap,
        R.string.chan_truy_cap_content,
        true,
        true
    ),
    CHAN_NOI_DUNG(
        R.drawable.ic_chan_noi_dung,
        R.string.chan_noi_dung,
        R.string.chan_noi_dung_content,
        true,
        true
    ),
    CHAN_VPN_PROXY(
        R.drawable.ic_chan_vpn,
        R.string.chan_vpn,
        R.string.chan_vpn_content,
        true,
        false
    )
}