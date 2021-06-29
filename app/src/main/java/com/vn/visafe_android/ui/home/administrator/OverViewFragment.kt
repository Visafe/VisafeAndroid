package com.vn.visafe_android.ui.home.administrator

import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentOverViewBinding
import com.vn.visafe_android.model.ApplicationMostData
import com.vn.visafe_android.model.ContentMostData
import com.vn.visafe_android.model.DeviceMostData
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.adapter.ApplicationMostAdapter
import com.vn.visafe_android.ui.adapter.ContentMostAdapter
import com.vn.visafe_android.ui.adapter.DeviceMostAdapter
import com.vn.visafe_android.ui.adapter.GroupListAdapter

class OverViewFragment : BaseFragment<FragmentOverViewBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = OverViewFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_over_view

    override fun initView() {
        val contentAdapter = ContentMostAdapter(createContentList())
        binding.rvContentMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContentMost.adapter = contentAdapter

        val deviceAdapter = DeviceMostAdapter(createDeviceList())
        binding.rvDeviceMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDeviceMost.adapter = deviceAdapter

        val appAdapter = ApplicationMostAdapter(createAppList())
        binding.rvAppMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAppMost.adapter = appAdapter

        val groupAdapter = GroupListAdapter(createGroupList())
        binding.rvGroup.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGroup.adapter = groupAdapter
    }

    private fun createContentList() : List<ContentMostData> {
        val list : ArrayList<ContentMostData> = ArrayList()
        list.add(ContentMostData("Mạng xã hội", 80, "1h"))
        list.add(ContentMostData("Chơi game", 60, "32p"))
        list.add(ContentMostData("Trình duyệt web", 20, "32s"))
        return list
    }

    private fun createAppList() : List<ApplicationMostData> {
        val list : ArrayList<ApplicationMostData> = ArrayList()
        list.add(ApplicationMostData("Instagram", 80, "1h"))
        list.add(ApplicationMostData("Google Map", 60, "32p"))
        list.add(ApplicationMostData("Book", 20, "32s"))
        list.add(ApplicationMostData("AppStore", 20, "32s"))
        return list
    }

    private fun createDeviceList() : List<DeviceMostData> {
        val list : ArrayList<DeviceMostData> = ArrayList()
        list.add(DeviceMostData("Iphone XS - Nguyễn Văn A", 80, "1h"))
        list.add(DeviceMostData("Ipad - Nguyễn Văn B", 60, "32p"))
        list.add(DeviceMostData("Iphone", 20, "32s"))
        list.add(DeviceMostData("Samsung", 20, "32s"))
        return list
    }

    private fun createGroupList() : List<GroupData> {
        val list : ArrayList<GroupData> = ArrayList()
        list.add(GroupData("Phòng 1: Marketing", 80, 15))
        list.add(GroupData("Phòng 2: CNTT", 60, 14))
        list.add(GroupData("Phòng 3: Kinh tế - Đối ngoại", 20, 13))
        list.add(GroupData("Phòng 4: Chuyên gia", 20, 13))
        return list
    }
}