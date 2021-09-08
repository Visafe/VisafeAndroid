package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class SendTokenRequest(
    @SerializedName("token")
    var token: String? = null,
    @SerializedName("deviceId")
    var deviceId: String? = null
)