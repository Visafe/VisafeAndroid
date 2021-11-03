package vn.ncsc.visafe.ui.group.detail.setup_protect

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_upgrade.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityBlockAdsGroupDetailBinding
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

class BlockAdsGroupDetailActivity : BaseSetupProtectActivity(), OnClickMoreItemQuery, BaseSetupProtectActivity.OnUpdateSuccess {

    companion object {
        const val NUM_ADS_BLOCKED = "NUM_DANGEROUS_DOMAIN"
        const val NUM_ADS_BLOCKED_ALL = "NUM_DANGEROUS_DOMAIN_ALL"
        const val DATA_GROUP_KEY = "DATA_GROUP_KEY"
    }

    lateinit var binding: ActivityBlockAdsGroupDetailBinding
    private var groupData: GroupData? = null
    private var adapter: QueryLogAdapter? = null
    private var queryLogList: MutableList<QueryLogData> = mutableListOf()
    private var numAdsBlocked: Int = 0
    private var numAdsBlockedAll: Int = 0
    private var isAdsBlocked: Boolean = false
    private var appAdsList: MutableList<String> = mutableListOf()
    private var listAppAdsDefault: ArrayList<Subject> = arrayListOf()

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockAdsGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(ProtectDeviceGroupDetailActivity.DATA_GROUP_KEY)
            numAdsBlocked = it.getIntExtra(NUM_ADS_BLOCKED, 0)
            numAdsBlockedAll = it.getIntExtra(NUM_ADS_BLOCKED_ALL, 0)
        }
        initView()
        initControl()
    }

    private fun initView() {
        initSetupBlock()
        binding.tvAmountEvery.text = numAdsBlockedAll.toString()
        binding.tvAmountToday.text = numAdsBlocked.toString()

        adapter = QueryLogAdapter(queryLogList, this)
        binding.rcvAdsBlocked.adapter = adapter
        adapter?.setOnClickListener(this)

        groupData?.let {
            isAdsBlocked = (groupData?.adblock_enabled == true ||
                    groupData?.game_ads_enabled == true ||
                    groupData?.app_ads?.isNotEmpty() == true)
            binding.switchBlockAds.isChecked = isAdsBlocked
            handleProtected(isAdsBlocked)
            //init data group
            binding.itemBlockAdsWeb.setChecked(groupData?.adblock_enabled == true)
            binding.itemBlockAdsGame.setChecked(groupData?.game_ads_enabled == true)
            binding.itemBlockAdsApp.setChecked(groupData?.app_ads?.isNotEmpty() == true)
            appAdsList.clear()
            it.app_ads?.toMutableList()?.let { listAppAds -> appAdsList.addAll(listAppAds) }
            for (i in appAdsList) {
                for (j in 0 until listAppAdsDefault.size) {
                    if (i == listAppAdsDefault[j].value) {
                        listAppAdsDefault[j].isChecked = true
                    }
                }
            }
            binding.itemBlockAdsApp.reloadData()
        }
    }

    private fun initControl() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                setResult(RESULT_OK)
                finish()
            }
        })
        binding.btnReset.setOnSingClickListener {
            binding.switchBlockAds.isChecked = false
        }

        binding.switchBlockAds.setOnCheckedChangeListener { _, isChecked ->
            binding.itemBlockAdsWeb.setChecked(isChecked)
            binding.itemBlockAdsGame.setChecked(isChecked)
            binding.itemBlockAdsApp.setChecked(isChecked)
            binding.switchBlockAds.isChecked = isChecked
            groupData?.adblock_enabled = isChecked
            groupData?.game_ads_enabled = isChecked
            groupData?.app_ads = if (isChecked) listOf(
                "instagram",
                "youtube",
                "spotify",
                "facebook"
            ) else listOf()
            handleProtected(isChecked)
            doUpdateGroup(groupData, this)
        }

        binding.itemBlockAdsWeb.setOnSwitchChangeListener { isChecked ->
            groupData?.adblock_enabled = isChecked
            doUpdateGroup(groupData, this)
        }
        binding.itemBlockAdsGame.setOnSwitchChangeListener { isChecked ->
            groupData?.game_ads_enabled = isChecked
            doUpdateGroup(groupData, this)
        }
        binding.itemBlockAdsApp.setOnSwitchChangeListener { isChecked ->
            groupData?.app_ads = if (isChecked) listOf(
                "instagram",
                "youtube",
                "spotify",
                "facebook"
            ) else listOf()
            doUpdateGroup(groupData, this)
        }
        binding.itemBlockAdsApp.setOnSwitchItemChangeListener { buttonView, isChecked, position ->
            if (buttonView.isPressed) {
                Log.e("switchWidget: ", "click")
            } else {
                Log.e("switchWidget: ", "setChecked " + listAppAdsDefault[position])
                //triggered due to programmatic assignment using 'setChecked()' method.
            }
            Log.e("initControl: ", " " + position + " " + listAppAdsDefault.size)
//            if (isChecked) {
//                appAdsList.add(position, listAppAdsDefault[position].value)
//            } else {
//                appAdsList.removeAt(position)
//            }
//            groupData?.app_ads = appAdsList
//            doUpdateGroup(groupData, this)
        }

        binding.btnBlockedAds.setOnSingClickListener {
            binding.btnBlockedAds.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
            binding.llNoProtect.visibility = if (binding.switchBlockAds.isChecked) View.GONE else View.VISIBLE
            binding.llProtected.visibility = if (binding.switchBlockAds.isChecked) View.VISIBLE else View.GONE
            binding.clSetupBlock.visibility = View.GONE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.btnBlockedAds.alpha = 0.5f
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
            binding.ivBlockAds.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_ads),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            doGetQueryLog(groupData)
            if (binding.viewSetupBlock.isVisible) {
                binding.viewSetupBlock.visibility = View.VISIBLE
                binding.llProtected.visibility = View.GONE
            } else {
                binding.viewSetupBlock.visibility = View.GONE
                binding.llProtected.visibility = View.VISIBLE
            }
        } else {
            binding.llNoProtect.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivBlockAds.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_ads),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }


    private fun doGetQueryLog(groupData: GroupData?) {
        if (!isAdsBlocked)
            return
        groupData?.groupid.let {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetQueryLogGroup(it, "ads_blocked", "20", "")
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
        getListAppAdsDefault()
        binding.itemBlockAdsApp.setData(listAppAdsDefault)
        binding.itemBlockAdsApp.setExpanded(false)
        binding.itemBlockAdsApp.disableExpanded()
    }

    private fun getListAppAdsDefault() {
        listAppAdsDefault.add(
            Subject(
                "Instagram",
                "instagram",
                R.drawable.ic_instagram,
                false
            )
        )
        listAppAdsDefault.add(
            Subject(
                "Youtube",
                "youtube",
                R.drawable.ic_youtube,
                false
            )
        )
        listAppAdsDefault.add(
            Subject(
                "Spotify",
                "spotify",
                R.drawable.ic_spotify,
                false
            )
        )
        listAppAdsDefault.add(
            Subject(
                "Facebook",
                "facebook",
                R.drawable.ic_facebook,
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
                    if (numAdsBlockedAll > 0) {
                        numAdsBlockedAll -= 1
                        binding.tvAmountEvery.text = numAdsBlockedAll.toString()
                    }
                    if (numAdsBlocked > 0) {
                        numAdsBlocked -= 1
                        binding.tvAmountToday.text = numAdsBlocked.toString()
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
            isAdsBlocked = (groupData?.adblock_enabled == true ||
                    groupData?.game_ads_enabled == true ||
                    groupData?.app_ads?.isNotEmpty() == true)
            binding.switchBlockAds.isChecked = isAdsBlocked
            handleProtected(isAdsBlocked)
            //init data group
            binding.itemBlockAdsWeb.setChecked(groupData?.adblock_enabled == true)
            binding.itemBlockAdsGame.setChecked(groupData?.game_ads_enabled == true)
            binding.itemBlockAdsApp.setChecked(groupData?.app_ads?.isNotEmpty() == true)
            appAdsList.clear()
            it.app_ads?.toMutableList()?.let { listAppAds -> appAdsList.addAll(listAppAds) }
            appAdsList.let {
                for (i in appAdsList) {
                    for (j in 0 until listAppAdsDefault.size) {
                        if (i == listAppAdsDefault[j].value) {
                            listAppAdsDefault[j].isChecked = true
                        }
                    }
                }
                binding.itemBlockAdsApp.reloadData()
            }
        }
    }

}