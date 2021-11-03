package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class RemoveDeviceRequest(
    @SerializedName("deviceId")
    var deviceId: String? = null,
    @SerializedName("groupId")
    var groupId: String? = null,
    @SerializedName("deviceMonitorID")
    var deviceMonitorID: Int? = null
)