package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentGroupManagementBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.TYPE_WORKSPACES
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.request.DeleteWorkSpaceRequest
import vn.ncsc.visafe.model.request.UpdateNameWorkspaceRequest
import vn.ncsc.visafe.model.response.GroupsDataResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.adapter.GroupListAdapter
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.create.workspace.CreateWorkspaceActivity
import vn.ncsc.visafe.ui.dialog.AccountTypeDialogBottomSheet
import vn.ncsc.visafe.ui.dialog.DisplayStatisticalForTimeBottomSheet
import vn.ncsc.visafe.ui.dialog.OnClickItemAccountType
import vn.ncsc.visafe.ui.dialog.OnClickItemTime
import vn.ncsc.visafe.ui.group.detail.GroupDetailActivity
import vn.ncsc.visafe.ui.group.join.ScanQRJoinGroupActivity
import vn.ncsc.visafe.utils.setOnSingClickListener

class GroupManagementFragment : BaseFragment<FragmentGroupManagementBinding>() {

    private var groupIsOwnerAdapter: GroupListAdapter? = null
    private var groupIsMemberAdapter: GroupListAdapter? = null
    private var mWorkspaceGroupData: WorkspaceGroupData? = null
    private var listGroupIsOwner: MutableList<GroupData?> = mutableListOf()
    private var listGroupIsMember: MutableList<GroupData?> = mutableListOf()
    private var positionTypeChoose = 0
    private var bottomSheet: AccountTypeDialogBottomSheet? = null
    private var listWorkspaceGroupData: MutableList<WorkspaceGroupData> = mutableListOf()
    private var timeStatistical: String = TimeStatistical.HANG_NGAY.value
    private var timeType: String = TimeStatistical.HANG_NGAY.time

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
                mWorkspaceGroupData?.let { doGetGroupWithId(it) }
            }
        }

    private var resultLauncherDeleteGroupActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                mWorkspaceGroupData?.let { doGetGroupWithId(it) }
            }
        }

    private var resultLauncherCreateWorkspaceActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val newWorkspace = it.getParcelableExtra<WorkspaceGroupData>("NewWorkSpace")
                    newWorkspace?.let { it1 -> listWorkspaceGroupData.add(it1) }
                }
            }
        }

    override fun layoutRes(): Int = R.layout.fragment_group_management

    @SuppressLint("LongLogTag")
    override fun initView() {
        if ((activity as BaseActivity).isLogin()) {
            (activity as MainActivity).timeTypes.observe(this, {
                if (it.isNotEmpty()) {
                    timeType = it
                    binding.viewStatistical.tvTime.text = it
                }
            })
            (activity as MainActivity).listWorkSpaceLiveData.observe(this, {
                if (it != null) {
                    listWorkspaceGroupData.clear()
                    listWorkspaceGroupData.addAll(it)
                    if (listWorkspaceGroupData.size > 0) {
                        listWorkspaceGroupData[0].let { workspaceGroupData ->
                            mWorkspaceGroupData = workspaceGroupData
                            doGetGroupWithId(workspaceGroupData)
                            updateViewWorkSpace(workspaceGroupData)
                            (activity as MainActivity).doGetStaticWorkspace(workspaceGroupData, timeStatistical)
                        }
                    }
                }
            })
            (activity as MainActivity).statisticalWorkSpaceLiveData.observe(this, {
                binding.viewStatistical.tvValueDangerous.text = it.num_dangerous_domain.toString()
                binding.viewStatistical.tvValueAds.text = it.num_ads_blocked.toString()
                binding.viewStatistical.tvValueViolate.text = it.num_violation.toString()
            })
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
            binding.cardViewStatistical.visibility = View.VISIBLE
            binding.view1.visibility = View.GONE
            binding.btnChangeWorkSpace.visibility = View.VISIBLE
        } else {
            binding.cardViewStatistical.visibility = View.GONE
            binding.view1.visibility = View.VISIBLE
            binding.btnChangeWorkSpace.visibility = View.GONE
            binding.tvGroupName.text = "Gia đình & nhóm"
            binding.tvGroupDescription.text = "Bảo vệ gia đình & người thân trên môi trường mạng"
        }
        updateViewListGroup()
        initControl()
    }

    private fun updateViewWorkSpace(workspaceGroupData: WorkspaceGroupData) {
        val typeWorkspaces = TYPE_WORKSPACES.fromIsTypeWorkSpaces(workspaceGroupData.type)
        typeWorkspaces?.let { type ->
            binding.tvGroupName.text = workspaceGroupData.name
            binding.tvGroupDescription.text = type.content
            binding.bgTop.background = context?.let { ContextCompat.getDrawable(it, type.resDrawableBgTop) }
        }
    }

    private fun initControl() {
        binding.btnCreateNewGroup.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_GROUP))
                return@setOnSingClickListener
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            intent.putExtra(DATA_WORKSPACE, mWorkspaceGroupData)
            resultLauncherCreateGroupActivity.launch(intent)
        }

        binding.btnJoinGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), ScanQRJoinGroupActivity::class.java)
            intent.putExtra(ScanQRJoinGroupActivity.DATA_TITLE, "Quét mã QR tham gia nhóm")
            startActivity(intent)
        }

        binding.btnChangeWorkSpace.setOnClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_GROUP))
                return@setOnClickListener
            bottomSheet = AccountTypeDialogBottomSheet.newInstance(listWorkspaceGroupData, positionTypeChoose)
            bottomSheet?.show(parentFragmentManager, null)
            bottomSheet?.onClickItemAccountType = object : OnClickItemAccountType {
                override fun onChoosse(data: WorkspaceGroupData, position: Int) {
                    positionTypeChoose = position
                    if (listWorkspaceGroupData.size > 0) {
                        listWorkspaceGroupData[position].let { workspaceGroupData ->
                            mWorkspaceGroupData = workspaceGroupData
                            doGetGroupWithId(workspaceGroupData)
                            updateViewWorkSpace(workspaceGroupData)
                            (activity as MainActivity).doGetStaticWorkspace(workspaceGroupData, timeStatistical)
                        }
                    }
                }

                override fun doDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
                    deleteWorkSpace(data, position)
                }

                override fun doUpdateNameWorkSpace(
                    data: WorkspaceGroupData,
                    name: String,
                    position: Int
                ) {
                    updateNameWorkSpace(data, name, position)
                }

                override fun add() {
                    if (listWorkspaceGroupData.size == 3) {
                        (activity as BaseActivity).showToast("Bạn đã tạo tối đa workspace")
                        return
                    }
                    val intentCreate = Intent(context, CreateWorkspaceActivity::class.java)
                    resultLauncherCreateWorkspaceActivity.launch(intentCreate)
                }

            }
        }

        binding.viewStatistical.tvTime.setOnSingClickListener {
            DisplayStatisticalForTimeBottomSheet(object : OnClickItemTime {
                override fun onClickItemTime(item: TimeStatistical) {
                    if (timeType == item.time)//không reload khi click lại ngày đang chọn
                        return
                    getDataStatistical(item)
                    (activity as MainActivity).timeTypes.value = item.time
                    binding.viewStatistical.tvTime.text = item.time
                }

            }).show(parentFragmentManager, null)
        }
    }

    private fun getDataStatistical(item: TimeStatistical) {
        mWorkspaceGroupData?.let { workSpaceData ->
            timeStatistical = item.value
            (activity as MainActivity).doGetStaticWorkspace(
                workSpaceData,
                timeStatistical
            )
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
                            if (group.isOwner) {
                                listGroupIsOwner.add(group)
                            } else {
                                listGroupIsMember.add(group)
                            }
                        }
                        groupIsOwnerAdapter?.notifyDataSetChanged()
                        groupIsMemberAdapter?.notifyDataSetChanged()
                    }
                    updateViewListGroup()
                    Log.e("doGetGroupWithId", "onResponse: " + listGroupIsOwner.size + " | " + listGroupIsMember.size)
                }

            }

            override fun onFailure(call: Call<GroupsDataResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun updateViewListGroup() {
        binding.tvGroupIsOwner.visibility = if (listGroupIsOwner.isEmpty()) View.GONE else View.VISIBLE
        binding.rcvGroupIsOwner.visibility = if (listGroupIsOwner.isEmpty()) View.GONE else View.VISIBLE
        binding.tvGroupIsMember.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
        binding.rcvGroupIsMember.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
        binding.viewLine.visibility = if (listGroupIsMember.isEmpty()) View.GONE else View.VISIBLE
    }

    fun deleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        showProgressDialog()
        val client = NetworkClient()
        val call = context?.let {
            client.client(context = it)
                .doDeleteWorkspace(DeleteWorkSpaceRequest(data.id))
        }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    bottomSheet?.deleteWorkSpace(data, position)
                    //default chọn giá trị đầu tiên của list workspace sau khi xóa thành công
                    positionTypeChoose = 0
                    if (listWorkspaceGroupData.size > 0) {
                        listWorkspaceGroupData[positionTypeChoose].let { workspaceGroupData ->
                            mWorkspaceGroupData = workspaceGroupData
                            doGetGroupWithId(workspaceGroupData)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun updateWorkSpace(data: WorkspaceGroupData, position: Int) {
        showProgressDialog()
        val updateWorkspaceRequest = WorkspaceGroupData(
            id = data.id,
            name = data.name,
            type = data.type,
            isActive = data.isActive,
            userOwner = data.userOwner,
            isOwner = data.isOwner,
            phishingEnabled = data.phishingEnabled,
            malwareEnabled = data.malwareEnabled,
            logEnabled = data.logEnabled,
            groupIds = data.groupIds,
            members = data.members,
            createdAt = data.createdAt
        )
        val client = NetworkClient()
        val call =
            context?.let { client.client(context = it).doUpdateWorkspace(updateWorkspaceRequest) }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    fun updateNameWorkSpace(data: WorkspaceGroupData, newName: String, position: Int) {
        showProgressDialog()
        val updateNameWorkspaceRequest = UpdateNameWorkspaceRequest(data.id, newName)
        val client = NetworkClient()
        val call = context?.let {
            client.client(context = it)
                .doUpdateNameWorkSpace(updateNameWorkspaceRequest)
        }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    bottomSheet?.updateNameWorkSpace(newName, position)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}