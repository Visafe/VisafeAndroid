package vn.ncsc.visafe.ui.group.detail

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.model.GroupData

open class BaseSetupProtectActivity : BaseActivity() {
    fun doUpdateGroup(groupData: GroupData?, onUpdate: OnUpdateSuccess) {
        groupData?.let {
            if (!isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doUpdateGroup(it)
            call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        onUpdate.onUpdateSuccess(groupData)
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

    interface OnUpdateSuccess {
        fun onUpdateSuccess(groupData: GroupData)
    }
}