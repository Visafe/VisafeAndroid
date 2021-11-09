package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class ActiveVipRequest(
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("deviceId")
    var deviceId: String? = null
)
