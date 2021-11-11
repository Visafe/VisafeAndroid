package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.FragmentOverViewProtectBinding
import vn.ncsc.visafe.model.*
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.WebViewActivity
import vn.ncsc.visafe.ui.adapter.OtherUtilitiesAdapter
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.authentication.RegisterActivity
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.custom.SpaceItemDecoration
import vn.ncsc.visafe.ui.dialog.DisplayStatisticalForTimeBottomSheet
import vn.ncsc.visafe.ui.dialog.ImageDialog
import vn.ncsc.visafe.ui.dialog.OnClickItemTime
import vn.ncsc.visafe.ui.group.detail.member.MemberManagementActivity
import vn.ncsc.visafe.ui.group.detail.setup_protect.BlockAdsGroupDetailActivity
import vn.ncsc.visafe.ui.group.detail.setup_protect.BlockTrackingGroupDetailActivity
import vn.ncsc.visafe.ui.pin.UpdatePinActivity
import vn.ncsc.visafe.ui.protect.*
import vn.ncsc.visafe.ui.upgrade.UpgradeActivity
import vn.ncsc.visafe.ui.website.WebsiteReportActivity
import vn.ncsc.visafe.utils.*

class OverViewProtectFragment : BaseFragment<FragmentOverViewProtectBinding>(), (OtherUtilitiesModel, Int) -> Unit {

    private var mWorkspaceGroupData: WorkspaceGroupData? = null
    private var timeStatistical: String = TimeStatistical.HANG_NGAY.value
    private var timeType: String = TimeStatistical.HANG_NGAY.time
    private var statsWorkSpace: StatsWorkSpace? = null
    private var otherUtilitiesAdapter: OtherUtilitiesAdapter? = null
    private var groupData: GroupData? = null

    private var resultLauncherSwitchWifi =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeProtectWifi.isChecked =
                    SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedWifiHome()
            }
        }

    @SuppressLint("SetTextI18n")
    private var resultLauncherProtectDevice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeProtectDevice.isChecked =
                    SharePreferenceKeyHelper.getInstance(ViSafeApp()).getBoolean(PreferenceKey.STATUS_OPEN_VPN)
            }
        }

    @SuppressLint("SetTextI18n")
    private var resultLauncherBlockAds =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                mWorkspaceGroupData = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getWorkspaceChoose()
                mWorkspaceGroupData?.let { workSpaceChoose ->
                    doGetAGroupWithId(workSpaceChoose)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private var resultLauncherBlockTracking =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                mWorkspaceGroupData = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getWorkspaceChoose()
                mWorkspaceGroupData?.let { workSpaceChoose ->
                    doGetAGroupWithId(workSpaceChoose)
                }
            }
        }

    private var resultLauncherAdvancedScan =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN).isNotEmpty()) {
                    val time = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN)
                    binding.tvTimeScan.text = "Lần quét gần đây nhất ${getTimeAgo(time.toLong())}"
                }
                val errorNumber = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.NUMBER_OF_ERROR)
                if (errorNumber.isNotEmpty()
                    && "0" != errorNumber
                ) {
                    binding.tvTitle.text = HtmlCompat.fromHtml(
                        getString(R.string.title_your_dangerous, errorNumber),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    binding.tvTitle.text = HtmlCompat.fromHtml(
                        getString(R.string.title_your_save),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
            }
        }

    private var resultLauncherCreateGroup =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                (activity as MainActivity).openWorkspaceTab()
            }
        }

    override fun layoutRes(): Int = R.layout.fragment_over_view_protect

    @SuppressLint("LongLogTag", "RestrictedApi")
    override fun initView() {
        (activity as MainActivity).isLoadView.observe(this, {
            if ((activity as BaseActivity).isLogin()) {
                binding.layoutHomeProtect.llHomeBlockAds.visibility = View.GONE
                binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.GONE
                binding.cardViewStatistical.visibility = View.VISIBLE
                binding.layoutAddVpn.cardViewAddVpn.visibility = if (SharePreferenceKeyHelper.getInstance(ViSafeApp())
                        .getBoolean(PreferenceKey.STATUS_OPEN_VPN)
                ) View.GONE else View.GONE
                binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.VISIBLE
                binding.layoutUpgrade.llRegisterNow.visibility = View.GONE
                binding.layoutHomePass.cvHomePass.visibility =
                    if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.PIN_CODE)
                            .isEmpty()
                    ) View.VISIBLE else View.GONE
            } else {
                binding.layoutHomeProtect.llHomeBlockAds.visibility = View.GONE
                binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.GONE
                binding.cardViewStatistical.visibility = View.GONE
                binding.layoutAddVpn.cardViewAddVpn.visibility = if (SharePreferenceKeyHelper.getInstance(ViSafeApp())
                        .getBoolean(PreferenceKey.STATUS_OPEN_VPN)
                ) View.GONE else View.VISIBLE
                binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.GONE
                binding.layoutUpgrade.llRegisterNow.visibility = View.VISIBLE
                binding.layoutHomePass.cvHomePass.visibility =
                    if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.PIN_CODE)
                            .isEmpty()
                    ) View.VISIBLE else View.GONE
            }
        })

        val pin = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE)
        binding.viewStatistical.tvOverview.text = "Tổng quan của thiết bị"
        binding.layoutHomePass.view.visibility = if (pin.isNotEmpty()) View.VISIBLE else View.GONE
        (activity as MainActivity).listWorkSpaceLiveData.observe(this, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    for (workSpace in it) {
                        if (workSpace.id == SharePreferenceKeyHelper.getInstance(ViSafeApp()).getWorkspaceChoose().id) {
                            mWorkspaceGroupData = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getWorkspaceChoose()
                            mWorkspaceGroupData?.let { workSpaceChoose ->
                                doGetAGroupWithId(workSpaceChoose)
                            }
                            return@observe
                        }
                    }
                    it[0].let { workspaceGroupData ->
                        mWorkspaceGroupData = workspaceGroupData
                        doGetAGroupWithId(workspaceGroupData)
                    }
                }
            }
        })
        EventUtils.isCreatePass.observe(this, {
            binding.layoutHomePass.cvHomePass.visibility = if (it) View.GONE else View.VISIBLE
        })

        if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN).isEmpty()) {
            binding.tvTitle.text = "Thiết bị của bạn có an toàn?"
            binding.tvTimeScan.visibility = View.GONE
            binding.tvScan.text = "KIỂM TRA NGAY"
        } else {
            val time = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN)
            binding.tvTimeScan.text = "Lần quét gần đây nhất ${getTimeAgo(time.toLong())}"
            binding.tvTimeScan.visibility = View.VISIBLE
            binding.tvScan.text = "QUÉT LẦN NỮA"
            val errorNumber = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.NUMBER_OF_ERROR)
            if (errorNumber.isNotEmpty()
                && "0" != errorNumber
            ) {
                binding.tvTitle.text = HtmlCompat.fromHtml(
                    getString(R.string.title_your_dangerous, errorNumber),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else {
                binding.tvTitle.text = HtmlCompat.fromHtml(
                    getString(R.string.title_your_save),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }


        (activity as MainActivity).timeScanUpdate.observe(this, {
            if (it.isEmpty()) {
                binding.tvTitle.text = "Thiết bị của bạn có an toàn?"
                binding.tvTimeScan.visibility = View.GONE
                binding.tvScan.text = "KIỂM TRA NGAY"
            } else {
                binding.tvTimeScan.text = "Lần quét gần đây nhất ${getTimeAgo(it.toLong())}"
                binding.tvTimeScan.visibility = View.VISIBLE
                binding.tvScan.text = "QUÉT LẦN NỮA"
                val errorNumber = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.NUMBER_OF_ERROR)
                if (errorNumber.isNotEmpty()
                    && "0" != errorNumber
                ) {
                    binding.tvTitle.text = HtmlCompat.fromHtml(
                        getString(R.string.title_your_dangerous, errorNumber),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    binding.tvTitle.text = HtmlCompat.fromHtml(
                        getString(R.string.title_your_save),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
            }
        })

        //update switch protect device
        (activity as MainActivity).isOpenProtectedDevice.observe(this, {
            binding.layoutHomeProtect.switchHomeProtectDevice.isChecked = it
            binding.layoutHomeProtect.ivHomeProtectDevice.setImageResource(
                if (it) {
                    R.drawable.ic_mobile
                } else {
                    R.drawable.ic_info_circle
                }
            )
            binding.layoutAddVpn.cardViewAddVpn.visibility = if (it) View.GONE else View.VISIBLE
        })

        binding.layoutAddVpn.btnHomeVpnAdd.text =
            if (SharePreferenceKeyHelper.getInstance(ViSafeApp())
                    .getBoolean(PreferenceKey.STATUS_OPEN_VPN)
            ) "Đã kết nối VPN" else "Thêm VPN"
        binding.layoutHomeProtect.switchHomeProtectWifi.isChecked =
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedWifiHome()

        binding.layoutHomeProtect.switchHomeProtectDevice.isChecked =
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).getBoolean(PreferenceKey.STATUS_OPEN_VPN)

        binding.layoutHomeProtect.ivHomeProtectDevice.setImageResource(
            if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getBoolean(PreferenceKey.STATUS_OPEN_VPN)) {
                R.drawable.ic_mobile
            } else {
                R.drawable.ic_info_circle
            }
        )

        if ((activity as BaseActivity).isLogin()) {
            binding.layoutHomeProtect.llHomeBlockAds.visibility = View.GONE
            binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.GONE
            binding.cardViewStatistical.visibility = View.VISIBLE
            binding.layoutAddVpn.cardViewAddVpn.visibility = View.VISIBLE
            binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.VISIBLE
            binding.layoutUpgrade.llRegisterNow.visibility = View.GONE
        } else {
            binding.layoutHomeProtect.llHomeBlockAds.visibility = View.GONE
            binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.GONE
            binding.cardViewStatistical.visibility = View.GONE
            binding.layoutAddVpn.cardViewAddVpn.visibility = View.GONE
            binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.GONE
            binding.layoutUpgrade.llRegisterNow.visibility = View.VISIBLE
        }

        //show thong tin thanh toan
        otherUtilitiesAdapter = OtherUtilitiesAdapter(getListUtilities(), this)
        val gridLayoutManager: RecyclerView.LayoutManager
        gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.setAutoMeasureEnabled(true)
        binding.layoutUtilities.rcvOtherUtilities.layoutManager = gridLayoutManager
        binding.layoutUtilities.rcvOtherUtilities.itemAnimator = DefaultItemAnimator()
        binding.layoutUtilities.rcvOtherUtilities.addItemDecoration(SpaceItemDecoration(dpToPx(requireContext(), 10)))
        binding.layoutUtilities.rcvOtherUtilities.isNestedScrollingEnabled = false
        binding.layoutUtilities.rcvOtherUtilities.adapter = otherUtilitiesAdapter
        initControl()
    }

    private var resultLauncherOpenInputPin =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                (activity as MainActivity).isOpenProtectedDevice.value = false
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                (activity as MainActivity).isOpenProtectedDevice.value = true
            }
        }

    private fun initControl() {
        binding.layoutAddVpn.btnHomeVpnAdd.setOnSingClickListener {
            if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getBoolean(PreferenceKey.STATUS_OPEN_VPN)) {
                (activity as MainActivity).isOpenProtectedDevice.value = false
            } else {
                showAddVpn()
            }
        }
        binding.tvScan.setOnSingClickListener {
            val intent = Intent(requireContext(), AdvancedScanActivity::class.java)
            resultLauncherAdvancedScan.launch(intent)
        }
        binding.layoutHomeWebsite.btnHomeWebsiteReport.setOnSingClickListener {
            startActivity(Intent(requireContext(), WebsiteReportActivity::class.java))
        }
        binding.layoutHomeProtectFamily.btnHomeFamilyAddGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            intent.putExtra(
                GroupManagementFragment.DATA_WORKSPACE,
                SharePreferenceKeyHelper.getInstance(ViSafeApp()).getWorkspaceChoose()
            )
            resultLauncherCreateGroup.launch(intent)
        }

        //bảo vệ thiết bị
        binding.layoutHomeProtect.llHomeProtectDevice.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROTECT)) {
                return@setOnSingClickListener
            }
            val intent = Intent(requireContext(), ProtectDeviceActivity::class.java)
            intent.putExtra(ProtectDeviceActivity.DATA_GROUP_KEY, groupData)
            intent.putExtra(
                ProtectDeviceActivity.NUM_DANGEROUS_DOMAIN,
                statsWorkSpace?.num_dangerous_domain
            )
            intent.putExtra(
                ProtectDeviceActivity.NUM_DANGEROUS_DOMAIN_ALL,
                statsWorkSpace?.num_dangerous_domain_all
            )
            resultLauncherProtectDevice.launch(intent)
        }
        //switch thiết bị
        binding.layoutHomeProtect.switchHomeProtectDevice.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {//switchWidget: ", "click
                if (ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE).isNotEmpty()) {
                    if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getBoolean(PreferenceKey.STATUS_OPEN_VPN)) {
                        val intent = Intent(context, UpdatePinActivity::class.java)
                        intent.putExtra(UpdatePinActivity.TYPE_ACTION, UpdatePinActivity.IS_CONFIRM_PIN)
                        resultLauncherOpenInputPin.launch(intent)
                    } else {
                        (activity as MainActivity).isOpenProtectedDevice.value = true
                    }
                } else {
                    SharePreferenceKeyHelper.getInstance(ViSafeApp()).putBoolean(PreferenceKey.STATUS_OPEN_VPN, isChecked)
                    binding.layoutHomeProtect.ivHomeProtectDevice.setImageResource(
                        if (isChecked) {
                            R.drawable.ic_mobile
                        } else {
                            R.drawable.ic_info_circle
                        }
                    )
                    (activity as MainActivity).isOpenProtectedDevice.value = isChecked
                }
            } else {//"switchWidget: ", "setChecked "
                //triggered due to programmatic assignment using 'setChecked()' method.
            }
        }

        //bảo vệ wifi
        binding.layoutHomeProtect.llHomeProtectWifi.setOnSingClickListener {
//            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROTECT)) {
//                return@setOnSingClickListener
//            }
            val intent = Intent(requireContext(), ProtectWifiActivity::class.java)
            intent.putExtra(
                ProtectWifiActivity.PROTECT_WIFI_KEY,
                binding.layoutHomeProtect.switchHomeProtectWifi.isChecked
            )
            resultLauncherSwitchWifi.launch(intent)
        }
        //switch wifi
        binding.layoutHomeProtect.switchHomeProtectWifi.setOnCheckedChangeListener { _, isChecked ->
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).putBoolean(PreferenceKey.IS_ENABLE_PROTECTED_WIFI_HOME, isChecked)
        }

        //chặn quảng cáo
        binding.layoutHomeProtect.llHomeBlockAds.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROTECT)) {
                return@setOnSingClickListener
            }
            val intent = Intent(requireContext(), BlockAdsGroupDetailActivity::class.java)
            intent.putExtra(BlockAdsGroupDetailActivity.DATA_GROUP_KEY, groupData)
            intent.putExtra(BlockAdsGroupDetailActivity.NUM_ADS_BLOCKED, statsWorkSpace?.num_ads_blocked)
            intent.putExtra(BlockAdsGroupDetailActivity.NUM_ADS_BLOCKED_ALL, statsWorkSpace?.num_ads_blocked_all)
            resultLauncherBlockAds.launch(intent)
        }
        //switch quảng cáo
        binding.layoutHomeProtect.switchHomeBlockAds.setOnCheckedChangeListener { _, isChecked ->
            groupData?.adblock_enabled = isChecked
            groupData?.game_ads_enabled = isChecked
            groupData?.app_ads =
                if (isChecked) listOf("instagram", "youtube", "spotify", "facebook") else listOf()
            doUpdateGroup(groupData)
        }

        //chặn theo dõi
        binding.layoutHomeProtect.llHomeBlockTracking.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROTECT)) {
                return@setOnSingClickListener
            }
            val intent = Intent(requireContext(), BlockTrackingGroupDetailActivity::class.java)
            intent.putExtra(BlockTrackingGroupDetailActivity.DATA_GROUP_KEY, groupData)
            intent.putExtra(
                BlockTrackingGroupDetailActivity.NUM_BLOCKED_TRACKING,
                statsWorkSpace?.num_native_tracking
            )
            intent.putExtra(
                BlockTrackingGroupDetailActivity.NUM_BLOCKED_TRACKING_ALL,
                statsWorkSpace?.num_native_tracking_all
            )
            resultLauncherBlockTracking.launch(intent)
        }
        //switch theo dõi
        binding.layoutHomeProtect.switchHomeBlockTracking.setOnCheckedChangeListener { _, isChecked ->
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
            doUpdateGroup(groupData)
        }

        binding.viewStatistical.tvTime.setOnSingClickListener {
            DisplayStatisticalForTimeBottomSheet(object : OnClickItemTime {
                override fun onClickItemTime(item: TimeStatistical) {
                    if (timeType == item.time)//không reload khi click lại ngày đang chọn
                        return
                    getDataStatistical(item)
                    binding.viewStatistical.tvTime.text = item.time
                }

            }).show(parentFragmentManager, null)
        }

        //Tạo mật mã
        binding.layoutHomePass.btnHomePassCreate.setOnSingClickListener {
            val intent = Intent(requireContext(), UpdatePinActivity::class.java)
            startActivity(intent)
        }

        //Nâng cấp
        binding.layoutUpgrade.btnUpgradeNow.setOnSingClickListener {
            if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROTECT))
                return@setOnSingClickListener
            val userInfo = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getUserInfo()
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_PACKAGE, userInfo.AccountType)
            startActivity(intent)
        }
        binding.layoutUpgrade.btnRegister.setOnSingClickListener {
            resultLauncherRegisterActivity.launch(Intent(requireContext(), RegisterActivity::class.java))
        }
    }

    private var resultLauncherRegisterActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if ((activity as MainActivity).needLogin(MainActivity.POSITION_PROFILE))
                    return@registerForActivityResult
            }
        }

    private fun getDataStatistical(item: TimeStatistical) {
        timeStatistical = item.value
        timeType = item.time
        doGetStaticAGroup(true, TimeStatistical.HANG_NGAY.value)
    }

    private fun showAddVpn() {
        val dialog = ImageDialog.newsIntance(ImageDialog.TYPE_ADD_VPN)
        dialog.setOnClickListener {
            (activity as MainActivity).isOpenProtectedDevice.value = true
        }
        dialog.show(parentFragmentManager, null)
    }

    override fun invoke(data: OtherUtilitiesModel, position: Int) {
        val intentOpenWeb = Intent(context, WebViewActivity::class.java)
        intentOpenWeb.putExtra(WebViewActivity.URL_KEY, data.value)
        when (data.type) {
            TypeUtilities.TIN_TUC_CANH_BAO, TypeUtilities.KIEM_TRA_WEB_LUA_DAO,
            TypeUtilities.KIEM_TRA_WIFI, TypeUtilities.KIEM_TRA_DO_LOT_TK, TypeUtilities.NHAN_DIEN_MA_DOC_TONG_TIEN -> {
                startActivity(intentOpenWeb)
            }
            TypeUtilities.GUI_BAO_CAO -> {
                startActivity(Intent(requireContext(), WebsiteReportActivity::class.java))
            }
        }
    }

    private fun getListUtilities(): MutableList<OtherUtilitiesModel> {
        val list: MutableList<OtherUtilitiesModel> = mutableListOf()
        list.add(
            OtherUtilitiesModel(
                "Tin tức, cảnh báo", "https://congcu.khonggianmang.vn/news-feed",
                R.drawable.ic_newspaper, TypeUtilities.TIN_TUC_CANH_BAO
            )
        )
        list.add(
            OtherUtilitiesModel(
                "Gửi cảnh báo", "",
                R.drawable.ic_send_alert_email, TypeUtilities.GUI_BAO_CAO
            )
        )
        list.add(
            OtherUtilitiesModel(
                "Kiểm tra WiFi", "https://congcu.khonggianmang.vn/check-ipma",
                R.drawable.ic_ipma_check_wifi, TypeUtilities.KIEM_TRA_WIFI
            )
        )
        list.add(
            OtherUtilitiesModel(
                "Kiểm tra độ lọt tài khoản", "https://congcu.khonggianmang.vn/check-data-leak",
                R.drawable.ic_check_data_leak, TypeUtilities.KIEM_TRA_DO_LOT_TK
            )
        )
        list.add(
            OtherUtilitiesModel(
                "Kiểm tra website lừa đảo", "https://congcu.khonggianmang.vn/check-phishing",
                R.drawable.ic_check_phishing, TypeUtilities.KIEM_TRA_WEB_LUA_DAO
            )
        )
        list.add(
            OtherUtilitiesModel(
                "Nhận diện mã độc tống tiền", "https://congcu.khonggianmang.vn/ransomware",
                R.drawable.ic_ransomware, TypeUtilities.NHAN_DIEN_MA_DOC_TONG_TIEN
            )
        )
        return list
    }

    private fun doGetAGroupWithId(workspaceGroupData: WorkspaceGroupData) {
        if (!(activity as BaseActivity).isLogin())
            return
        showProgressDialog()
        if (workspaceGroupData.groupIds?.size!! > 0) {
            val id = workspaceGroupData.groupIds?.get(0)
            val client = NetworkClient()
            val call = client.client(context = requireContext()).doGetAGroupWithId(id)
            call.enqueue(BaseCallback(this, object : Callback<GroupData> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<GroupData>,
                    response: Response<GroupData>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let {
                            groupData = it
                            binding.layoutHomeProtect.switchHomeBlockAds.isChecked = it.adblock_enabled ||
                                    it.game_ads_enabled ||
                                    it.app_ads?.isNotEmpty() == true
                            binding.layoutHomeProtect.switchHomeBlockTracking.isChecked =
                                it.native_tracking?.isNotEmpty() == true
                            doGetStaticAGroup(false, TimeStatistical.HANG_NGAY.value)
//                            doGetStaticDevice(it, TimeStatistical.HANG_NGAY.value)
                        }
                    } else {
                        dismissProgress()
                    }

                }

                override fun onFailure(call: Call<GroupData>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                    dismissProgress()
                }
            }))
        }
    }

    fun doGetStaticAGroup(isShowProgress: Boolean, timeLimit: String) {
        if (isShowProgress)
            showProgressDialog()
        val userInfo = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getUserInfo()
        val client = NetworkClient()
        val call = client.client(context = requireContext()).doGetStatisticalOneGroup(userInfo.DefaultGroup, timeLimit)
        call.enqueue(BaseCallback(this, object : Callback<StatsWorkspaceResponse> {
            override fun onResponse(
                call: Call<StatsWorkspaceResponse>,
                response: Response<StatsWorkspaceResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let { data ->
                        statsWorkSpace = StatsWorkSpace(data)
                        binding.viewStatistical.tvValueDangerous.text = data.num_dangerous_domain.toString()
                        binding.viewStatistical.tvValueAds.text = data.num_ads_blocked.toString()
                        binding.viewStatistical.tvValueViolate.text = data.num_violation.toString()
                    }
                }
                if (isShowProgress)
                    dismissProgress()
            }

            override fun onFailure(call: Call<StatsWorkspaceResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doUpdateGroup(data: GroupData?) {
        data?.let {
            if (!(activity as BaseActivity).isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = requireContext()).doUpdateGroup(it)
            call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        groupData = data
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

    fun doGetStaticDevice(groupData: GroupData?, timeLimit: String) {
        val userInfo = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getUserInfo()
        val deviceId = SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.DEVICE_ID)
        groupData?.let {
            if (it.ids?.size == 0) {
                return@let
            }
            val client = NetworkClient()
            val call =
                client.client(context = requireContext())
                    .doGetStatisticalOneDeviceInGroup(userInfo.DefaultGroup, deviceId, timeLimit)
            call.enqueue(object : Callback<StatsWorkspaceResponse> {
                override fun onResponse(
                    call: Call<StatsWorkspaceResponse>,
                    response: Response<StatsWorkspaceResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let { data ->
                            binding.viewStatistical.tvValueDangerous.text = data.num_dangerous_domain.toString()
                            binding.viewStatistical.tvValueAds.text = data.num_ads_blocked.toString()
                            binding.viewStatistical.tvValueViolate.text = data.num_violation.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<StatsWorkspaceResponse>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                }
            })
        }
    }
}