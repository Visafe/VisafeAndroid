package com.vn.visafe_android.ui.group.detail

import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDetailDeviceManagementBinding
import com.vn.visafe_android.model.DeviceData
import com.vn.visafe_android.ui.adapter.DeviceAdapter
import com.vn.visafe_android.ui.adapter.OnClickDevice
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.dialog.VisafeDialogBottomSheet

class GroupDetailDeviceManagementFragment :
    BaseFragment<FragmentGroupDetailDeviceManagementBinding>() {
    private lateinit var deviceAdapter: DeviceAdapter

    override fun layoutRes(): Int = R.layout.fragment_group_detail_device_management

    override fun initView() {

        deviceAdapter = DeviceAdapter(createDeviceList())
        binding.rvDevice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDevice.adapter = deviceAdapter
        deviceAdapter.setOnClickListener(object : OnClickDevice {
            override fun onClickDevice(data: DeviceData, position: Int) {

            }

            override fun onMoreDevice(data: DeviceData, position: Int) {
                showDialogEdit(data, position)
            }

        })

        binding.tvAmoutDevice.text =
            getString(R.string.amout_device, createDeviceList().size.toString())
    }

    private fun createDeviceList(): ArrayList<DeviceData> {
        val list: ArrayList<DeviceData> = ArrayList()
        list.add(DeviceData("Iphone XS - Nguyễn Văn A", "Trần Thành Long"))
        list.add(DeviceData("Ipad", "Trần Thành Long"))
        list.add(DeviceData("Iphone XS", "Trần Thành Long"))
        list.add(DeviceData("Samsung", "Trần Thành Long"))
        list.add(DeviceData("Iphone XS - Nguyễn Văn A", "Trần Thành Long"))
        return list
    }

    private fun showDialogEdit(data: DeviceData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            data.nameDevice!!,
            data.nameUser!!,
            VisafeDialogBottomSheet.TYPE_EDIT,
            getString(R.string.edit_device),
            getString(R.string.delete_device)
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.DELETE -> {
                    showDialogDelete(data, position)
                }

                Action.EDIT -> {
                    showDialogEditName(data, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogDelete(data: DeviceData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_device_content, data.nameDevice, data.nameUser),
            VisafeDialogBottomSheet.TYPE_CONFIRM
        )
        bottomSheet.show(parentFragmentManager, null)
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

    private fun showDialogEditName(data: DeviceData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstanceEdit(
            data.nameDevice!!,
            data.nameUser!!,
            VisafeDialogBottomSheet.TYPE_SAVE,
            getString(R.string.input_name_device),
            ""
        )
        bottomSheet.show(parentFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.SAVE -> {
                    deviceAdapter.deleteItem(data, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }
}