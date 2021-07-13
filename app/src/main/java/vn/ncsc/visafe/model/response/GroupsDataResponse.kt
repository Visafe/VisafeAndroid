package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName
import vn.ncsc.visafe.model.GroupData

data class GroupsDataResponse(
    @SerializedName("clients")
    var clients: List<GroupData>? = null,
    @SerializedName("auto_clients")
    var auto_clients: String? = null,
    @SerializedName("supported_tags")
    var supported_tags: List<String>? = null
)