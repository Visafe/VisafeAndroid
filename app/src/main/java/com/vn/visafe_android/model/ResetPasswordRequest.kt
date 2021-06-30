package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("repeat_password")
    var repeatPassword: String? = null
)