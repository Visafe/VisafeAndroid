package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

data class CreateWorkSpaceRequest(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("phishingEnabled")
    var phishingEnabled: Boolean? = null,
    @SerializedName("malwareEnabled")
    var malwareEnabled: Boolean? = null,
    @SerializedName("logEnabled")
    var logEnabled: Boolean? = null
)