package vn.ncsc.visafe.ui.create.group

import android.content.Context
import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentSetupCreateGroupBinding
import vn.ncsc.visafe.ui.adapter.SetupCreateGroupAdapter
import vn.ncsc.visafe.ui.create.group.access_manager.AccessManagerFragment
import vn.ncsc.visafe.ui.create.group.block_tracking.BlockTracingAdsCreateGroupFragment
import vn.ncsc.visafe.utils.OnSingleClickListener

class SetupCreateGroupFragment : BaseFragment<FragmentSetupCreateGroupBinding>(),
    BlockTracingAdsCreateGroupFragment.OnSaveBlockTrackingAndAds, BlockFollowCreateGroupFragment.OnSaveBlockFollowCreateGroup,
    AccessManagerFragment.OnSaveAccessManager, BlockContentCreateGroupFragment.OnSaveBlockContent {
    companion object {
        fun newInstance(): SetupCreateGroupFragment {
            val args = Bundle()

            val fragment = SetupCreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var isSaveBlockTracingAndAds = false
    private var isSaveBlockFollowCreateGroup = false
    private var isSaveAccessManager = false
    private var isSaveBlockContent = false
    private var createGroupActivity: CreateGroupActivity? = null
    private var adapter: SetupCreateGroupAdapter? = null

    override fun layoutRes(): Int = R.layout.fragment_setup_create_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                createGroupActivity?.onBackPressed()
            }
        })
        adapter = SetupCreateGroupAdapter(requireContext())
        binding.rvSetup.adapter = adapter
        adapter?.onClickSetupGroup = object : SetupCreateGroupAdapter.OnClickSetupGroup {
            override fun onClickSetupGroup(group: SetupCreateGroup) {
                when (group) {
                    SetupCreateGroup.CHAN_QUANG_CAO -> {
                        createGroupActivity?.addFragment(
                            BlockTracingAdsCreateGroupFragment.newInstance(
                                group.isSelected,
                                this@SetupCreateGroupFragment
                            )
                        )
                    }
                    SetupCreateGroup.CHAN_THEO_DOI -> {
                        createGroupActivity?.addFragment(
                            BlockFollowCreateGroupFragment.newInstance(
                                group.isSelected,
                                this@SetupCreateGroupFragment
                            )
                        )
                    }
                    SetupCreateGroup.CHAN_TRUY_CAP -> {
                        createGroupActivity?.addFragment(
                            AccessManagerFragment.newInstance(
                                group.isSelected,
                                this@SetupCreateGroupFragment
                            )
                        )
                    }
                    SetupCreateGroup.CHAN_NOI_DUNG -> {
                        createGroupActivity?.addFragment(
                            BlockContentCreateGroupFragment.newInstance(
                                group.isSelected,
                                this@SetupCreateGroupFragment
                            )
                        )
                    }
                    else -> return
                }
            }

        }
        //Lấy data
        binding.btnComplete.setOnClickListener {
            adapter?.let {
                for (group in it.dataList) {
                    when (group) {
                        SetupCreateGroup.CHAN_QUANG_CAO -> {
                            if (!isSaveBlockTracingAndAds) {
                                createGroupActivity?.createGroupRequest?.adblock_enabled = group.isSelected
                                createGroupActivity?.createGroupRequest?.game_ads_enabled = group.isSelected
                                createGroupActivity?.createGroupRequest?.app_ads =
                                    if (group.isSelected) listOf("instagram", "youtube", "spotify", "facebook") else listOf()
                            }
                        }
                        SetupCreateGroup.CHAN_THEO_DOI -> {
                            if (!isSaveBlockFollowCreateGroup) {
                                createGroupActivity?.createGroupRequest?.native_tracking =
                                    if (group.isSelected) listOf(
                                        "alexa",
                                        "apple",
                                        "huawei",
                                        "roku",
                                        "samsung",
                                        "sonos",
                                        "windows",
                                        "xiaomi"
                                    ) else listOf()
                            }
                        }
                        SetupCreateGroup.CHAN_TRUY_CAP -> {
                            if (!isSaveAccessManager) {
                                createGroupActivity?.createGroupRequest?.blocked_services =
                                    if (group.isSelected) listOf(
                                        "facebook",
                                        "zalo",
                                        "tiktok",
                                        "instagram",
                                        "tinder",
                                        "twitter",
                                        "netflix",
                                        "reddit",
                                        "9gag",
                                        "discord"
                                    ) else listOf()
                                createGroupActivity?.createGroupRequest?.block_webs =
                                    if (group.isSelected) listOf(
                                        "https://www.youtube.com/",
                                        "https://www.facebook.com/",
                                        "https://gmail.com/",
                                        "https://www.youtube.com/"
                                    ) else listOf()
                            }
                        }
                        SetupCreateGroup.CHAN_NOI_DUNG -> {
                            if (!isSaveBlockContent) {
                                createGroupActivity?.createGroupRequest?.porn_enabled = group.isSelected
                                if (group.isSelected) {
                                    createGroupActivity?.createGroupRequest?.safesearch_enabled = true
                                    createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = true
                                } else {
                                    createGroupActivity?.createGroupRequest?.safesearch_enabled = false
                                    createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = false
                                }
                                createGroupActivity?.createGroupRequest?.gambling_enabled = group.isSelected

                            }
                        }
                        SetupCreateGroup.CHAN_VPN_PROXY -> {
                            createGroupActivity?.createGroupRequest?.bypass_enabled = group.isSelected
                        }

                    }
                }
            }
            createGroupActivity?.doCreateGroup()
        }
    }

    override fun onSaveBlockTrackingAndAds(isSave: Boolean) {
        isSaveBlockTracingAndAds = isSave
        SetupCreateGroup.CHAN_QUANG_CAO.isSelected = isSave
        adapter?.notifyDataSetChanged()
    }

    override fun onSaveBlockFollowCreateGroup(isSave: Boolean) {
        isSaveBlockFollowCreateGroup = isSave
        SetupCreateGroup.CHAN_THEO_DOI.isSelected = isSave
        adapter?.notifyDataSetChanged()
    }

    override fun onSaveAccessManager(isSave: Boolean) {
        isSaveAccessManager = isSave
        SetupCreateGroup.CHAN_TRUY_CAP.isSelected = isSave
        adapter?.notifyDataSetChanged()
    }

    override fun onSaveBlockContent(isSave: Boolean) {
        isSaveBlockContent = isSave
        SetupCreateGroup.CHAN_NOI_DUNG.isSelected = isSave
        adapter?.notifyDataSetChanged()
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
        true,
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