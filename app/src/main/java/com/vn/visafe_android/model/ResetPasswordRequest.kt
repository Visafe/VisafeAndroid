package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("repeat_password")
    var repeat_password: String? = null
)