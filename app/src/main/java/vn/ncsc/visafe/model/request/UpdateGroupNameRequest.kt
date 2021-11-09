package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class UpdateGroupNameRequest(
    @SerializedName("group_id")
    var groupId: String? = null,
    @SerializedName("group_name")
    var groupName: String? = null
)