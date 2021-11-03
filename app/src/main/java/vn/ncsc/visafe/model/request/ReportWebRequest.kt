package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class ReportWebRequest(
    @SerializedName("url")
    var url: String? = null
)