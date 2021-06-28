package com.vn.visafe_android.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("passwordagain")
    var passwordagain: String? = null
)