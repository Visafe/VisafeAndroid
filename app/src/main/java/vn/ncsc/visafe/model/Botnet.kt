package vn.ncsc.visafe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Botnet(
    @SerializedName("src_ip")
    var src_ip: String? = null,
    @SerializedName("isp")
    var isp: String? = null,
    @SerializedName("os")
    var os: String? = null,
    @SerializedName("browsers")
    var browsers: String? = null,
    @SerializedName("anonymizer")
    var anonymizer: Boolean? = null,
    @SerializedName("blacklist")
    var blacklist: Boolean? = null,
    @SerializedName("lastseen")
    var lastseen: String? = null,
    @SerializedName("isBotnet")
    var isBotnet: String? = null,
    @SerializedName("detail")
    var detail: List<DetailBotnet>? = null
) : Parcelable

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