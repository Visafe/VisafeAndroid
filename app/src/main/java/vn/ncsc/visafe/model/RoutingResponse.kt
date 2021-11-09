package vn.ncsc.visafe.model

import com.google.gson.annotations.SerializedName

class RoutingResponse {
    @SerializedName("hostname")
    var hostname: String? = null

    @SerializedName("ip")
    var ip: String? = null
}