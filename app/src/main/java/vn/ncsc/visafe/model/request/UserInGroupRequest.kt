package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class UserInGroupRequest(
    @SerializedName("userId")
    var userId: Int? = null,
    @SerializedName("groupId")
    var groupId: String? = null,
    @SerializedName("usernames")
    var usernames: Array<String>? = null
)
