package vn.ncsc.visafe.ui.group.detail.setup_protect

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityBlockTrackingGroupDetailBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.QueryLogData
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.model.request.DeleteLogRequest
import vn.ncsc.visafe.model.request.UpdateWhiteListRequest
import vn.ncsc.visafe.model.response.QueryLogResponse
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.ui.group.detail.BaseSetupProtectActivity
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.getTimeAgo
import vn.ncsc.visafe.utils.setOnSingClickListener
import java.text.SimpleDateFormat
import java.util.*

class BlockTrackingGroupDetailActivity : BaseSetupProtectActivity(), OnClickMoreItemQuery,
    BaseSetupProtectActivity.OnUpdateSuccess {

    companion object {
        const val NUM_BLOCKED_TRACKING = "NUM_BLOCKED_TRACKING"
        const val NUM_BLOCKED_TRACKING_ALL = "NUM_BLOCKED_TRACKING_ALL"
        const val DATA_GROUP_KEY = "DATA_GROUP_KEY"
    }

    lateinit var binding: ActivityBlockTrackingGroupDetailBinding
    private var groupData: GroupData? = null
    private var adapter: QueryLogAdapter? = null
    private var queryLogList: MutableList<QueryLogData> = mutableListOf()
    private var numBlockedTracking: Int = 0
    private var numBlockedTrackingAll: Int = 0
    private var isBlockedTracking: Boolean = false
    private var nativeTrackingList: MutableList<String> = mutableListOf()
    private var listNativeTrackingDefault: ArrayList<Subject> = arrayListOf()

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockTrackingGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(DATA_GROUP_KEY)
            numBlockedTracking = it.getIntExtra(NUM_BLOCKED_TRACKING, 0)
            numBlockedTrackingAll = it.getIntExtra(NUM_BLOCKED_TRACKING_ALL, 0)
        }
        initView()
        initControl()
    }

    private fun initView() {
        initSetupBlock()
        binding.tvTitle.text = HtmlCompat.fromHtml(
            getString(R.string.title_protect_device_group_detail),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvAmountEvery.text = numBlockedTrackingAll.toString()
        binding.tvAmountToday.text = numBlockedTracking.toString()

        adapter = QueryLogAdapter(queryLogList, this)
        binding.rcvBlockTracking.adapter = adapter
        adapter?.setOnClickListener(this)

        groupData?.let {
            binding.tvDescription.text = it.name
            isBlockedTracking = groupData?.native_tracking?.isNotEmpty() == true
            binding.switchBlockTracking.isChecked = isBlockedTracking
            handleProtected(isBlockedTracking)
            //init data group
            binding.itemBlockTrackingDevice.setChecked(groupData?.native_tracking?.isNotEmpty() == true)
            nativeTrackingList.clear()
            it.native_tracking?.toMutableList()?.let { listBlockTracking -> nativeTrackingList.addAll(listBlockTracking) }
            for (i in nativeTrackingList) {
                for (j in 0 until listNativeTrackingDefault.size) {
                    if (i == listNativeTrackingDefault[j].value) {
                        listNativeTrackingDefault[j].isChecked = true
                    }
                }
            }
            binding.itemBlockTrackingDevice.reloadData()
        }
    }

    private fun initControl() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })

        binding.switchBlockTracking.setOnCheckedChangeListener { _, isChecked ->
            binding.itemBlockTrackingDevice.setChecked(isChecked)
            binding.switchBlockTracking.isChecked = isChecked
            groupData?.native_tracking = if (isChecked) listOf(
                "alexa",
                "apple",
                "huawei",
                "roku",
                "samsung",
                "sonos",
                "windows",
                "xiaomi"
            ) else listOf()
            handleProtected(isChecked)
            doUpdateGroup(groupData, this)
        }

        binding.itemBlockTrackingDevice.setOnSwitchChangeListener { isChecked ->
            groupData?.native_tracking = if (isChecked) listOf(
                "alexa",
                "apple",
                "huawei",
                "roku",
                "samsung",
                "sonos",
                "windows",
                "xiaomi"
            ) else listOf()
            doUpdateGroup(groupData, this)
        }
        binding.itemBlockTrackingDevice.setOnSwitchItemChangeListener { isChecked, position ->
            Log.e("initControl: ", " " + position + " " + isChecked + " " + nativeTrackingList.size)
//            if (isChecked) {
//                nativeTrackingList.add(position, listNativeTrackingDefault[position].value)
//            } else {
//                nativeTrackingList.removeAt(position)
//            }
//            groupData?.native_tracking = nativeTrackingList
//            doUpdateGroup(groupData, this)
        }

        binding.btnBlockedTracking.setOnSingClickListener {
            binding.btnBlockedTracking.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
            binding.llNoProtect.visibility = if (binding.switchBlockTracking.isChecked) View.GONE else View.VISIBLE
            binding.llProtected.visibility = if (binding.switchBlockTracking.isChecked) View.VISIBLE else View.GONE
            binding.clSetupBlock.visibility = View.GONE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.btnBlockedTracking.alpha = 0.5f
            binding.viewBlock.visibility = View.INVISIBLE
            binding.btnSetupBlock.alpha = 1f
            binding.viewSetupBlock.visibility = View.VISIBLE
            binding.llNoProtect.visibility = View.GONE
            binding.llProtected.visibility = View.GONE
            binding.clSetupBlock.visibility = View.VISIBLE
        }
    }


    private fun handleProtected(isProtected: Boolean) {
        if (isProtected) {
            binding.llNoProtect.visibility = View.GONE
            binding.llProtected.visibility = View.VISIBLE
            binding.ivCheck.setImageResource(R.drawable.ic_checkmark_circle)
            binding.ivBlockTracking.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_tracking),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            doGetQueryLog(groupData)
        } else {
            binding.llNoProtect.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivBlockTracking.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_tracking),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }


    private fun doGetQueryLog(groupData: GroupData?) {
        if (!isBlockedTracking)
            return
        groupData?.groupid.let {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetQueryLogGroup(it, "native_tracking", "20", "")
            call.enqueue(BaseCallback(this, object : Callback<QueryLogResponse> {
                override fun onResponse(
                    call: Call<QueryLogResponse>,
                    response: Response<QueryLogResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let { resData ->
                            queryLogList.clear()
                            resData.data?.let { list -> queryLogList.addAll(list) }
                            adapter?.notifyDataSetChanged()
                        }

                    }
                    dismissProgress()
                }

                override fun onFailure(call: Call<QueryLogResponse>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                    dismissProgress()
                }
            }))
        }

    }

    private fun initSetupBlock() {
        getListBlockTrackingDefault()
        binding.itemBlockTrackingDevice.setData(listNativeTrackingDefault)
        binding.itemBlockTrackingDevice.setExpanded(false)
        binding.itemBlockTrackingDevice.disableExpanded()
    }

    private fun getListBlockTrackingDefault() {
        listNativeTrackingDefault.add(
            Subject(
                "Alexa",
                "alexa",
                R.drawable.ic_alexa,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Apple",
                "apple",
                R.drawable.ic_apple,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Huawei",
                "huawei",
                R.drawable.ic_huawei,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Roku",
                "roku",
                R.drawable.ic_roku,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Samsung",
                "samsung",
                R.drawable.ic_samsung,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Sonos",
                "sonos",
                R.drawable.ic_sonos,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Windows",
                "windows",
                R.drawable.ic_windows,
                false
            )
        )
        listNativeTrackingDefault.add(
            Subject(
                "Xiaomi",
                "xiaomi",
                R.drawable.ic_xiaomi,
                false
            )
        )
    }

    private fun doUpdateWhiteList(host: String) {
        showProgressDialog()
        val updateWhiteList = UpdateWhiteListRequest(groupData?.groupid, arrayOf(host))
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doUpdateWhiteList(updateWhiteList)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showToast("Thêm vào whitelist thành công")
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doDeleteLog(data: QueryLogData, position: Int) {
        showProgressDialog()
        val deleteLogRequest = DeleteLogRequest(groupData?.groupid, data.doc_id)
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doDeleteLog(deleteLogRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    adapter?.deleteItem(data, position)
                    adapter?.notifyDataSetChanged()
                    if (numBlockedTrackingAll > 0) {
                        numBlockedTrackingAll -= 1
                        binding.tvAmountEvery.text = numBlockedTrackingAll.toString()
                    }
                    if (numBlockedTracking > 0) {
                        numBlockedTracking -= 1
                        binding.tvAmountToday.text = numBlockedTracking.toString()
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    override fun onClickMore(data: QueryLogData, position: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val date = dateFormat.parse(data.time)
        val title = getString(R.string.da_chan, getTimeAgo(date.time))
        val name = data.question?.host
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            title,
            name,
            VisafeDialogBottomSheet.TYPE_EDIT_DELETE,
            "Bỏ chặn truy cập",
            R.drawable.ic_un_blocked,
            "Xoá cảnh báo"
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { _, action ->
            when (action) {
                Action.EDIT -> {
                    name?.let { doUpdateWhiteList(it) }
                }
                Action.DELETE -> {
                    doDeleteLog(data, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    override fun onUpdateSuccess(data: GroupData) {
        data.let {
            binding.tvDescription.text = it.name
            isBlockedTracking = groupData?.native_tracking?.isNotEmpty() == true
            binding.switchBlockTracking.isChecked = isBlockedTracking
            handleProtected(isBlockedTracking)
            //init data group
            binding.itemBlockTrackingDevice.setChecked(groupData?.native_tracking?.isNotEmpty() == true)
            nativeTrackingList.clear()
            it.native_tracking?.toMutableList()?.let { listBlockTracking -> nativeTrackingList.addAll(listBlockTracking) }
            nativeTrackingList.let {
                for (i in nativeTrackingList) {
                    for (j in 0 until listNativeTrackingDefault.size) {
                        if (i == listNativeTrackingDefault[j].value) {
                            listNativeTrackingDefault[j].isChecked = true
                        }
                    }
                }
                binding.itemBlockTrackingDevice.reloadData()
            }
        }
    }

}