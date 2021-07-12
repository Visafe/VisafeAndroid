package vn.ncsc.visafe.ui.group.detail

import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentGroupDetailAccessManagementBinding
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.utils.setOnSingClickListener

class GroupDetailAccessManagementFragment :
    BaseFragment<FragmentGroupDetailAccessManagementBinding>() {


    override fun layoutRes(): Int = R.layout.fragment_group_detail_access_management

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

        binding.tvSave.setOnSingClickListener {
            activity?.finish()
        }
    }
}