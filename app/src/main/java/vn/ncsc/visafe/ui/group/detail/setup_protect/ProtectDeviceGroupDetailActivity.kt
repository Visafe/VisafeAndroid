package vn.ncsc.visafe.ui.group.detail.setup_protect

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.text.HtmlCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityProtectDeviceGroupDetailBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.QueryLogData
import vn.ncsc.visafe.model.request.DeleteLogRequest
import vn.ncsc.visafe.model.request.UpdateWhiteListRequest
import vn.ncsc.visafe.model.response.QueryLogResponse
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.dialog.VisafeDialogBottomSheet
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.getTimeAgo
import java.text.SimpleDateFormat
import java.util.*

class ProtectDeviceGroupDetailActivity : BaseActivity() {

    lateinit var binding: ActivityProtectDeviceGroupDetailBinding
    private var groupData: GroupData? = null
    private var adapter: QueryLogAdapter? = null
    private var queryLogList: MutableList<QueryLogData> = mutableListOf()
    private var numDangerousDomain: Int = 0
    private var numDangerousDomainAll: Int = 0

    companion object {
        const val NUM_DANGEROUS_DOMAIN = "NUM_DANGEROUS_DOMAIN"
        const val NUM_DANGEROUS_DOMAIN_ALL = "NUM_DANGEROUS_DOMAIN_ALL"
        const val DATA_GROUP_KEY = "DATA_GROUP_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProtectDeviceGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupData = it.getParcelableExtra(DATA_GROUP_KEY)
            numDangerousDomain = it.getIntExtra(NUM_DANGEROUS_DOMAIN, 0)
            numDangerousDomainAll = it.getIntExtra(NUM_DANGEROUS_DOMAIN_ALL, 0)
        }
        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        setResult(RESULT_OK)
        finish()
    }

    private fun initView() {
        binding.tvTitle.text = HtmlCompat.fromHtml(
            getString(R.string.title_protect_device_group_detail),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvAmountEvery.text = numDangerousDomainAll.toString()
        binding.tvAmountToday.text = numDangerousDomain.toString()
        groupData?.let {
            binding.tvDescription.text = it.name
            doGetQueryLog(it)
        }
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
//                setResult(RESULT_OK)
                finish()
            }
        })

        adapter = QueryLogAdapter(queryLogList, this)
        binding.rcvSetupProtected.adapter = adapter
        adapter?.setOnClickListener(object : OnClickMoreItemQuery {
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

        })
    }

    private fun doGetQueryLog(groupData: GroupData?) {
        groupData?.groupid.let {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetQueryLogGroup(it, "dangerous_domain", "20", "")
            call.enqueue(BaseCallback(this, object : Callback<QueryLogResponse> {
                override fun onResponse(
                    call: Call<QueryLogResponse>,
                    response: Response<QueryLogResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let { resData ->
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
                    if (numDangerousDomain > 0) {
                        numDangerousDomain -= 1
                        binding.tvAmountEvery.text = numDangerousDomainAll.toString()
                    }
                    if (numDangerousDomainAll > 0) {
                        numDangerousDomainAll -= 1
                        binding.tvAmountToday.text = numDangerousDomain.toString()
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
                    if (numDangerousDomain > 0) {
                        numDangerousDomain -= 1
                        binding.tvAmountEvery.text = numDangerousDomainAll.toString()
                    }
                    if (numDangerousDomainAll > 0) {
                        numDangerousDomainAll -= 1
                        binding.tvAmountToday.text = numDangerousDomain.toString()
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

}