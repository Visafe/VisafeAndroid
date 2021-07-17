package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentOverViewProtectBinding
import vn.ncsc.visafe.model.ApplicationMostData
import vn.ncsc.visafe.model.ContentMostData
import vn.ncsc.visafe.model.DeviceMostData
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.dialog.ImageDialog
import vn.ncsc.visafe.ui.protect.BlockAdsActivity
import vn.ncsc.visafe.ui.protect.BlockTrackingDetailActivity
import vn.ncsc.visafe.ui.protect.ProtectDeviceActivity
import vn.ncsc.visafe.ui.protect.ProtectWifiActivity
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

        binding.layoutHomeProtect.llHomeProtectDevice.setOnSingClickListener {
            val intent = Intent(requireContext(), ProtectDeviceActivity::class.java)
            intent.putExtra(
                ProtectDeviceActivity.PROTECT_DEVICE_KEY,
                binding.layoutHomeProtect.switchHomeProtectDevice.isChecked
            )
            startActivity(intent)
        }

        binding.layoutHomeProtect.llHomeProtectWifi.setOnSingClickListener {
            val intent = Intent(requireContext(), ProtectWifiActivity::class.java)
            intent.putExtra(
                ProtectWifiActivity.PROTECT_WIFI_KEY,
                binding.layoutHomeProtect.switchHomeProtectWifi.isChecked
            )
            startActivity(intent)
        }

        binding.layoutHomeProtect.llHomeBlockAds.setOnSingClickListener {
            val intent = Intent(requireContext(), BlockAdsActivity::class.java)
            intent.putExtra(
                BlockAdsActivity.BLOCK_ADS_KEY,
                binding.layoutHomeProtect.switchHomeBlockAds.isChecked
            )
            startActivity(intent)
        }

        binding.layoutHomeProtect.llHomeBlockTracking.setOnSingClickListener {
            val intent = Intent(requireContext(), BlockTrackingDetailActivity::class.java)
            intent.putExtra(
                BlockTrackingDetailActivity.BLOCK_TRACKING_KEY,
                binding.layoutHomeProtect.switchHomeBlockTracking.isChecked
            )
            startActivity(intent)
        }

        setupWebviewUtilities()
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

    fun doGetStaticWorkspace(workspaceGroupData: WorkspaceGroupData) {
        workspaceGroupData.id.let {
            if (!(activity as BaseActivity).isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = context?.let { it1 -> client.client(context = it1).doGetStatisticalOneWorkspace(workspaceGroupData.id, "24") }
            call?.enqueue(BaseCallback(this, object : Callback<StatsWorkspaceResponse> {
                override fun onResponse(
                    call: Call<StatsWorkspaceResponse>,
                    response: Response<StatsWorkspaceResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebviewUtilities() {
        val setting = binding.wvHomeUtilities.settings
        setting.useWideViewPort = true
        setting.loadWithOverviewMode = true
        setting.javaScriptEnabled = true
        setting.allowContentAccess = true
        setting.setSupportZoom(false)
        setting.builtInZoomControls = true
        setting.displayZoomControls = false
        binding.wvHomeUtilities.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        binding.wvHomeUtilities.loadUrl("https://tienich.khonggianmang.vn/")
    }
}