package vn.ncsc.visafe.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("full_name")
    var username: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("phone_number")
    var phoneNumber: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("repeat_password")
    var repeatPassword: String? = null
)