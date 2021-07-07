package com.vn.visafe_android.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAccessManagerBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.time.TimeProtectionFragment
import com.vn.visafe_android.utils.setOnSingClickListener

class AccessManagerFragment : BaseFragment<FragmentAccessManagerBinding>() {

    companion object {
        fun newInstance(): AccessManagerFragment {
            val args = Bundle()

            val fragment = AccessManagerFragment()
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

    override fun layoutRes(): Int = R.layout.fragment_access_manager

    override fun initView() {
        val dataItemApp = arrayListOf(
            Subject(
                "Instagram",
                "instagram",
                R.drawable.ic_instagram
            ),
            Subject(
                "Google Map",
                "googlemap",
                R.drawable.ic_google_map
            ),
            Subject(
                "Book",
                "book",
                R.drawable.ic_book
            ),
            Subject(
                "App Store",
                "appstore",
                R.drawable.ic_appstore
            ),
            Subject(
                "Facebook",
                "facebook",
                R.drawable.ic_facebook
            )
        )

        val dataItemWeb = arrayListOf(
            Subject(
                "https://www.youtube.com/",
                "https://www.youtube.com/",
                R.drawable.ic_instagram
            ),
            Subject(
                "https://www.facebook.com/",
                "https://www.facebook.com/",
                R.drawable.ic_google_map
            ),
            Subject(
                "https://gmail.com/",
                "https://gmail.com/",
                R.drawable.ic_book
            )
        )

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
        binding.itemApp.setData(dataItemApp)
        binding.itemWeb.setData(dataItemWeb)
        binding.itemLimit.setData(dataItemLimit)

        binding.tvNext.setOnSingClickListener {
            createGroupActivity?.createGroupRequest?.blocked_services = binding.itemApp.getDataListSubject()
            createGroupActivity?.createGroupRequest?.block_webs = binding.itemWeb.getDataListBlockWeb()
            if (binding.itemLimit.isChecked()) {
                createGroupActivity?.createGroupRequest?.safesearch_enabled = binding.itemLimit.isChecked()
                createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = binding.itemLimit.isChecked()
            } else {
                createGroupActivity?.createGroupRequest?.safesearch_enabled = binding.itemLimit.getDataListSubject()?.equals("google")
                createGroupActivity?.createGroupRequest?.youtuberestrict_enabled = binding.itemLimit.getDataListSubject()?.equals("youtube")
            }


            createGroupActivity?.createGroupRequest?.porn_enabled = binding.itemSensitive.isChecked()
            createGroupActivity?.createGroupRequest?.bypass_enabled = binding.itemByPass.isChecked()
            val gson = Gson()
            Log.e(
                "AccessManagerFragment: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
//            createGroupActivity?.addFragment(TimeProtectionFragment.newInstance())
            createGroupActivity?.doCreateGroup()
        }
    }

}