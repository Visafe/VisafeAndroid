package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class UpdateWhiteListRequest(
    @SerializedName("group_id")
    var group_id: String? = null,
    @SerializedName("white_list")
    var white_list: Array<String>? = null
)