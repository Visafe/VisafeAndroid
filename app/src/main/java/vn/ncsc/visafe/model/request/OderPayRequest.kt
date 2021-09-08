package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class OderPayRequest(
    @SerializedName("package_price_time_id")
    var package_price_time_id: Int? = null,
    @SerializedName("device_id")
    var device_id: String? = null
)