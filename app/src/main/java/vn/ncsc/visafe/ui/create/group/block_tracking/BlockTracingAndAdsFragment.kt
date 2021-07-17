package vn.ncsc.visafe.ui.create.group.block_tracking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentBlockTracingAndAdsBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class BlockTracingAndAdsFragment : BaseFragment<FragmentBlockTracingAndAdsBinding>() {
    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
        fun newInstance(selected: Boolean, onCallBack: OnSaveBlockTrackingAndAds): BlockTracingAndAdsFragment {
            val args = Bundle()
            args.putBoolean(KEY_SELECTED, selected)
            val fragment = BlockTracingAndAdsFragment()
            fragment.arguments = args
            fragment.onCallBack = onCallBack
            return fragment
        }
    }

    private lateinit var onCallBack: OnSaveBlockTrackingAndAds
    private var isSelected: Boolean = false
    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_block_tracing_and_ads

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

    private fun setCheckedForAll(isSelected: Boolean) {
        binding.itemBlockAdsWeb.setChecked(isSelected)
        binding.itemBlockAdsGame.setChecked(isSelected)
        binding.itemBlockAdsApp.setChecked(isSelected)
    }

    override fun initView() {
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
        setCheckedForAll(isSelected)
        initControl()
    }

    private fun initControl() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                createGroupActivity?.onBackPressed()
            }
        })
        binding.btnReset.setOnSingClickListener {
            setCheckedForAll(false)
        }
        binding.btnSave.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.adblock_enabled = binding.itemBlockAdsWeb.isChecked()
            createGroupActivity?.createGroupRequest?.game_ads_enabled = binding.itemBlockAdsGame.isChecked()
            createGroupActivity?.createGroupRequest?.app_ads = binding.itemBlockAdsApp.getDataListSubject()
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            onCallBack.onSaveBlockTrackingAndAds(
                binding.itemBlockAdsWeb.isChecked()
                        || binding.itemBlockAdsWeb.isChecked() || binding.itemBlockAdsApp.getDataListSubject() != null
            )
            createGroupActivity?.onBackPressed()
        }
    }

    interface OnSaveBlockTrackingAndAds {
        fun onSaveBlockTrackingAndAds(isSave: Boolean)
    }

}