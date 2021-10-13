package vn.ncsc.visafe.data

import com.google.gson.Gson
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
                    if (it is JSONObject) {
                        val jsonObject = JSONObject(it.string())
                        val statusCode = jsonObject.getString("status_code")
                        val msg = jsonObject.getString("msg")
                        val localMsg = jsonObject.getString("local_msg")
                        val baseResponse = BaseResponse(statusCode.toInt(), msg, localMsg)
                        baseController.onError(baseResponse)
                    } else {
                        val buffer = it.source().buffer.readByteArray()
                        val dataString = buffer.decodeToString()
                        val baseResponse = Gson().fromJson(dataString, BaseResponse::class.java)
                        baseController.onError(baseResponse)
                    }
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