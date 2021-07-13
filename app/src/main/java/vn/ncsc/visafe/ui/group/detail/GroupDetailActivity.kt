package vn.ncsc.visafe.ui.group.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityGroupDetailBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.request.DeleteGroupRequest
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.utils.getTextGroup
import vn.ncsc.visafe.utils.setOnSingClickListener

class GroupDetailActivity : BaseActivity() {
    lateinit var binding: ActivityGroupDetailBinding

    companion object {
        const val DATA_ID = "DATA_ID"
        const val DATA_POSITION = "DATA_POSITION"
    }

    private var groupId: String? = null
    private var positionGroup: Int? = null
    private var groupNumber: String? = null
    private var mIdGroup: String? = null
    private var fkUserId: Int? = null
    private var groupData: GroupData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupId = it.getStringExtra(DATA_ID)
            positionGroup = it.getIntExtra(DATA_POSITION, 0)
        }
        initView()
        initControl()
    }

    private fun initControl() {
        binding.ivMore.setOnSingClickListener {
            val title: String = groupNumber.toString()
            val bottomSheet = VisafeDialogBottomSheet.newInstance(
                "",
                title,
                VisafeDialogBottomSheet.TYPE_EDIT,
                getString(R.string.edit_group),
                getString(R.string.delete_group)
            )
            bottomSheet.show(supportFragmentManager, null)
            bottomSheet.setOnClickListener { _, action ->
                when (action) {
                    Action.DELETE -> {
                        showDialogDeleteGroup()
                    }
                    Action.EDIT -> {
//                    showDialogUpdateNameWorkSpace(data, position)
                    }
                    else -> {
                        return@setOnClickListener
                    }
                }
            }
        }
        binding.ivBack.setOnClickListener { finish() }
        binding.btnAddNewMember.setOnSingClickListener {
//            val intent = Intent(this@GroupDetailActivity, MemberManagementActivity::class.java)
//            intent.putExtra(MemberManagementActivity.KEY_DATA, groupData)
//            startActivity(intent)
        }
    }

    private fun initView() {
        groupId?.let { doGetAGroupWithId(it) }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun showDialogDeleteGroup() {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_group_content, groupNumber),
            VisafeDialogBottomSheet.TYPE_CONFIRM
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.CONFIRM -> {
                    doDeleteGroup()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun doDeleteGroup() {
        showProgressDialog()
        val deleteGroupRequest = DeleteGroupRequest(groupId = mIdGroup, fkUserId = fkUserId)
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doDeleteGroup(deleteGroupRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        val dialog =
                            SuccessDialogFragment.newInstance(
                                "Xóa nhóm thành công", "Nhóm mà bạn chọn đã được xóa", "Đồng ý"
                            )
                        dialog.show(supportFragmentManager, "")
                        dialog.setOnClickListener {
                            when (it) {
                                Action.CONFIRM -> {
                                    setResult(RESULT_OK)
                                    finish()
                                }
                                else -> {
                                    return@setOnClickListener
                                }
                            }
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
                        groupNumber = "Phòng ${positionGroup?.plus(1)}"
                        binding.tvName.text = "${groupNumber}: ${it.name}"
                        binding.ivGroup.text = getTextGroup(groupNumber)
                        binding.tvNumberMember.text = "${it.listUsersGroupInfo?.size} thành viên"
                        mIdGroup = it.groupid
                        fkUserId = it.fkUserId
                        groupData = it
                    }
                }

            }

            override fun onFailure(call: Call<GroupData>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}