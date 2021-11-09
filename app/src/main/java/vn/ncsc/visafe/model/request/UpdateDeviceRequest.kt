package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class UpdateDeviceRequest(
    @SerializedName("deviceMonitorId") //id của người chủ group
    var deviceMonitorId: Int? = null,
    @SerializedName("deviceId")
    var deviceId: String? = null,
    @SerializedName("groupId")
    var groupId: String? = null,
    @SerializedName("deviceOwner")//tên chủ device cần thay đổi
    var deviceOwner: String? = null
)