package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName

data class PayPackageResponse(
    @SerializedName("error_code")
    var error_code: Int? = null,
    @SerializedName("local_message")
    var local_message: String? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("payUrl")
    var payUrl: String? = null
)