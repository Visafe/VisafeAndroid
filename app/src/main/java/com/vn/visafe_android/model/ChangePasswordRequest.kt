package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("currentPassword")
    var currentPassword: String? = null,
    @SerializedName("newPassword")
    var newPassword: String? = null,
    @SerializedName("repeatPassword")
    var repeatPassword: String? = null
)
