package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

open class ActiveAccountRequest(
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("phone_number")
    var phoneNumber: String? = null
)