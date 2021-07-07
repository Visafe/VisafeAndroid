package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

open class ActiveAccountRequest(
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("phone_number")
    var phoneNumber: String? = null
)