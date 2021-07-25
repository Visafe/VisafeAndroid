package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class DeleteLogRequest(
    @SerializedName("group_id")
    var group_id: String? = null,
    @SerializedName("doc_id")
    var doc_id: String? = null
)