package com.vn.visafe_android.data

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseCallback<T>(private val baseController: BaseController, private val mCallback: Callback<T>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        when {
            response.code() == NetworkClient.CODE_TIMEOUT_SESSION -> {
                baseController.onTimeOutSession()
            }
            isError(response.code()) -> {
                response.errorBody()?.let {
                    val jsonObject = JSONObject(it.string())
                    val statusCode = jsonObject.getString("status_code")
                    val msg = jsonObject.getString("msg")
                    val baseResponse = BaseResponse(statusCode.toInt(), msg)
                    baseController.onError(baseResponse)
                }

            }
            else -> {
                mCallback.onResponse(call, response)
            }
        }
    }

    private fun isError(errorCode: Int?): Boolean {
        return NetworkClient.ERROR_CODE.contains(errorCode)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        mCallback.onFailure(call, t)
    }
}