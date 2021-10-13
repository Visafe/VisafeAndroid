package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class OutGroupRequest(
    @SerializedName("device_id")
    var deviceId: String? = null,
    @SerializedName("group_id")
    var groupId: String? = null
)
