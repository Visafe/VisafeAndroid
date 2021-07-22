package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentOverViewProtectBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.WebViewActivity
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.create.group.CreateGroupActivity
import vn.ncsc.visafe.ui.dialog.DisplayStatisticalForTimeBottomSheet
import vn.ncsc.visafe.ui.dialog.ImageDialog
import vn.ncsc.visafe.ui.dialog.OnClickItemTime
import vn.ncsc.visafe.ui.protect.BlockAdsActivity
import vn.ncsc.visafe.ui.protect.BlockTrackingDetailActivity
import vn.ncsc.visafe.ui.protect.ProtectDeviceActivity
import vn.ncsc.visafe.ui.protect.ProtectWifiActivity
import vn.ncsc.visafe.ui.website.WebsiteReportActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.setOnSingClickListener

class OverViewProtectFragment : BaseFragment<FragmentOverViewProtectBinding>() {

    private var mWorkspaceGroupData: WorkspaceGroupData? = null
    private var timeStatistical: String = TimeStatistical.HANG_NGAY.value
    private var timeType: String = TimeStatistical.HANG_NGAY.time

    companion object {
        @JvmStatic
        fun newInstance() = OverViewProtectFragment()
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
            val gson = Gson()
            Log.e("OverViewProtectFrg StaticWorkspace: ", gson.toJson(it))
            binding.viewStatistical.tvValueDangerous.text = it.num_dangerous_domain.toString()
            binding.viewStatistical.tvValueAds.text = it.num_ads_blocked.toString()
            binding.viewStatistical.tvValueViolate.text = it.num_violation.toString()
        })
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

        //bảo vệ thiết bị
        binding.layoutHomeProtect.llHomeProtectDevice.setOnSingClickListener {
            val intent = Intent(requireContext(), ProtectDeviceActivity::class.java)
            intent.putExtra(
                ProtectDeviceActivity.PROTECT_DEVICE_KEY,
                binding.layoutHomeProtect.switchHomeProtectDevice.isChecked
            )
            startActivity(intent)
        }

        //bảo vệ wifi
        binding.layoutHomeProtect.llHomeProtectWifi.setOnSingClickListener {
            val intent = Intent(requireContext(), ProtectWifiActivity::class.java)
            intent.putExtra(
                ProtectWifiActivity.PROTECT_WIFI_KEY,
                binding.layoutHomeProtect.switchHomeProtectWifi.isChecked
            )
            startActivity(intent)
        }
        binding.layoutHomeProtect.switchHomeProtectWifi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val intent = Intent(requireContext(), ProtectWifiActivity::class.java)
                intent.putExtra(
                    ProtectWifiActivity.PROTECT_WIFI_KEY,
                    binding.layoutHomeProtect.switchHomeProtectWifi.isChecked
                )
                startActivity(intent)
            }
        }

        //chặn quảng cáo
        binding.layoutHomeProtect.llHomeBlockAds.setOnSingClickListener {
            val intent = Intent(requireContext(), BlockAdsActivity::class.java)
            intent.putExtra(
                BlockAdsActivity.BLOCK_ADS_KEY,
                binding.layoutHomeProtect.switchHomeBlockAds.isChecked
            )
            startActivity(intent)
        }

        //chặn theo dõi
        binding.layoutHomeProtect.llHomeBlockTracking.setOnSingClickListener {
            val intent = Intent(requireContext(), BlockTrackingDetailActivity::class.java)
            intent.putExtra(
                BlockTrackingDetailActivity.BLOCK_TRACKING_KEY,
                binding.layoutHomeProtect.switchHomeBlockTracking.isChecked
            )
            startActivity(intent)
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