package com.vn.visafe_android.ui.create.group

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentBlockFollowCreateGroupBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.access_manager.AccessManagerFragment
import com.vn.visafe_android.utils.setOnSingClickListener

class BlockFollowCreateGroupFragment : BaseFragment<FragmentBlockFollowCreateGroupBinding>() {
    companion object {
        fun newInstance(): BlockFollowCreateGroupFragment {
            val args = Bundle()

            val fragment = BlockFollowCreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_block_follow_create_group

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
            createGroupActivity?.createGroupRequest?.native_tracking =
                binding.itemBlockTrackingDevice.getDataListSubject()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.addFragment(AccessManagerFragment.newInstance())
        }

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
        binding.itemBlockTrackingDevice.setExpanded(false)
        binding.itemBlockTrackingDevice.disableExpanded()
    }
}