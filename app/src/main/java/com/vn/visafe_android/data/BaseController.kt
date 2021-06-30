package com.vn.visafe_android.data

interface BaseController {
    fun onTimeOutSession()

    fun onError(baseResponse: BaseResponse)
}