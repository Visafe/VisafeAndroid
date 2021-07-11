package com.vn.visafe_android.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentOverViewProtectBinding
import com.vn.visafe_android.model.ApplicationMostData
import com.vn.visafe_android.model.ContentMostData
import com.vn.visafe_android.model.DeviceMostData
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.adapter.ApplicationMostAdapter
import com.vn.visafe_android.ui.adapter.ContentMostAdapter
import com.vn.visafe_android.ui.adapter.DeviceMostAdapter
import com.vn.visafe_android.ui.adapter.GroupListAdapter
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.dialog.ImageDialog
import com.vn.visafe_android.ui.group.dashboard.GroupDashboardActivity
import com.vn.visafe_android.utils.setOnSingClickListener

class OverViewProtectFragment : BaseFragment<FragmentOverViewProtectBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = OverViewProtectFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_over_view_protect

    override fun initView() {
//        val contentAdapter = ContentMostAdapter(createContentList())
//        binding.rvContentMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.rvContentMost.adapter = contentAdapter
//
//        val deviceAdapter = DeviceMostAdapter(createDeviceList())
//        binding.rvDeviceMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.rvDeviceMost.adapter = deviceAdapter
//
//        val appAdapter = ApplicationMostAdapter(createAppList())
//        binding.rvAppMost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.rvAppMost.adapter = appAdapter
//
//        val groupAdapter = GroupListAdapter(createGroupList())
//        groupAdapter.onClickGroup = object : GroupListAdapter.OnClickGroup {
//            override fun openGroup(data: GroupData) {
//                val intent = Intent(requireContext(), GroupDashboardActivity::class.java)
//                intent.putExtra(GroupDashboardActivity.GROUP_DATA_KEY, data)
//                startActivity(intent)
//            }
//
//            override fun onClickMore() {
//
//            }
//
//        }
//        binding.rvGroup.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.rvGroup.adapter = groupAdapter
        binding.layoutAddVpn.btnHomeVpnAdd.setOnSingClickListener {
            showAddVpn()
        }
        binding.layoutHomeProtectFamily.btnHomeFamilyAddGroup.setOnSingClickListener {
            startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
        }
        binding.layoutHomeProtect.switchHomeProtectDevice.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutHomeProtect.ivHomeProtectDevice.setImageResource(
                if (isChecked) {
                    R.drawable.ic_mobile
                } else {
                    R.drawable.ic_info_circle
                }
            )
        }
    }

    private fun createContentList() : List<ContentMostData> {
        val list : ArrayList<ContentMostData> = ArrayList()
        list.add(ContentMostData("Mạng xã hội", 80, "1h"))
        list.add(ContentMostData("Chơi game", 60, "32p"))
        list.add(ContentMostData("Trình duyệt web", 20, "32s"))
        return list
    }

    private fun createAppList() : List<ApplicationMostData> {
        val list : ArrayList<ApplicationMostData> = ArrayList()
        list.add(ApplicationMostData("Instagram", 80, "1h"))
        list.add(ApplicationMostData("Google Map", 60, "32p"))
        list.add(ApplicationMostData("Book", 20, "32s"))
        list.add(ApplicationMostData("AppStore", 20, "32s"))
        return list
    }

    private fun createDeviceList() : List<DeviceMostData> {
        val list : ArrayList<DeviceMostData> = ArrayList()
        list.add(DeviceMostData("Iphone XS - Nguyễn Văn A", 80, "1h"))
        list.add(DeviceMostData("Ipad - Nguyễn Văn B", 60, "32p"))
        list.add(DeviceMostData("Iphone", 20, "32s"))
        list.add(DeviceMostData("Samsung", 20, "32s"))
        return list
    }

    private fun createGroupList() : List<GroupData> {
        val list : ArrayList<GroupData> = ArrayList()
        list.add(GroupData("Phòng 1: Marketing", 80, 15, null))
        list.add(GroupData("Phòng 2: CNTT", 60, 14, null))
        list.add(GroupData("Phòng 3: Kinh tế - Đối ngoại", 20, 13, null))
        list.add(GroupData("Phòng 4: Chuyên gia", 20, 13, null))
        return list
    }

    private fun showAddVpn() {
        val dialog = ImageDialog.newsIntance(ImageDialog.TYPE_ADD_VPN)
        dialog.setOnClickListener {
            Toast.makeText(requireContext(), "Add VPN", Toast.LENGTH_SHORT).show()
        }
        dialog.show(parentFragmentManager, null)
    }

    //Hiển thị dialog bật thông báo
    private fun showDialogTurnOnNoti() {
        val dialog = ImageDialog.newsIntance(ImageDialog.TYPE_TURN_ON_NOTI)
        dialog.setOnClickListener {
            Toast.makeText(requireContext(), "Add VPN", Toast.LENGTH_SHORT).show()
        }
        dialog.show(parentFragmentManager, null)
    }
}