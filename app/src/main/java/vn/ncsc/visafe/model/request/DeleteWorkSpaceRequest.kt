package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class DeleteWorkSpaceRequest(
    @SerializedName("workspaceId")
    var workspaceId: String? = null
)