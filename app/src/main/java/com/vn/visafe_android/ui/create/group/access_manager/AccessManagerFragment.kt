package com.vn.visafe_android.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAccessManagerBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.time.TimeProtectionFragment

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
        binding.tvSave.setOnClickListener {
            createGroupActivity?.addFragment(TimeProtectionFragment.newInstance())
        }
        val data = arrayListOf<Subject>(
            Subject(
                "Instagram",
                R.drawable.ic_instagram
            ),
            Subject(
                "Google Map",
                R.drawable.ic_google_map
            ),
            Subject(
                "Book",
                R.drawable.ic_book
            ),
            Subject(
                "App Store",
                R.drawable.ic_appstore
            ),
            Subject(
                "Facebook",
                R.drawable.ic_facebook
            )
        )

        binding.itemApp.setData(data)
        binding.itemGame.setData(data)
        binding.itemWeb.setData(data)
        binding.itemLimit.setData(data)
    }

}