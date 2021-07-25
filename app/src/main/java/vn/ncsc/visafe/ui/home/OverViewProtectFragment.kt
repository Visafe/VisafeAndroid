package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentOverViewProtectBinding
import vn.ncsc.visafe.model.StatsWorkSpace
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.WebViewActivity
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.authentication.RegisterActivity
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.dialog.DisplayStatisticalForTimeBottomSheet
import vn.ncsc.visafe.ui.dialog.ImageDialog
import vn.ncsc.visafe.ui.dialog.OnClickItemTime
import vn.ncsc.visafe.ui.pin.UpdatePinActivity
import vn.ncsc.visafe.ui.protect.BlockAdsActivity
import vn.ncsc.visafe.ui.protect.BlockTrackingDetailActivity
import vn.ncsc.visafe.ui.protect.ProtectDeviceActivity
import vn.ncsc.visafe.ui.protect.ProtectWifiActivity
import vn.ncsc.visafe.ui.upgrade.UpgradeActivity
import vn.ncsc.visafe.ui.website.WebsiteReportActivity
import vn.ncsc.visafe.utils.EventUtils
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setOnSingClickListener

class OverViewProtectFragment : BaseFragment<FragmentOverViewProtectBinding>() {

    private var mWorkspaceGroupData: WorkspaceGroupData? = null
    private var timeStatistical: String = TimeStatistical.HANG_NGAY.value
    private var timeType: String = TimeStatistical.HANG_NGAY.time
    private var statsWorkSpace: StatsWorkSpace? = null

    companion object {
        @JvmStatic
        fun newInstance() = OverViewProtectFragment()
    }

    private var resultLauncherSwitchWifi =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeProtectWifi.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedWifiHome()
            }
        }
    private var resultLauncherSwitchDevice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeProtectDevice.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedDeviceHome()
            }
        }
    private var resultLauncherSwitchAds =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeBlockAds.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableBlockAdsHome()
            }
        }
    private var resultLauncherSwitchFollow =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.layoutHomeProtect.switchHomeBlockTracking.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableBlockFollowHome()
            }
        }

    override fun layoutRes(): Int = R.layout.fragment_over_view_protect

    @SuppressLint("LongLogTag")
    override fun initView() {
        val pin = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
        binding.layoutHomePass.view.visibility = if (pin.isNotEmpty()) View.VISIBLE else View.GONE
        (activity as MainActivity).timeTypes.observe(this, {
            if (it.isNotEmpty()) {
                timeType = it
                binding.viewStatistical.tvTime.text = it
            }
        })
        (activity as MainActivity).listWorkSpaceLiveData.observe(this, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    it[0].let { workspaceGroupData ->
                        mWorkspaceGroupData = workspaceGroupData
                        (activity as MainActivity).doGetStaticWorkspace(workspaceGroupData, timeStatistical)
                    }
                }
            }
        })
        (activity as MainActivity).statisticalWorkSpaceLiveData.observe(this, {
            statsWorkSpace = it
            binding.viewStatistical.tvValueDangerous.text = it.num_dangerous_domain.toString()
            binding.viewStatistical.tvValueAds.text = it.num_ads_blocked.toString()
            binding.viewStatistical.tvValueViolate.text = it.num_violation.toString()
        })
        EventUtils.isCreatePass.observe(this, {
            binding.layoutHomePass.cvHomePass.visibility = if (it) View.GONE else View.VISIBLE
        })

        binding.layoutHomeProtect.switchHomeProtectWifi.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedWifiHome()
        binding.layoutHomeProtect.switchHomeProtectDevice.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableProtectedDeviceHome()
        if ((activity as BaseActivity).isLogin()) {
            binding.layoutHomeProtect.llHomeBlockAds.visibility = View.VISIBLE
            binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.VISIBLE
            binding.layoutHomeProtect.switchHomeBlockAds.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableBlockAdsHome()
            binding.layoutHomeProtect.switchHomeBlockTracking.isChecked = SharePreferenceKeyHelper.getInstance(ViSafeApp()).isEnableBlockFollowHome()
            binding.cardViewStatistical.visibility = View.VISIBLE
            binding.layoutAddVpn.cardViewAddVpn.visibility = View.VISIBLE
            binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.VISIBLE
        } else {
            binding.layoutHomeProtect.llHomeBlockAds.visibility = View.GONE
            binding.layoutHomeProtect.llHomeBlockTracking.visibility = View.GONE
            binding.cardViewStatistical.visibility = View.GONE
            binding.layoutAddVpn.cardViewAddVpn.visibility = View.GONE
            binding.layoutHomeProtectFamily.cardViewHomeProtectFamily.visibility = View.GONE
        }
        initControl()
    }

    private fun initControl() {
        binding.layoutAddVpn.btnHomeVpnAdd.setOnSingClickListener {
            showAddVpn()
        }
        binding.layoutHomeWebsite.btnHomeWebsiteReport.setOnSingClickListener {
            startActivity(Intent(requireContext(), WebsiteReportActivity::class.java))
        }
        binding.layoutHomeProtectFamily.btnHomeFamilyAddGroup.setOnSingClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            intent.putExtra(GroupManagementFragment.DATA_WORKSPACE, mWorkspaceGroupData)
            startActivity(intent)
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

        //bảo vệ thiết bị
        binding.layoutHomeProtect.llHomeProtectDevice.setOnSingClickListener {
            val intent = Intent(requireContext(), ProtectDeviceActivity::class.java)
            intent.putExtra(
                ProtectDeviceActivity.PROTECT_DEVICE_KEY,
                binding.layoutHomeProtect.switchHomeProtectDevice.isChecked
            )
            intent.putExtra(ProtectDeviceActivity.STATIS_WORKSPACE_DATA, statsWorkSpace)
            resultLauncherSwitchDevice.launch(intent)
        }
        //switch thiết bị
        binding.layoutHomeProtect.switchHomeProtectDevice.setOnCheckedChangeListener { _, isChecked ->
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).putBoolean(PreferenceKey.IS_ENABLE_PROTECTED_DEVICE_HOME, isChecked)
        }

        //bảo vệ wifi
        binding.layoutHomeProtect.llHomeProtectWifi.setOnSingClickListener {
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
            if ((activity as BaseActivity).needLogin()) {
                return@setOnSingClickListener
            }
            val intent = Intent(requireContext(), BlockAdsActivity::class.java)
            intent.putExtra(
                BlockAdsActivity.BLOCK_ADS_KEY,
                binding.layoutHomeProtect.switchHomeBlockAds.isChecked
            )
            resultLauncherSwitchAds.launch(intent)
        }
        //switch quảng cáo
        binding.layoutHomeProtect.switchHomeBlockAds.setOnCheckedChangeListener { _, isChecked ->
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).putBoolean(PreferenceKey.IS_ENABLE_BLOCK_ADS_HOME, isChecked)
        }

        //chặn theo dõi
        binding.layoutHomeProtect.llHomeBlockTracking.setOnSingClickListener {
            if ((activity as BaseActivity).needLogin()) {
                return@setOnSingClickListener
            }
            val intent = Intent(requireContext(), BlockTrackingDetailActivity::class.java)
            intent.putExtra(
                BlockTrackingDetailActivity.BLOCK_TRACKING_KEY,
                binding.layoutHomeProtect.switchHomeBlockTracking.isChecked
            )
            resultLauncherSwitchFollow.launch(intent)
        }
        //switch theo dõi
        binding.layoutHomeProtect.switchHomeBlockTracking.setOnCheckedChangeListener { _, isChecked ->
            SharePreferenceKeyHelper.getInstance(ViSafeApp()).putBoolean(PreferenceKey.IS_ENABLE_BLOCK_FOLLOW_HOME, isChecked)
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
            if ((activity as BaseActivity).needLogin())
                return@setOnSingClickListener
            val intent = Intent(requireContext(), UpgradeActivity::class.java)
            intent.putExtra(UpgradeActivity.CURRENT_VERSION_KEY, UpgradeActivity.TYPE_REGISTER)
            startActivity(intent)
        }
        binding.layoutUpgrade.btnRegister.setOnSingClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        binding.layoutUtilities.viewUtilities.setOnSingClickListener {
            startActivity(Intent(context, WebViewActivity::class.java))
        }
    }

    private fun getDataStatistical(item: TimeStatistical) {
        mWorkspaceGroupData?.let { workSpaceData ->
            timeStatistical = item.value
            (activity as MainActivity).timeTypes.value = item.time
            (activity as MainActivity).doGetStaticWorkspace(
                workSpaceData,
                timeStatistical
            )
        }
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