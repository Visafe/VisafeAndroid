package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName
import vn.ncsc.visafe.model.UsersGroupInfo

data class AddMemberInGroupResponse(
    @SerializedName("invited")
    var invited: MutableList<UsersGroupInfo>? = null,
    @SerializedName("invited_sent_mail")
    var invited_sent_mail: MutableList<String>? = null,
    @SerializedName("phone_out_sys")
    var phone_out_sys: MutableList<String>? = null
)