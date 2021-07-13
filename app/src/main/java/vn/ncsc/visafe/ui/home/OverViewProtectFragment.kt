package vn.ncsc.visafe.ui.home

import android.content.Intent
import android.widget.Toast
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentOverViewProtectBinding
import vn.ncsc.visafe.model.ApplicationMostData
import vn.ncsc.visafe.model.ContentMostData
import vn.ncsc.visafe.model.DeviceMostData
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.dialog.ImageDialog
import vn.ncsc.visafe.utils.ChartUtil
import vn.ncsc.visafe.utils.setOnSingClickListener

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
        val dataChart = LinkedHashMap<String, Int>()
        dataChart["1"] = 10
        dataChart["2"] = 20
        dataChart["3"] = 30
        dataChart["4"] = 40
        dataChart["5"] = 50
        dataChart["6"] = 60
        dataChart["7"] = 70
        dataChart["8"] = 75
        dataChart["9"] = 65
        dataChart["10"] = 70
        dataChart["11"] = 20
        ChartUtil.initBarChart(binding.layoutHomeChart.hiChartView, dataChart, ChartUtil.getArrayColor(dataChart.size))
    }

    private fun createContentList(): List<ContentMostData> {
        val list: ArrayList<ContentMostData> = ArrayList()
        list.add(ContentMostData("Mạng xã hội", 80, "1h"))
        list.add(ContentMostData("Chơi game", 60, "32p"))
        list.add(ContentMostData("Trình duyệt web", 20, "32s"))
        return list
    }

    private fun createAppList(): List<ApplicationMostData> {
        val list: ArrayList<ApplicationMostData> = ArrayList()
        list.add(ApplicationMostData("Instagram", 80, "1h"))
        list.add(ApplicationMostData("Google Map", 60, "32p"))
        list.add(ApplicationMostData("Book", 20, "32s"))
        list.add(ApplicationMostData("AppStore", 20, "32s"))
        return list
    }

    private fun createDeviceList(): List<DeviceMostData> {
        val list: ArrayList<DeviceMostData> = ArrayList()
        list.add(DeviceMostData("Iphone XS - Nguyễn Văn A", 80, "1h"))
        list.add(DeviceMostData("Ipad - Nguyễn Văn B", 60, "32p"))
        list.add(DeviceMostData("Iphone", 20, "32s"))
        list.add(DeviceMostData("Samsung", 20, "32s"))
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