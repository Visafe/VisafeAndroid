package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class UpdateWorkspaceRequest(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("userOwner")
    var userOwner: Int? = null,
    @SerializedName("isOwner")
    var isOwner: Boolean? = null,
    @SerializedName("phishingEnabled")
    var phishingEnabled: Boolean? = null,
    @SerializedName("malwareEnabled")
    var malwareEnabled: Boolean? = null,
    @SerializedName("logEnabled")
    var logEnabled: Boolean? = null,
    @SerializedName("groupIds")
    var groupIds: List<String>? = null,
    @SerializedName("members")
    var members: List<String>? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null
)