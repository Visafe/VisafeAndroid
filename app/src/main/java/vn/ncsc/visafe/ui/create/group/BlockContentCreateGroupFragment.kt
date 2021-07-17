package vn.ncsc.visafe.ui.create.group

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentBlockContentCreateGroupBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class BlockContentCreateGroupFragment : BaseFragment<FragmentBlockContentCreateGroupBinding>() {

    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
        fun newInstance(selected: Boolean, onSaveBlockContent: OnSaveBlockContent): BlockContentCreateGroupFragment {
            val args = Bundle()
            args.putBoolean(KEY_SELECTED, selected)
            val fragment = BlockContentCreateGroupFragment()
            fragment.arguments = args
            fragment.onSaveBlockContent = onSaveBlockContent
            return fragment
        }
    }

    private var isSelected: Boolean = false
    private lateinit var onSaveBlockContent: OnSaveBlockContent
    private var createGroupActivity: CreateGroupActivity? = null

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

    override fun layoutRes(): Int = R.layout.fragment_block_content_create_group

    override fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                createGroupActivity?.onBackPressed()
            }
        })
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
            createGroupActivity?.createGroupRequest?.porn_enabled = binding.itemLimit.isChecked()
            if (binding.itemLimit.getDataListSubject()?.size == 2) {
                createGroupActivity?.createGroupRequest?.safesearch_enabled = true
                createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = true
            } else if (binding.itemLimit.getDataListSubject()?.size == 1) {
                binding.itemLimit.getDataListSubject()?.let {
                    for (i in it) {
                        if (i == "google") {
                            createGroupActivity?.createGroupRequest?.safesearch_enabled = true
                        } else {
                            createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = true
                        }
                    }
                }
            }
            createGroupActivity?.createGroupRequest?.gambling_enabled = binding.itemSensitive.isChecked()
            createGroupActivity?.createGroupRequest?.bypass_enabled = binding.itemByPass.isChecked()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.onBackPressed()
        }
        setCheckedForAll(isSelected)
        binding.btnReset.setOnSingClickListener {
            setCheckedForAll(false)
        }
    }

    private fun setCheckedForAll(isSelected: Boolean) {
        binding.itemLimit.setChecked(isSelected)
        binding.itemSensitive.setChecked(isSelected)
        binding.itemByPass.setChecked(isSelected)
    }

    interface OnSaveBlockContent {
        fun onSaveBlockContent(isSave: Boolean)
    }
}