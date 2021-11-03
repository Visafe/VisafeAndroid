package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class DeleteGroupRequest(
    @SerializedName("groupid")
    var groupId: String? = null,
    @SerializedName("fkUserId")
    var fkUserId: Int? = null
)