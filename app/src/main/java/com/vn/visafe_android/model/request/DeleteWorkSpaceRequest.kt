package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

data class DeleteWorkSpaceRequest(
    @SerializedName("workspaceId")
    var workspaceId: String? = null
)