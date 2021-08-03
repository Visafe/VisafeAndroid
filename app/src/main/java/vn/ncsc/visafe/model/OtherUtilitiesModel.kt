package vn.ncsc.visafe.model

data class OtherUtilitiesModel(
    val title: String? = null,
    val value: String? = null,
    val resIcon: Int = 0,
    val type: TypeUtilities
)

enum class TypeUtilities {
    TIN_TUC_CANH_BAO,
    GUI_BAO_CAO,
    KIEM_TRA_WIFI,
    KIEM_TRA_DO_LOT_TK,
    KIEM_TRA_WEB_LUA_DAO,
    NHAN_DIEN_MA_DOC_TONG_TIEN
}