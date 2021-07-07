package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

data class UpdateNameWorkspaceRequest(
    @SerializedName("workspace_id")
    var workspaceId: String? = null,
    @SerializedName("workspace_name")
    var workspaceName: String? = null
)