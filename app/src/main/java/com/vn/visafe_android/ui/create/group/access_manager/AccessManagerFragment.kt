package com.vn.visafe_android.ui.create.group.access_manager

import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAccessManagerBinding
import com.vn.visafe_android.model.Subject

class AccessManagerFragment : BaseFragment<FragmentAccessManagerBinding>() {

    companion object {
        fun newInstance(): AccessManagerFragment {
            val args = Bundle()

            val fragment = AccessManagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_access_manager

    override fun initView() {
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