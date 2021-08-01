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
import vn.ncsc.visafe.databinding.ActivityBlockAccessGroupDetailBinding
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

class BlockAccessGroupDetailActivity : BaseSetupProtectActivity(), OnClickMoreItemQuery,
    BaseSetupProtectActivity.OnUpdateSuccess {

    companion object {
        const val NUM_BLOCKED_ACCESS = "NUM_BLOCKED_ACCESS"
        const val NUM_BLOCKED_ACCESS_ALL = "NUM_BLOCKED_ACCESS_ALL"
        const val DATA_GROUP_KEY = "DATA_GROUP_KEY"
    }

    lateinit var binding: ActivityBlockAccessGroupDetailBinding
    private var groupData: GroupData? = null
    private var adapter: QueryLogAdapter? = null
    private var queryLogList: MutableList<QueryLogData> = mutableListOf()
    private var numBlockedAccess: Int = 0
    private var numBlockedAccessAll: Int = 0
    private var isBlockedAccess: Boolean = false
    private var blockServiceList: MutableList<String> = mutableListOf()
    private var listBlockServiceDefault: ArrayList<Subject> = arrayListOf()

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockAccessGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(DATA_GROUP_KEY)
            numBlockedAccess = it.getIntExtra(NUM_BLOCKED_ACCESS, 0)
            numBlockedAccessAll = it.getIntExtra(NUM_BLOCKED_ACCESS_ALL, 0)
        }
        initView()
        initControl()
    }

    private fun initView() {
        initSetupBlock()
        binding.tvAmountEvery.text = numBlockedAccessAll.toString()
        binding.tvAmountToday.text = numBlockedAccess.toString()

        adapter = QueryLogAdapter(queryLogList, this)
        binding.rcvBlockAccess.adapter = adapter
        adapter?.setOnClickListener(this)

        groupData?.let {
            binding.tvDescription.text = it.name
            isBlockedAccess = groupData?.blocked_services?.isNotEmpty() == true
            binding.switchBlockAccess.isChecked = isBlockedAccess
            handleProtected(isBlockedAccess)
            //init data group
            binding.itemBlockAccess.setChecked(groupData?.blocked_services?.isNotEmpty() == true)
            blockServiceList.clear()
            it.blocked_services?.toMutableList()?.let { listBlockService -> blockServiceList.addAll(listBlockService) }
            for (i in blockServiceList) {
                for (j in 0 until listBlockServiceDefault.size) {
                    if (i == listBlockServiceDefault[j].value) {
                        listBlockServiceDefault[j].isChecked = true
                    }
                }
            }
            binding.itemBlockAccess.reloadData()
        }
    }

    private fun initControl() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })

        binding.switchBlockAccess.setOnCheckedChangeListener { _, isChecked ->
            binding.itemBlockAccess.setChecked(isChecked)
            binding.switchBlockAccess.isChecked = isChecked
            groupData?.blocked_services = if (isChecked) listOf(
                "facebook",
                "zalo",
                "tiktok",
                "instagram",
                "tinder",
                "twitter",
                "netflix",
                "reddit",
                "9gag",
                "discord"
            ) else listOf()
            handleProtected(isChecked)
            doUpdateGroup(groupData, this)
        }

        binding.itemBlockAccess.setOnSwitchChangeListener { isChecked ->
            groupData?.blocked_services = if (isChecked) listOf(
                "facebook",
                "zalo",
                "tiktok",
                "instagram",
                "tinder",
                "twitter",
                "netflix",
                "reddit",
                "9gag",
                "discord"
            ) else listOf()
            doUpdateGroup(groupData, this)
        }
        binding.itemBlockAccess.setOnSwitchItemChangeListener { isChecked, position ->
            Log.e("initControl: ", " " + position + " " + isChecked + " " + blockServiceList.size)
//            if (isChecked) {
//                nativeTrackingList.add(position, listNativeTrackingDefault[position].value)
//            } else {
//                nativeTrackingList.removeAt(position)
//            }
//            groupData?.native_tracking = nativeTrackingList
//            doUpdateGroup(groupData, this)
        }

        binding.btnBlockedAccess.setOnSingClickListener {
            binding.btnBlockedAccess.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
            binding.llNoProtect.visibility = if (binding.switchBlockAccess.isChecked) View.GONE else View.VISIBLE
            binding.llProtected.visibility = if (binding.switchBlockAccess.isChecked) View.VISIBLE else View.GONE
            binding.clSetupBlock.visibility = View.GONE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.btnBlockedAccess.alpha = 0.5f
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
            binding.ivBlockAccess.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_access),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            doGetQueryLog(groupData)
        } else {
            binding.llNoProtect.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivBlockAccess.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_access),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }


    private fun doGetQueryLog(groupData: GroupData?) {
        if (!isBlockedAccess)
            return
        groupData?.groupid.let {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetQueryLogGroup(it, "access_blocked", "20", "")
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
        getListBlockAccessDefault()
        binding.itemBlockAccess.setData(listBlockServiceDefault)
        binding.itemBlockAccess.setExpanded(false)
        binding.itemBlockAccess.disableExpanded()
    }

    private fun getListBlockAccessDefault() {
        listBlockServiceDefault.add(
            Subject(
                "Facebook",
                "facebook",
                R.drawable.ic_facebook,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Zalo",
                "zalo",
                R.drawable.ic_zalo,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Tiktok",
                "tiktok",
                R.drawable.ic_tiktok,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Instagram",
                "instagram",
                R.drawable.ic_instagram,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Tinder",
                "tinder",
                R.drawable.ic_tinder,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Twitter",
                "twitter",
                R.drawable.ic_twitter,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Netflix",
                "netflix",
                R.drawable.ic_netflix,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Reddit",
                "reddit",
                R.drawable.ic_reddit,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "9gag",
                "9gag",
                R.drawable.ic_9gag,
                false
            )
        )
        listBlockServiceDefault.add(
            Subject(
                "Discord",
                "discord",
                R.drawable.ic_discord,
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
                    if (numBlockedAccessAll > 0) {
                        numBlockedAccessAll -= 1
                        binding.tvAmountEvery.text = numBlockedAccessAll.toString()
                    }
                    if (numBlockedAccess > 0) {
                        numBlockedAccess -= 1
                        binding.tvAmountToday.text = numBlockedAccess.toString()
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
            isBlockedAccess = groupData?.blocked_services?.isNotEmpty() == true
            binding.switchBlockAccess.isChecked = isBlockedAccess
            handleProtected(isBlockedAccess)
            //init data group
            binding.itemBlockAccess.setChecked(groupData?.blocked_services?.isNotEmpty() == true)
            blockServiceList.clear()
            it.blocked_services?.toMutableList()?.let { listBlockService -> blockServiceList.addAll(listBlockService) }
            blockServiceList.let {
                for (i in blockServiceList) {
                    for (j in 0 until listBlockServiceDefault.size) {
                        if (i == listBlockServiceDefault[j].value) {
                            listBlockServiceDefault[j].isChecked = true
                        }
                    }
                }
                binding.itemBlockAccess.reloadData()
            }
        }
    }

}