package vn.ncsc.visafe.ui.group.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
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
import vn.ncsc.visafe.model.StatsWorkSpace
import vn.ncsc.visafe.model.request.DeleteGroupRequest
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.ui.group.detail.device.DeviceManagementActivity
import vn.ncsc.visafe.ui.group.detail.member.AddMemberActivity
import vn.ncsc.visafe.ui.group.detail.member.MemberManagementActivity
import vn.ncsc.visafe.ui.group.detail.setup_protect.ProtectDeviceGroupDetailActivity
import vn.ncsc.visafe.utils.getTextGroup
import vn.ncsc.visafe.utils.setOnSingClickListener

class GroupDetailActivity : BaseActivity(), SetupProtectGroupDetailAdapter.OnClickSetupGroup {
    lateinit var binding: ActivityGroupDetailBinding

    companion object {
        const val DATA_ID = "DATA_ID"
        const val DATA_POSITION = "DATA_POSITION"
    }

    private var groupId: String? = null
    private var positionGroup: Int? = null
    private var groupName: String? = null
    private var mIdGroup: String? = null
    private var fkUserId: Int? = null
    private var groupData: GroupData? = null
    private var statsWorkSpace: StatsWorkSpace? = null

    private var adapter: SetupProtectGroupDetailAdapter? = null

    @SuppressLint("SetTextI18n")
    private var resultLauncherMemberManager =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    groupData = result.data?.getParcelableExtra(MemberManagementActivity.KEY_DATA)
                    binding.tvNumberMember.text = "${groupData?.listUsersGroupInfo?.size} thành viên"
                }
            }
        }

    private var resultLauncherDeviceManager =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    groupData = result.data?.getParcelableExtra(MemberManagementActivity.KEY_DATA)
                    binding.tvNumberDevice.text = "${groupData?.listDevicesGroupInfo?.size} thiết bị"
                }
            }
        }

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
        binding.ivBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.ivMore.setOnSingClickListener {
            val title: String = groupName.toString()
            val bottomSheet = VisafeDialogBottomSheet.newInstance(
                "",
                title,
                VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
                getString(R.string.edit_info_group),
                getString(R.string.delete_group)
            )
            bottomSheet.show(supportFragmentManager, null)
            bottomSheet.setOnClickListener { _, action ->
                when (action) {
                    Action.EDIT -> {
//                    showDialogUpdateNameWorkSpace(data, position)
                    }
                    Action.DELETE -> {
                        showDialogDeleteGroup()
                    }
                    else -> {
                        return@setOnClickListener
                    }
                }
            }
        }
        binding.btnAddNewMember.setOnSingClickListener {
            val intent = Intent(this@GroupDetailActivity, AddMemberActivity::class.java)
//            intent.putExtra(MemberManagementActivity.KEY_DATA, groupData)
//            intent.putExtra(MemberManagementActivity.KEY_GROUP_NUMBER, groupNumber)
            startActivity(intent)

        }
        binding.ctrlMemberManager.setOnSingClickListener {
            val intent = Intent(this@GroupDetailActivity, MemberManagementActivity::class.java)
            intent.putExtra(MemberManagementActivity.KEY_DATA, groupData)
            intent.putExtra(MemberManagementActivity.KEY_GROUP_NAME, groupName)
            resultLauncherMemberManager.launch(intent)
        }

        binding.ctrlDeviceManager.setOnSingClickListener {
            val intent = Intent(this@GroupDetailActivity, DeviceManagementActivity::class.java)
            intent.putExtra(DeviceManagementActivity.KEY_DATA, groupData)
            resultLauncherDeviceManager.launch(intent)
        }

        binding.btnStatistical.setOnClickListener {
            binding.btnStatistical.alpha = 1f
            binding.viewLineStatistical.visibility = View.VISIBLE
            binding.viewStatistical.ctrlStatistical.visibility = View.VISIBLE

            binding.btnSetupProtected.alpha = 0.4f
            binding.viewLineSetupProtected.visibility = View.INVISIBLE
            binding.rcvSetupProtected.visibility = View.GONE
        }

        binding.btnSetupProtected.setOnClickListener {
            binding.btnSetupProtected.alpha = 1f
            binding.viewLineSetupProtected.visibility = View.VISIBLE
            binding.rcvSetupProtected.visibility = View.VISIBLE

            binding.btnStatistical.alpha = 0.4f
            binding.viewLineStatistical.visibility = View.INVISIBLE
            binding.viewStatistical.ctrlStatistical.visibility = View.GONE
            binding.viewStatistical.tvOverview.visibility = View.GONE
            binding.viewStatistical.tvTime.visibility = View.GONE
        }
    }

    private fun initView() {
        binding.viewStatistical.tvOverview.visibility = View.GONE
        binding.viewStatistical.tvTime.visibility = View.GONE
        groupId?.let { doGetAGroupWithId(it) }
        adapter = SetupProtectGroupDetailAdapter(applicationContext, this)
        binding.rcvSetupProtected.adapter = adapter
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

    private fun showDialogDeleteGroup() {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_group_content, groupName),
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCLE
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
                        groupName = it.name
                        binding.tvName.text = groupName
                        binding.ivGroup.text = getTextGroup(groupName)
                        binding.tvNumberMember.text = "${it.listUsersGroupInfo?.size} thành viên"
                        binding.tvNumberDevice.text = "${it.listDevicesGroupInfo?.size} thiết bị"
                        mIdGroup = it.groupid
                        fkUserId = it.fkUserId
                        groupData = it
                        doGetStaticAGroup(groupData, TimeStatistical.HANG_NGAY.value)
                        updateSetupProtect(it)
                    }
                }

            }

            override fun onFailure(call: Call<GroupData>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun updateSetupProtect(groupData: GroupData) {
        SetupProtectDetailGroup.CHAN_QUANG_CAO.isEnable = groupData.adblock_enabled ||
                groupData.game_ads_enabled ||
                groupData.app_ads?.isNotEmpty() == true
        SetupProtectDetailGroup.CHAN_THEO_DOI.isEnable = groupData.native_tracking?.isNotEmpty() == true
        SetupProtectDetailGroup.CHAN_TRUY_CAP.isEnable = groupData.blocked_services?.isNotEmpty() == true ||
                groupData.block_webs?.isNotEmpty() == true
        SetupProtectDetailGroup.CHAN_NOI_DUNG.isEnable = groupData.porn_enabled || groupData.safesearch_enabled
                || groupData.youtuberestrict_enabled || groupData.gambling_enabled
        SetupProtectDetailGroup.CHAN_VPN_PROXY.isEnable = groupData.bypass_enabled
        adapter?.notifyDataSetChanged()
    }

    fun doGetStaticAGroup(groupData: GroupData?, timeLimit: String) {
        groupData?.groupid.let {
            if (!isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetStatisticalOneGroup(it, timeLimit)
            call.enqueue(BaseCallback(this, object : Callback<StatsWorkspaceResponse> {
                override fun onResponse(
                    call: Call<StatsWorkspaceResponse>,
                    response: Response<StatsWorkspaceResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let { data ->
                            binding.viewStatistical.tvValueDangerous.text = data.num_dangerous_domain.toString()
                            binding.viewStatistical.tvValueAds.text = data.num_ads_blocked.toString()
                            binding.viewStatistical.tvValueViolate.text = data.num_violation.toString()
                            statsWorkSpace = StatsWorkSpace(data)
                        }

                    }
                    dismissProgress()
                }

                override fun onFailure(call: Call<StatsWorkspaceResponse>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                    dismissProgress()
                }
            }))
        }

    }

    @SuppressLint("SetTextI18n")
    private var resultLauncherProtectDevice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
                    groupData = result.data?.getParcelableExtra(MemberManagementActivity.KEY_DATA)
                    binding.tvNumberMember.text = "${groupData?.listUsersGroupInfo?.size} thành viên"
                }
            }
        }

    override fun onClickSetupGroup(data: SetupProtectDetailGroup) {
        adapter?.let {
            for (group in it.dataList) {
                if (group == data) {
                    when (group) {
                        SetupProtectDetailGroup.BAO_VE_THIET_BI -> {
                            val intent = Intent(this@GroupDetailActivity, ProtectDeviceGroupDetailActivity::class.java)
                            intent.putExtra(ProtectDeviceGroupDetailActivity.DATA_GROUP_KEY, groupData)
                            intent.putExtra(ProtectDeviceGroupDetailActivity.NUM_DANGEROUS_DOMAIN, statsWorkSpace?.num_dangerous_domain)
                            intent.putExtra(ProtectDeviceGroupDetailActivity.NUM_DANGEROUS_DOMAIN_ALL, statsWorkSpace?.num_dangerous_domain_all)
                            resultLauncherProtectDevice.launch(intent)
                        }
                        SetupProtectDetailGroup.CHAN_QUANG_CAO -> {
                            showToast("CHAN_QUANG_CAO")
                        }
                        SetupProtectDetailGroup.CHAN_THEO_DOI -> {
                            showToast("CHAN_THEO_DOI")
                        }
                        SetupProtectDetailGroup.CHAN_TRUY_CAP -> {
                            showToast("CHAN_TRUY_CAP")
                        }
                        SetupProtectDetailGroup.CHAN_NOI_DUNG -> {
                            showToast("CHAN_NOI_DUNG")
                        }
                    }
                }
            }
        }
    }

    override fun onSwitchItemChange(data: SetupProtectDetailGroup, isChecked: Boolean) {
        Log.e("onSwitchItemChange: ", Gson().toJson(data))
        adapter?.let {
            for (group in it.dataList) {
                if (group == data) {
                    when (group) {
                        SetupProtectDetailGroup.CHAN_QUANG_CAO -> {
                            groupData?.adblock_enabled = isChecked
                            groupData?.game_ads_enabled = isChecked
                            groupData?.app_ads =
                                if (isChecked) listOf("instagram", "youtube", "spotify", "facebook") else listOf()
                        }
                        SetupProtectDetailGroup.CHAN_THEO_DOI -> {
                            groupData?.native_tracking =
                                if (isChecked) listOf(
                                    "alexa",
                                    "apple",
                                    "huawei",
                                    "roku",
                                    "samsung",
                                    "sonos",
                                    "windows",
                                    "xiaomi"
                                ) else listOf()
                        }
                        SetupProtectDetailGroup.CHAN_TRUY_CAP -> {
                            groupData?.blocked_services =
                                if (isChecked) listOf(
                                    "facebook",
                                    "zalo",
                                    "tiktok",
                                    "instagram",
                                    "tinder",
                                    "twitter",
                                    "netflix",
                                    "reddit",
                                    "9gag",
                                    "discord"
                                ) else listOf()
                            groupData?.block_webs =
                                if (isChecked) listOf(
                                    "https://www.youtube.com/",
                                    "https://www.facebook.com/",
                                    "https://gmail.com/",
                                    "https://www.youtube.com/"
                                ) else listOf()
                        }
                        SetupProtectDetailGroup.CHAN_NOI_DUNG -> {
                            groupData?.porn_enabled = isChecked
                            groupData?.safesearch_enabled = isChecked
                            groupData?.youtuberestrict_enabled = isChecked
                            groupData?.gambling_enabled = isChecked

                        }
                        SetupProtectDetailGroup.CHAN_VPN_PROXY -> {
                            groupData?.bypass_enabled = isChecked
                        }

                    }
                }
            }
        }
        doUpdateGroup(groupData)
    }

    private fun doUpdateGroup(groupData: GroupData?) {
        groupData?.let {
            if (!isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doUpdateGroup(it)
            call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {

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
}