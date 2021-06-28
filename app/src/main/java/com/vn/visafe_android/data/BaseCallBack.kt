package com.vn.visafe_android.data

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseCallback<T>(private val baseController: BaseController, private val mCallback: Callback<T>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.code() == NetworkClient.CODE_TIMEOUT_SESSION) {
            baseController.onTimeOutSession()
        } else {
            mCallback.onResponse(call, response)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        mCallback.onFailure(call, t)
    }
}