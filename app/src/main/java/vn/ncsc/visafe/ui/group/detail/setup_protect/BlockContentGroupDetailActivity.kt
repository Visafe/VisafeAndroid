package vn.ncsc.visafe.ui.group.detail.setup_protect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityBlockContentGroupDetailBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.QueryLogData
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

class BlockContentGroupDetailActivity : BaseSetupProtectActivity(), OnClickMoreItemQuery,
    BaseSetupProtectActivity.OnUpdateSuccess {

    companion object {
        const val NUM_BLOCKED_CONTENT = "NUM_BLOCKED_CONTENT"
        const val NUM_BLOCKED_CONTENT_ALL = "NUM_BLOCKED_CONTENT_ALL"
        const val DATA_GROUP_KEY = "DATA_GROUP_KEY"
    }

    lateinit var binding: ActivityBlockContentGroupDetailBinding
    private var groupData: GroupData? = null
    private var adapter: QueryLogAdapter? = null
    private var queryLogList: MutableList<QueryLogData> = mutableListOf()
    private var numBlockedContent: Int = 0
    private var numBlockedContentAll: Int = 0
    private var isBlockedContent: Boolean = false

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra(DATA_GROUP_KEY, groupData)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockContentGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(DATA_GROUP_KEY)
            numBlockedContent = it.getIntExtra(NUM_BLOCKED_CONTENT, 0)
            numBlockedContentAll = it.getIntExtra(NUM_BLOCKED_CONTENT_ALL, 0)
        }
        initView()
        initControl()
    }

    private fun initView() {
        binding.tvAmountEvery.text = numBlockedContentAll.toString()
        binding.tvAmountToday.text = numBlockedContent.toString()

        adapter = QueryLogAdapter(queryLogList, this)
        binding.rcvBlockContent.adapter = adapter
        adapter?.setOnClickListener(this)

        groupData?.let {
            isBlockedContent = groupData?.porn_enabled == true ||
                    groupData?.safesearch_enabled == true ||
                    groupData?.youtuberestrict_enabled == true ||
                    groupData?.gambling_enabled == true ||
                    groupData?.phishing_enabled == true
            handleProtected(isBlockedContent)
            binding.switchBlockContent.isChecked = isBlockedContent
            //init data group
            binding.itemBlockAdultContent.setChecked(groupData?.porn_enabled == true)
            binding.switchSafeSearch.isChecked = groupData?.safesearch_enabled == true
            binding.switchYoutuberEstrict.isChecked = groupData?.youtuberestrict_enabled == true
            binding.itemBlockGambling.setChecked(groupData?.gambling_enabled == true)
            binding.itemBlockFakeNews.setChecked(groupData?.phishing_enabled == true)
        }
    }

    private val mListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        groupData?.porn_enabled = isChecked
        groupData?.safesearch_enabled = isChecked
        groupData?.youtuberestrict_enabled = isChecked
        groupData?.gambling_enabled = isChecked
        groupData?.phishing_enabled = isChecked
        doUpdateGroup(groupData, this@BlockContentGroupDetailActivity)
    }

    private fun initControl() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                val intent = Intent()
                intent.putExtra(DATA_GROUP_KEY, groupData)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        binding.switchBlockContent.setOnCheckedChangeListener(mListener)

        binding.itemBlockAdultContent.setOnSwitchChangeListener { isChecked ->
            groupData?.porn_enabled = isChecked
            binding.switchBlockContent.setOnCheckedChangeListener(null)
            doUpdateGroup(groupData, this)
        }

        binding.switchSafeSearch.setOnCheckedChangeListener { _, isChecked ->
            groupData?.safesearch_enabled = isChecked
            binding.switchBlockContent.setOnCheckedChangeListener(null)
            doUpdateGroup(groupData, this)
        }

        binding.switchYoutuberEstrict.setOnCheckedChangeListener { _, isChecked ->
            groupData?.youtuberestrict_enabled = isChecked
            binding.switchBlockContent.setOnCheckedChangeListener(null)
            doUpdateGroup(groupData, this)
        }

        binding.itemBlockGambling.setOnSwitchChangeListener { isChecked ->
            groupData?.gambling_enabled = isChecked
            binding.switchBlockContent.setOnCheckedChangeListener(null)
            doUpdateGroup(groupData, this)
        }

        binding.itemBlockFakeNews.setOnSwitchChangeListener { isChecked ->
            groupData?.phishing_enabled = isChecked
            binding.switchBlockContent.setOnCheckedChangeListener(null)
            doUpdateGroup(groupData, this)
        }

        binding.btnBlockedContent.setOnSingClickListener {
            binding.btnBlockedContent.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
            binding.llNoProtect.visibility = if (binding.switchBlockContent.isChecked) View.GONE else View.VISIBLE
            binding.llProtected.visibility = if (binding.switchBlockContent.isChecked) View.VISIBLE else View.GONE
            binding.clSetupBlock.visibility = View.GONE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.btnBlockedContent.alpha = 0.5f
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
            binding.ivBlockContent.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_content),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            doGetQueryLog(groupData)
        } else {
            binding.llNoProtect.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivBlockContent.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_content),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }


    private fun doGetQueryLog(groupData: GroupData?) {
        if (!isBlockedContent)
            return
        groupData?.groupid.let {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetQueryLogGroup(it, "content_blocked", "20", "")
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
                    if (numBlockedContentAll > 0) {
                        numBlockedContentAll -= 1
                        binding.tvAmountEvery.text = numBlockedContentAll.toString()
                    }
                    if (numBlockedContent > 0) {
                        numBlockedContent -= 1
                        binding.tvAmountToday.text = numBlockedContent.toString()
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
            isBlockedContent = groupData?.porn_enabled == true ||
                    groupData?.safesearch_enabled == true ||
                    groupData?.youtuberestrict_enabled == true ||
                    groupData?.gambling_enabled == true ||
                    groupData?.phishing_enabled == true
            binding.switchBlockContent.isChecked = isBlockedContent
            binding.switchBlockContent.setOnCheckedChangeListener(mListener)
            handleProtected(isBlockedContent)
            binding.itemBlockAdultContent.setChecked(groupData?.porn_enabled == true)
            binding.switchSafeSearch.isChecked = groupData?.safesearch_enabled == true
            binding.switchYoutuberEstrict.isChecked = groupData?.youtuberestrict_enabled == true
            binding.itemBlockGambling.setChecked(groupData?.gambling_enabled == true)
            binding.itemBlockFakeNews.setChecked(groupData?.phishing_enabled == true)
            groupData = data
        }
    }

}