package com.vn.visafe_android.ui.create.group

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentBlockFollowCreateGroupBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.block_tracking.BlockTracingAndAdsFragment
import com.vn.visafe_android.utils.setOnSingClickListener

class BlockFollowCreateGroupFragment : BaseFragment<FragmentBlockFollowCreateGroupBinding>() {
    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
        fun newInstance(
            selected: Boolean,
            onSaveBlockFollowCreateGroup: OnSaveBlockFollowCreateGroup
        ): BlockFollowCreateGroupFragment {
            val args = Bundle()
            args.putBoolean(KEY_SELECTED, selected)
            val fragment = BlockFollowCreateGroupFragment()
            fragment.arguments = args
            fragment.onSaveBlockFollowCreateGroup = onSaveBlockFollowCreateGroup
            return fragment
        }
    }

    private var isSelected: Boolean = false
    private lateinit var onSaveBlockFollowCreateGroup: OnSaveBlockFollowCreateGroup
    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_block_follow_create_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isSelected = it.getBoolean(KEY_SELECTED)
        }
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
        binding.btnReset.setOnClickListener {
            binding.itemBlockTrackingDevice.setChecked(false)
        }
        binding.btnSave.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.native_tracking =
                binding.itemBlockTrackingDevice.getDataListSubject()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
            onSaveBlockFollowCreateGroup.onSaveBlockFollowCreateGroup(true)
        }

        binding.itemBlockTrackingDevice.setData(
            arrayListOf(
                Subject(
                    "Alexa",
                    "alexa",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Apple",
                    "apple",
                    R.drawable.ic_apple
                ),
                Subject(
                    "Huawei",
                    "huawei",
                    R.drawable.ic_huawei
                ),
                Subject(
                    "Roku",
                    "roku",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Samsung",
                    "samsung",
                    R.drawable.ic_samsung
                ),
                Subject(
                    "Sonos",
                    "sonos",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Windows",
                    "windows",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Xiaomi",
                    "xiaomi",
                    R.drawable.ic_xiaomi
                )
            )
        )
        binding.itemBlockTrackingDevice.setExpanded(false)
        binding.itemBlockTrackingDevice.disableExpanded()
        binding.itemBlockTrackingDevice.setChecked(isSelected)
    }

    interface OnSaveBlockFollowCreateGroup {
        fun onSaveBlockFollowCreateGroup(isSave: Boolean)
    }
}