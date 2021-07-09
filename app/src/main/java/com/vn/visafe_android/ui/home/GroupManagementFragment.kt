package com.vn.visafe_android.ui.home

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentGroupManagementBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.adapter.GroupListAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
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

    private var groupListAdapter: GroupListAdapter? = null
    private var listGroup: MutableList<GroupData?> = mutableListOf()
    private var workspaceGroupData: WorkspaceGroupData? = null

    override fun layoutRes(): Int = R.layout.fragment_group_management

    override fun initView() {
        groupListAdapter = GroupListAdapter(listGroup)
        groupListAdapter?.setEnableImageGroup(true)
        groupListAdapter?.onClickGroup = object : GroupListAdapter.OnClickGroup {
            override fun openGroup(data: GroupData) {

            }

            override fun onClickMore() {
            }
        }
        binding.rvGroup.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvGroup.adapter = groupListAdapter

        initControl()
        workspaceGroupData?.let { doGetGroupWithId(it) }
    }

    private fun initControl() {
        binding.btnCreateNewGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            intent.putExtra(DATA_WORKSPACE, workspaceGroupData)
            startActivity(intent)
        }

        binding.btnJoinGroup.setOnSingClickListener {

        }
    }


    private fun doGetGroupWithId(workspaceGroupData: WorkspaceGroupData) {
        showProgressDialog()
        val client = NetworkClient()
        val call = context?.let { client.client(context = it).doGetGroupWithId(workspaceGroupData.id) }
        call?.enqueue(BaseCallback(this, object : Callback<List<GroupData>> {
            override fun onResponse(
                call: Call<List<GroupData>>,
                response: Response<List<GroupData>>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            listGroup.addAll(it)
                            groupListAdapter?.notifyDataSetChanged()
                            binding.rvGroup.visibility = View.VISIBLE
                        } else {
                            binding.rvGroup.visibility = View.GONE
                            listGroup.addAll(it)
                            groupListAdapter?.notifyDataSetChanged()
                        }

                    }
                }

            }

            override fun onFailure(call: Call<List<GroupData>>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}