package vn.ncsc.visafe.ui.group.detail.member

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityMemberManagementBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UsersGroupInfo
import vn.ncsc.visafe.model.request.UserInGroupRequest
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.utils.setOnSingClickListener

class MemberManagementActivity : BaseActivity(), MemberManagerAdapter.OnSelectItemListener {

    lateinit var binding: ActivityMemberManagementBinding
    private var groupData: GroupData? = null
    private var listUsersGroupInfo: MutableList<UsersGroupInfo> = mutableListOf()
    private var groupName: String? = ""
    private var memberManagerAdapter: MemberManagerAdapter? = null

    companion object {
        const val KEY_GROUP_NAME = "KEY_GROUP_NAME"
        const val KEY_DATA = "KEY_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(KEY_DATA)
            groupName = it.getStringExtra(KEY_GROUP_NAME)
        }
        initView()
        initControl()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        groupData?.let {
            listUsersGroupInfo.clear()
            it.listUsersGroupInfo?.toMutableList()?.let { it1 -> listUsersGroupInfo.addAll(it1) }
            binding.tvNumberMember.text = "${listUsersGroupInfo.size} thành viên"
            binding.tvContent.text = it.name
        }
        memberManagerAdapter = MemberManagerAdapter(
            groupData?.fkUserId, groupData?.userManage, groupData?.usersActive, this
        )
        memberManagerAdapter?.setData(listUsersGroupInfo)
        binding.rcvMember.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvMember.adapter = memberManagerAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra(KEY_DATA, groupData)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun initControl() {
        binding.ivBack.setOnSingClickListener {
            val intent = Intent()
            intent.putExtra(KEY_DATA, groupData)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onSelectItem(item: UsersGroupInfo, position: Int) {
    }

    override fun onMore(item: UsersGroupInfo, position: Int) {
        val fullName = item.fullName
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "Thành viên",
            fullName,
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
            "Cấp quyền làm Quản trị viên",
            "Xóa thành viên khỏi nhóm"
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.EDIT -> {
                    upgradeUserToManager(item, groupData?.groupid)
                }
                Action.DELETE -> {
                    showDialogDeleteGroup(fullName, item, groupData?.groupid)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogDeleteGroup(fullName: String?, item: UsersGroupInfo, groupId: String?) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_group_content, "thành viên ${fullName} khỏi nhóm?"),
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCLE
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.CONFIRM -> {
                    deleteUser(fullName, item, groupId)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun deleteUser(fullName: String?, item: UsersGroupInfo, groupId: String?) {
        val removeUserRequest = UserInGroupRequest(userId = item.userID?.toInt(), groupId = groupId)
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doRemoveUserFromGroup(removeUserRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Xóa thành viên $fullName thành công")
                    memberManagerAdapter?.deleteItem(item)
                    memberManagerAdapter?.notifyDataSetChanged()
                    binding.tvNumberMember.text = "${listUsersGroupInfo.size} thành viên"
                    groupData?.listUsersGroupInfo?.clear()
                    groupData?.listUsersGroupInfo?.addAll(listUsersGroupInfo)
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun upgradeUserToManager(item: UsersGroupInfo, groupId: String?) {
        val upgradeUserToManagerRequest = UserInGroupRequest(userId = item.userID?.toInt(), groupId = groupId)
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doUpgradeUserToManager(upgradeUserToManagerRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Cập nhật thành công")
                    groupData?.userManage?.add(item.userID.toString())
                    groupData?.usersActive?.let { lstActive ->
                        for (i in lstActive) {
                            if (item.userID.toString() == i) {
                                groupData?.usersActive?.remove(i)
                            }
                        }
                    }
                    memberManagerAdapter?.notifyDataSetChanged()
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}