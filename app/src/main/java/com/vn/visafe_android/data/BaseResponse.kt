package com.vn.visafe_android.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class BaseResponse(
    @SerializedName("status_code")
    open var status_code: Int? = null,
    @SerializedName("msg")
    open var msg: String? = null
)