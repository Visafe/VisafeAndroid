package com.vn.visafe_android.ui.create.group

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentBlockContentCreateGroupBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.time.TimeProtectionFragment
import com.vn.visafe_android.utils.setOnSingClickListener

class BlockContentCreateGroupFragment : BaseFragment<FragmentBlockContentCreateGroupBinding>() {

    companion object {
        fun newInstance(): BlockContentCreateGroupFragment {
            val args = Bundle()

            val fragment = BlockContentCreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_block_content_create_group

    override fun initView() {
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
        val dataItemLimit = arrayListOf(
            Subject(
                "Google tìm kiếm",
                "google",
                R.drawable.ic_logo_social_google
            ),
            Subject(
                "Youtube",
                "youtube",
                R.drawable.ic_youtube
            )
        )
        binding.itemLimit.setData(dataItemLimit)
        binding.itemLimit.disableExpanded()
        binding.itemLimit.setExpanded(false)
        binding.itemByPass.disableExpanded()
        binding.itemSensitive.disableExpanded()

        binding.btnSave.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.safesearch_enabled = binding.itemLimit.isChecked()
            createGroupActivity?.createGroupRequest?.porn_enabled = binding.itemSensitive.isChecked()
            createGroupActivity?.createGroupRequest?.bypass_enabled = binding.itemByPass.isChecked()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
        }
    }
}