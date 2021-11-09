package vn.ncsc.visafe.ui.group.detail.member

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
import vn.ncsc.visafe.ui.group.join.AddMemberInGroupActivity
import vn.ncsc.visafe.utils.setOnSingClickListener

class MemberManagementActivity : BaseActivity(), MemberManagerAdapter.OnSelectItemListener {

    lateinit var binding: ActivityMemberManagementBinding
    private var groupData: GroupData? = null
    private var listUsersGroupInfo: MutableList<UsersGroupInfo> = mutableListOf()
    private var listUsersActive: MutableList<String> = mutableListOf()
    private var listUserManage: MutableList<String> = mutableListOf()
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
            binding.tvContent.isSelected = true
            it.usersActive?.let { it1 -> listUsersActive.addAll(it1) }
            it.userManage?.let { it1 -> listUserManage.addAll(it1) }
        }
        memberManagerAdapter = MemberManagerAdapter(
            groupData?.fkUserId, listUserManage, listUsersActive, this
        )
        memberManagerAdapter?.setData(listUsersGroupInfo)
        binding.rcvMember.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvMember.adapter = memberManagerAdapter

        binding.edtInputSearchMember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                memberManagerAdapter?.filter?.filter(s)
            }

        })
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
        binding.btnAddMember.setOnSingClickListener {
            val intent = Intent(this@MemberManagementActivity, AddMemberInGroupActivity::class.java)
            intent.putExtra(KEY_DATA, groupData)
            resultLauncherAddMember.launch(intent)
        }
    }

    private var resultLauncherAddMember =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    groupData?.groupid?.let { doGetAGroupWithId(it) }
                }
            }
        }


    private fun doGetAGroupWithId(id: String) {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doGetAGroupWithId(id)
        call.enqueue(BaseCallback(this, object : Callback<GroupData> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<GroupData>,
                response: Response<GroupData>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let {
                        groupName = it.name
                        groupData = it
                        it.listUsersGroupInfo?.let { it1 ->
                            listUsersGroupInfo.clear()
                            listUsersGroupInfo.addAll(it1)
                            binding.tvNumberMember.text = "${listUsersGroupInfo.size} thành viên"
                        }
                        listUserManage.clear()
                        listUsersActive.clear()
                        it.usersActive?.let { it1 -> listUsersActive.addAll(it1) }
                        it.userManage?.let { it1 -> listUserManage.addAll(it1) }
                        memberManagerAdapter?.notifyDataSetChanged()
                    }
                }

            }

            override fun onFailure(call: Call<GroupData>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    override fun onSelectItem(item: UsersGroupInfo, position: Int) {
    }

    override fun onMore(item: UsersGroupInfo, position: Int) {
        if (item.typePosition == TypePosition.IS_OWNER)
            return
        val fullName = item.fullName
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "Thành viên",
            fullName,
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
            if (item.typePosition == TypePosition.ADMINISTRATORS) "Đặt làm Giám sát viên" else "Cấp quyền làm Quản trị viên",
            "Xóa $fullName khỏi nhóm"
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.EDIT -> {
                    if (item.typePosition == TypePosition.ADMINISTRATORS) {
                        upgradeUserToViewer(item, groupData?.groupid)
                    } else {
                        upgradeUserToManager(item, groupData?.groupid)
                    }
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
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCEL
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

    //xóa thành viên
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
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Xóa thành viên $fullName thành công")
                    groupData?.groupid?.let { doGetAGroupWithId(it) }
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    //đặt làm quản trị viên
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
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Cập nhật thành công")
                    groupData?.groupid?.let { doGetAGroupWithId(it) }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    //dat lam giam sat vien
    private fun upgradeUserToViewer(item: UsersGroupInfo, groupId: String?) {
        val upgradeUserToManagerRequest = UserInGroupRequest(userId = item.userID?.toInt(), groupId = groupId)
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doUpgradeUserToViewer(upgradeUserToManagerRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Cập nhật thành công")
                    groupData?.groupid?.let { doGetAGroupWithId(it) }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}