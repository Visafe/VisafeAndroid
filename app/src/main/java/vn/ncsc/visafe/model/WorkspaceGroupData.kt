package vn.ncsc.visafe.model

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.ncsc.visafe.R

@Parcelize
data class WorkspaceGroupData(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("userOwner")
    var userOwner: Int? = null,
    @SerializedName("isOwner")
    var isOwner: Boolean? = null,
    @SerializedName("phishingEnabled")
    var phishingEnabled: Boolean? = null,
    @SerializedName("malwareEnabled")
    var malwareEnabled: Boolean? = null,
    @SerializedName("logEnabled")
    var logEnabled: Boolean? = null,
    @SerializedName("groupIds")
    var groupIds: List<String>? = null,
    @SerializedName("members")
    var members: List<String>? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    var isSelected: Boolean? = null
) : Parcelable

enum class OWNER(val title: Int, val textColor: Int, val backgroundColor: Int) {
    QUAN_TRI(R.string.quan_tri, Color.parseColor("#FFB31F"), R.color.color_1AFFB31F),
    THANH_VIEN(R.string.thanh_vien, Color.parseColor("#33B6FF"), R.color.color_1A33B6FF);

    companion object {
        fun fromIsOwner(isOwner: Boolean?) = if (isOwner == true) QUAN_TRI else THANH_VIEN
    }
}

enum class TYPE_WORKSPACES(
    val type: String,
    val nameWorkSpace: String,
    val content: String,
    val resDrawableIcon: Int,
    val resDrawableBgTop: Int
) {
    PERSONAL(
        "PERSONAL",
        "Con người",
        "Bảo vệ con người",
        R.drawable.ic_personal,
        R.drawable.bg_top_protect_family_group
    ),
    FAMILY(
        "FAMILY",
        "Gia đình & nhóm",
        "Bảo vệ gia đình & người thân trên môi trường mạng",
        R.drawable.ic_family,
        R.drawable.bg_top_protect_family_group
    ),
    ENTERPRISE(
        "ENTERPRISE",
        "Bảo vệ tổ chức",
        "Tất cả thành viên tham gia nhóm đều được ViSafe bảo vệ trên môi trường mạng",
        R.drawable.ic_business,
        R.drawable.bg_top_protect_enterprise_group
    ),
    SCHOOL(
        "SCHOOL",
        "Gia đình & nhóm",
        "Bảo vệ gia đình & người thân trên môi trường mạng", R.drawable.ic_education,
        R.drawable.bg_top_protect_family_group
    ),
    GOVERNMENT_ORGANIZATION(
        "GOVERNMENT_ORGANIZATION",
        "Gia đình & nhóm",
        "Bảo vệ gia đình & người thân trên môi trường mạng",
        R.drawable.ic_chinh_phu,
        R.drawable.bg_top_protect_family_group
    );

    companion object {
        private val mapType = values().associateBy(TYPE_WORKSPACES::type)
        fun fromIsTypeWorkSpaces(type: String?) = mapType[type]
    }
}
