package vn.ncsc.visafe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailBotnet(
    @SerializedName("cc_country")
    var cc_country: String? = null,
    @SerializedName("cc_ip")
    var cc_ip: String? = null,
    @SerializedName("cc_port")
    var cc_port: String? = null,
    @SerializedName("lastseen")
    var lastseen: String? = null,
    @SerializedName("mw_type")
    var mw_type: String? = null,
    @SerializedName("src_ip")
    var src_ip: String? = null
) : Parcelable