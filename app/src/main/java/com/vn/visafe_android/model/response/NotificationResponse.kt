package com.vn.visafe_android.model.response

import com.google.gson.annotations.SerializedName
import com.vn.visafe_android.model.NotificationModel

data class NotificationResponse(
    @SerializedName("notis")
    var notis: List<NotificationModel>? = null,
    @SerializedName("count")
    var count: Int? = null
)