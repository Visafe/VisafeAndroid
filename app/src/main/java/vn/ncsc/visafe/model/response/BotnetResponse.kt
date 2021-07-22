package vn.ncsc.visafe.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.ncsc.visafe.model.Botnet

@Parcelize
data class BotnetResponse(
    @SerializedName("status")
    open var status: Int? = null,
    @SerializedName("msg")
    open var msg: Botnet? = null
) : Parcelable