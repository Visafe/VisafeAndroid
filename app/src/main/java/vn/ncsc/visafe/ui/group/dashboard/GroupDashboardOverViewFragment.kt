package vn.ncsc.visafe.ui.group.dashboard

import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentGroupDashboardOverViewBinding
import vn.ncsc.visafe.model.ApplicationMostData
import vn.ncsc.visafe.model.GroupPeopleData
import vn.ncsc.visafe.model.GroupPeopleNotiData
import vn.ncsc.visafe.ui.adapter.ApplicationMostAdapter
import vn.ncsc.visafe.ui.adapter.GroupDashboardAdapter

class GroupDashboardOverViewFragment :
    BaseFragment<FragmentGroupDashboardOverViewBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_group_dashboard_over_view

    override fun initView() {
        val appAdapter = ApplicationMostAdapter(createAppList())
        binding.rvAppMost.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAppMost.adapter = appAdapter

        val groupAdapter = GroupDashboardAdapter(createGroupList())
        binding.rvPeople.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPeople.adapter = groupAdapter
    }

    private fun createAppList() : List<ApplicationMostData> {
        val list : ArrayList<ApplicationMostData> = ArrayList()
        list.add(ApplicationMostData("Instagram", 80, "1h"))
        list.add(ApplicationMostData("Google Map", 60, "32p"))
        list.add(ApplicationMostData("Book", 20, "32s"))
        list.add(ApplicationMostData("AppStore", 20, "32s"))
        return list
    }

    private fun createGroupList() : List<GroupPeopleData> {
        val list : ArrayList<GroupPeopleData> = ArrayList()
        val listNoti : ArrayList<GroupPeopleNotiData> = ArrayList()
        listNoti.add(GroupPeopleNotiData("Thiết bị Iphone X đã cố gắng truy cập trang", "www.facbook.com"))
        listNoti.add(GroupPeopleNotiData("Thiết bị Iphone X đã cố gắng truy cập trang", "www.facbook.com"))
        listNoti.add(GroupPeopleNotiData("Thiết bị Iphone X đã cố gắng truy cập trang", "www.facbook.com"))
        listNoti.add(GroupPeopleNotiData("Thiết bị Iphone X đã cố gắng truy cập trang", "www.facbook.com"))
        list.add(GroupPeopleData("Iphone X - Nguyễn Văn A", 5, 10, 15, 20, listNoti))
        list.add(GroupPeopleData("Iphone XS - Nguyễn Văn B", 15, 20, 155, 22, listNoti))
        return list
    }
}