package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName

data class CheckDeviceInGroupResponse(
    @SerializedName("status_code")
    val statusCode: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
    @SerializedName("groupId")
    val groupId: String? = null,
    @SerializedName("groupName")
    val groupName: String? = null,
    @SerializedName("groupOwner")
    val groupOwner: String? = null,
    @SerializedName("numberDevice")
    val numberDevice: Int? = null
)