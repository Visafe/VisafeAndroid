package vn.ncsc.visafe.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.ncsc.visafe.model.DetailBotnet

@Parcelize
data class BotnetResponse(
    @SerializedName("anonymizer")
    var anonymizer: Boolean? = false,
    @SerializedName("asn")
    var asn: String? = null,
    @SerializedName("blacklist")
    var blacklist: Boolean? = false,
    @SerializedName("browsers")
    var browsers: String? = null,
    @SerializedName("cc_ip")
    var cc_ip: String? = null,
    @SerializedName("detail")
    var detail: MutableList<DetailBotnet>? = null,
    @SerializedName("isBotnet")
    var isBotnet: String? = null,
    @SerializedName("isp")
    var isp: String? = null,
    @SerializedName("lastseen")
    var lastseen: String? = null,
    @SerializedName("malware_type")
    var malware_type: String? = null,
    @SerializedName("os")
    var os: String? = null,
    @SerializedName("src_ip")
    var src_ip: String? = null
) : Parcelable