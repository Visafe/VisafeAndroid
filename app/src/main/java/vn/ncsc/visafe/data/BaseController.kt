package vn.ncsc.visafe.data

interface BaseController {
    fun onTimeOutSession()

    fun onError(baseResponse: BaseResponse)
}