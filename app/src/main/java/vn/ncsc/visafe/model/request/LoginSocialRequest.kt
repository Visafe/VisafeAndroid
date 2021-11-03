package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class LoginSocialRequest(
    @SerializedName("token")
    var token: String? = null
)
