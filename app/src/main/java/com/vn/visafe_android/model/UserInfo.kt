package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("UserID")
    var userID: Int? = null,
    @SerializedName("FullName")
    var fullName: String? = null,
    @SerializedName("Email")
    var email: String? = null,
    @SerializedName("Password")
    var Password: String? = null,
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("IsVerify")
    var isVerify: Boolean? = null,
    @SerializedName("IsActive")
    var isActive: Boolean? = null
)