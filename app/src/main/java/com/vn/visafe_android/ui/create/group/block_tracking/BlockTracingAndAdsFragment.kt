package com.vn.visafe_android.ui.create.group.block_tracking

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentBlockTracingAndAdsBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.access_manager.AccessManagerFragment
import com.vn.visafe_android.utils.setOnSingClickListener

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
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
        binding.btnSave.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.adblock_enabled = binding.itemBlockAdsWeb.isChecked()
            createGroupActivity?.createGroupRequest?.game_ads_enabled = binding.itemBlockAdsGame.isChecked()
            createGroupActivity?.createGroupRequest?.app_ads = binding.itemBlockAdsApp.getDataListSubject()
//            createGroupActivity?.createGroupRequest?.native_tracking = binding.itemBlockTrackingDevice.getDataListSubject()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
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
        binding.itemBlockAdsApp.setExpanded(false)
        binding.itemBlockAdsApp.disableExpanded()
    }

}