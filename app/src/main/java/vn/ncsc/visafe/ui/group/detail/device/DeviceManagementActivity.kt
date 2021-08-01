package vn.ncsc.visafe.ui.group.detail.device

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityDeviceManagementBinding
import vn.ncsc.visafe.model.GroupData
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
            binding.tvNumberMember.text = "${listDeviceManager.size} thiết bị"
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
        bottomSheet.setOnClickListener { inputText, action ->
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

    private fun showDialogDelete(data: DeviceGroup, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_device_content, data.deviceName, data.deviceOwner),
            VisafeDialogBottomSheet.TYPE_CONFIRM_CANCLE
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.CONFIRM -> {

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