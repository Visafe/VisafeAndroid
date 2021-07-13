package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("id")
    var id: Int? = null
)