package com.vn.visafe_android.model.response

import com.google.gson.annotations.SerializedName
import com.vn.visafe_android.data.BaseResponse

data class LoginResponse(
    @SerializedName("token")
    var token: String? = null
) : BaseResponse()
