package com.vn.visafe_android.ui.home

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentNotificationBinding
import com.vn.visafe_android.model.NotificationModel
import com.vn.visafe_android.model.request.NotificationRequest
import com.vn.visafe_android.model.response.NotificationResponse
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.adapter.NotificationAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationAdapter.OnSelectItemListener,
    XRecyclerView.LoadingListener {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private var mPage = 1
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationList: MutableList<NotificationModel> = mutableListOf()

    override fun layoutRes(): Int = R.layout.fragment_notification

    override fun initView() {
        notificationAdapter = NotificationAdapter(this)
        binding.rcvNotification.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvNotification.adapter = notificationAdapter
        binding.rcvNotification.setLoadingListener(this)
        binding.rcvNotification.setPullRefreshEnabled(true)
        mPage = 1
        doGetNotification(TypeLoad.FIRST_LOAD)
    }

    private fun doGetNotification(type: TypeLoad) {
        if (!(activity as MainActivity).isLogin())
            return
        if (type == TypeLoad.FIRST_LOAD) showProgressDialog()
        val client = NetworkClient()
        val call = context?.let { client.client(context = it).doGetNotification(mPage) }
        call?.enqueue(BaseCallback(this, object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rcvNotification.visibility = View.GONE
                    when (type) {
                        TypeLoad.FIRST_LOAD, TypeLoad.REFRESH -> {
                            response.body()?.notis?.let {
                                if (it.isNotEmpty()) {
                                    notificationList.clear()
                                    notificationList.addAll(it)
                                    notificationAdapter?.setData(notificationList)
                                    binding.tvNoData.visibility = View.GONE
                                    binding.rcvNotification.visibility = View.VISIBLE
                                }
                            }
                        }
                        TypeLoad.LOAD_MORE -> {
                            if (response.body()?.notis?.isNotEmpty() == true) {
                                notificationList.addAll(response.body()?.notis!!)
                                notificationAdapter?.setData(notificationList)
                            }
                            binding.tvNoData.visibility = View.GONE
                            binding.rcvNotification.visibility = View.VISIBLE
                        }
                    }
                }
                when (type) {
                    TypeLoad.FIRST_LOAD -> dismissProgress()
                    TypeLoad.REFRESH -> binding.rcvNotification.refreshComplete()
                    TypeLoad.LOAD_MORE -> binding.rcvNotification.loadMoreComplete()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                when (type) {
                    TypeLoad.FIRST_LOAD -> dismissProgress()
                    TypeLoad.REFRESH -> binding.rcvNotification.refreshComplete()
                    TypeLoad.LOAD_MORE -> binding.rcvNotification.loadMoreComplete()
                }
            }
        }))
    }

    override fun onSelectItem(item: NotificationModel) {
        doReadANotification(item.id)
    }

    private fun doReadANotification(id: Int?) {
        showProgressDialog()
        val client = NetworkClient()
        val call = context?.let { client.client(context = it).doReadANotification(NotificationRequest(id)) }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    override fun onRefresh() {
        mPage = 1
        doGetNotification(TypeLoad.REFRESH)
    }

    override fun onLoadMore() {
        mPage++
        doGetNotification(TypeLoad.LOAD_MORE)
    }

    enum class TypeLoad {
        FIRST_LOAD, REFRESH, LOAD_MORE
    }

}