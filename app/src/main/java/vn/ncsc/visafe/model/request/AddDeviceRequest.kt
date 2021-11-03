package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class AddDeviceRequest(
    @SerializedName("deviceId")
    var deviceId: String? = null,
    @SerializedName("groupName")
    var groupName: String? = null,
    @SerializedName("groupId")
    var groupId: String? = null,
    @SerializedName("deviceName")
    var deviceName: String? = null,
    @SerializedName("macAddress")
    var macAddress: String? = null,
    @SerializedName("ipAddress")
    var ipAddress: String? = null,
    @SerializedName("deviceType")
    var deviceType: String? = null,
    @SerializedName("deviceOwner")
    var deviceOwner: String? = null,
    @SerializedName("deviceDetail")
    var deviceDetail: String? = null
)