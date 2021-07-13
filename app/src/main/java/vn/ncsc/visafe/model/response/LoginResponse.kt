package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName
import vn.ncsc.visafe.data.BaseResponse

data class LoginResponse(
    @SerializedName("token")
    var token: String? = null
) : BaseResponse()
