package com.vn.visafe_android.ui.home.administrator

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupManagementBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.adapter.GroupListAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity

class GroupManagementFragment : BaseFragment<FragmentGroupManagementBinding>() {

    companion object {

        @JvmStatic
        fun newInstance() =
            GroupManagementFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_group_management

    override fun initView() {
        val groupAdapter = GroupListAdapter(createGroupList())
        groupAdapter.setEnableImageGroup(true)
        groupAdapter.onClickGroup = object : GroupListAdapter.OnClickGroup {
            override fun openGroup(data: GroupData) {

            }

            override fun createGroup() {
                startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
            }

            override fun onClickMore() {
            }
        }
        binding.rvGroup.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGroup.adapter = groupAdapter
    }

    private fun createGroupList() : List<GroupData?> {
        val list : ArrayList<GroupData?> = ArrayList()
        list.add(GroupData("Phòng 1: Marketing", 80, 15, null))
        list.add(GroupData("Phòng 2: CNTT", 60, 14, null))
        list.add(GroupData("Phòng 3: Kinh tế - Đối ngoại", 20, 13, null))
        list.add(GroupData("Phòng 4: Chuyên gia", 20, 13, null))
        list.add(null)
        return list
    }
}