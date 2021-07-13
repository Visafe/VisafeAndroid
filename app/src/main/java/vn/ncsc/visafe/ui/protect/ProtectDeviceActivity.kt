package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import vn.ncsc.visafe.ui.adapter.ProtectDeviceAdapter
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityProtectDeviceBinding
import vn.ncsc.visafe.model.DeviceData
import vn.ncsc.visafe.ui.adapter.DeviceAdapter
import vn.ncsc.visafe.ui.adapter.OnClickDevice
import vn.ncsc.visafe.utils.ChartUtil
import vn.ncsc.visafe.utils.OnSingleClickListener

class ProtectDeviceActivity : BaseActivity() {
    companion object {
        const val PROTECT_DEVICE_KEY = "PROTECT_DEVICE_KEY"
    }

    lateinit var binding: ActivityProtectDeviceBinding
    private lateinit var adapter: ProtectDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProtectDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })

        val isProtected = intent.extras?.getBoolean(PROTECT_DEVICE_KEY, false) ?: false
        binding.switchProtectDevice.isChecked = isProtected
        handleProtected(isProtected)
        binding.switchProtectDevice.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.switchProtectDevice.isChecked = isChecked
            handleProtected(isChecked)
        }

        adapter = ProtectDeviceAdapter(createDeviceList(), this)
        binding.rvData.adapter = adapter
        adapter.setOnClickListener(object : OnClickDevice {
            override fun onClickDevice(data: DeviceData, position: Int) {

            }

            override fun onMoreDevice(data: DeviceData, position: Int) {
//                showDialogEdit(data, position)
            }

        })
    }

    private fun handleProtected(isProtected: Boolean) {
        if (isProtected) {
            binding.llProtected.visibility = View.VISIBLE
            binding.llNoProtect.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_checkmark_circle)
            binding.ivDevice.background = ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            initDataChart()
            binding.tvTitle.text = HtmlCompat.fromHtml(getString(R.string.protected_device), HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.llProtected.visibility = View.GONE
            binding.llNoProtect.visibility = View.VISIBLE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivDevice.background = ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(getString(R.string.no_protected_device), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun createDeviceList(): ArrayList<DeviceData> {
        val list: ArrayList<DeviceData> = ArrayList()
        list.add(DeviceData("https://www.facebook.com/", "Trần Thành Long", true, 12))
        list.add(DeviceData("https://www.facebook.com/", "Trần Thành Long", false, 11))
        list.add(DeviceData("https://www.facebook.com/", "Trần Thành Long", true, 40))
        list.add(DeviceData("https://www.facebook.com/", "Trần Thành Long", false, 50))
        list.add(DeviceData("https://www.facebook.com/", "Trần Thành Long", false, 60))
        return list
    }

    private fun initDataChart() {
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
        ChartUtil.initBarChart(binding.hiChartView, dataChart, ChartUtil.getArrayColor(dataChart.size))
    }
}