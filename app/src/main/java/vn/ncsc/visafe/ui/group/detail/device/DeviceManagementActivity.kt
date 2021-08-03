package vn.ncsc.visafe.ui.group.detail.device

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import vn.ncsc.visafe.databinding.ActivityDeviceManagementBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.request.RemoveDeviceRequest
import vn.ncsc.visafe.model.response.DeviceGroup
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.ui.group.detail.member.MemberManagementActivity
import vn.ncsc.visafe.utils.setOnSingClickListener

class DeviceManagementActivity : BaseActivity(), DeviceManagerAdapter.OnClickDevice {
    lateinit var binding: ActivityDeviceManagementBinding
    private var groupData: GroupData? = null
    private var listDeviceManager: MutableList<DeviceGroup> = mutableListOf()
    private var deviceManagerAdapter: DeviceManagerAdapter? = null

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(MemberManagementActivity.KEY_DATA)
        }
        initView()
        initControl()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        groupData?.let {
            listDeviceManager.clear()
            it.listDevicesGroupInfo?.toMutableList()?.let { it1 -> listDeviceManager.addAll(it1) }
            binding.tvNumberDevice.text = "${listDeviceManager.size} thiết bị"
            binding.tvContent.text = it.name
        }
        deviceManagerAdapter = DeviceManagerAdapter(this)
        deviceManagerAdapter?.setData(listDeviceManager)
        binding.rcvMember.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.rcvMember.adapter = deviceManagerAdapter
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
        binding.btnAddDevice.setOnSingClickListener {
            val intent = Intent(this@DeviceManagementActivity, AddDeviceActivity::class.java)
            intent.putExtra(AddDeviceActivity.KEY_DATA, groupData)
            resultLauncherAddDevice.launch(intent)
        }
    }

    private var resultLauncherAddDevice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                if (result.data != null) {
//                    val newMember = result.data?.getParcelableExtra<UsersGroupInfo>(JoinGroupActivity.NEW_MEMBER)
//                    newMember?.let {
//                        groupData?.listUsersGroupInfo?.add(it)
//                        binding.tvNumberMember.text = "${groupData?.listUsersGroupInfo?.size} thành viên"
//                    }
                }
            }
        }

    override fun onClickDevice(data: DeviceGroup, position: Int) {
    }

    override fun onMoreDevice(data: DeviceGroup, position: Int) {
        showDialogEdit(data, position)
    }

    private fun showDialogEdit(data: DeviceGroup, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            data.deviceName!!,
            data.deviceOwner!!,
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
            getString(R.string.edit_device),
            getString(R.string.delete_device)
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.DELETE -> {
                    showDialogDelete(data, position)
                }

                Action.EDIT -> {
                    showDialogEditName(data)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun removeDeviceFromGroup(data: DeviceGroup, position: Int) {
        val removeDeviceRequest =
            RemoveDeviceRequest(deviceId = data.deviceID, groupId = data.groupID, deviceMonitorID = data.deviceMonitorID)
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doRemoveDeviceFromGroup(removeDeviceRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Xóa thiết bị ${data.deviceName} thành công")
                    deviceManagerAdapter?.deleteItem(data)
                    deviceManagerAdapter?.notifyDataSetChanged()
                    binding.tvNumberDevice.text = "${listDeviceManager.size} thiết bị"
                    groupData?.listDevicesGroupInfo?.clear()
                    groupData?.listDevicesGroupInfo?.addAll(listDeviceManager)
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun showDialogDelete(data: DeviceGroup, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_device_content, data.deviceName, data.deviceOwner),
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCLE
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.CONFIRM -> {
                    removeDeviceFromGroup(data, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogEditName(data: DeviceGroup) {
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            data.deviceName!!,
            data.deviceOwner!!,
            VisafeDialogBottomSheet.TYPE_INPUT_SAVE,
            getString(R.string.input_name_device),
            ""
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.SAVE -> {
                    deviceManagerAdapter?.deleteItem(data)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }
}