package com.vn.visafe_android.ui.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentGroupManagementBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.response.GroupsDataResponse
import com.vn.visafe_android.ui.adapter.GroupListAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.group.detail.GroupDetailActivity
import com.vn.visafe_android.ui.group.join.ScanQRJoinGroupActivity
import com.vn.visafe_android.utils.setOnSingClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupManagementFragment : BaseFragment<FragmentGroupManagementBinding>() {

    companion object {
        const val DATA_WORKSPACE = "DATA_WORKSPACE"

        @JvmStatic
        fun newInstance() =
            GroupManagementFragment()
    }

    private var resultLauncherCreateGroupActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                workspaceGroupData?.let { doGetGroupWithId(it) }
            }
        }

    private var resultLauncherDeleteGroupActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                workspaceGroupData?.let { doGetGroupWithId(it) }
            }
        }

    private var groupIsOwnerAdapter: GroupListAdapter? = null
    private var groupIsMemberAdapter: GroupListAdapter? = null
    private var workspaceGroupData: WorkspaceGroupData? = null
    private var listGroupIsOwner: MutableList<GroupData?> = mutableListOf()
    private var listGroupIsMember: MutableList<GroupData?> = mutableListOf()

    override fun layoutRes(): Int = R.layout.fragment_group_management

    override fun initView() {
        updateView()
        groupIsOwnerAdapter = GroupListAdapter(listGroupIsOwner)
        groupIsOwnerAdapter?.setEnableImageGroup(true)
        groupIsOwnerAdapter?.onClickGroup = object : GroupListAdapter.OnClickGroup {
            override fun openGroup(data: GroupData, position: Int) {
                data.groupid?.let { gotoDetailGroup(it, position) }
            }

            override fun onClickMore() {
            }
        }
        binding.rcvGroupIsOwner.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvGroupIsOwner.adapter = groupIsOwnerAdapter
        //group list member
        groupIsMemberAdapter = GroupListAdapter(listGroupIsMember)
        groupIsMemberAdapter?.setEnableImageGroup(true)
        groupIsMemberAdapter?.onClickGroup = object : GroupListAdapter.OnClickGroup {
            override fun openGroup(data: GroupData, position: Int) {
                data.groupid?.let { gotoDetailGroup(it, position) }
            }

            override fun onClickMore() {
            }
        }
        binding.rcvGroupIsMember.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvGroupIsMember.adapter = groupIsMemberAdapter

        initControl()
    }

    private fun initControl() {
        binding.btnCreateNewGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            intent.putExtra(DATA_WORKSPACE, workspaceGroupData)
            resultLauncherCreateGroupActivity.launch(intent)
        }

        binding.btnJoinGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), ScanQRJoinGroupActivity::class.java)
            intent.putExtra(ScanQRJoinGroupActivity.DATA_TITLE, "Quét mã QR tham gia nhóm")
            startActivity(intent)
        }
    }

    private fun gotoDetailGroup(groupId: String, position: Int) {
        val intent = Intent(context, GroupDetailActivity::class.java)
        intent.putExtra(GroupDetailActivity.DATA_ID, groupId)
        intent.putExtra(GroupDetailActivity.DATA_POSITION, position)
        resultLauncherDeleteGroupActivity.launch(intent)
    }

    private fun doGetGroupWithId(workspaceGroupData: WorkspaceGroupData) {
        showProgressDialog()
        val client = NetworkClient()
        val call = context?.let { client.client(context = it).doGetGroupsWithId(workspaceGroupData.id) }
        call?.enqueue(BaseCallback(this, object : Callback<GroupsDataResponse> {
            override fun onResponse(
                call: Call<GroupsDataResponse>,
                response: Response<GroupsDataResponse>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    listGroupIsOwner.clear()
                    listGroupIsMember.clear()
                    response.body()?.clients?.let {
                        for (group in it) {
                            if (group.isOwner == true) {
                                listGroupIsOwner.add(group)
                            } else {
                                listGroupIsMember.add(group)
                            }
                        }
                        groupIsOwnerAdapter?.notifyDataSetChanged()
                        groupIsMemberAdapter?.notifyDataSetChanged()
                    }
                    updateView()
                    Log.e("doGetGroupWithId", "onResponse: " + listGroupIsOwner.size + " | " + listGroupIsMember.size)
                }

            }

            override fun onFailure(call: Call<GroupsDataResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun updateView() {
        binding.tvGroupIsOwner.visibility = if (listGroupIsOwner.isEmpty()) View.GONE else View.VISIBLE
        binding.rcvGroupIsOwner.visibility = if (listGroupIsOwner.isEmpty()) View.GONE else View.VISIBLE
        binding.tvGroupIsMember.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
        binding.rcvGroupIsMember.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
        binding.viewLine.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
    }

    fun updateWorkspace(data: WorkspaceGroupData) {
        this.workspaceGroupData = data
        workspaceGroupData?.let { doGetGroupWithId(it) }
    }
}