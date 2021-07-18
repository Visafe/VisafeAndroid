package vn.ncsc.visafe.utils

import vn.ncsc.visafe.R

enum class SetupConfig(
    val image: Int,
    val title: String,
    val content: String,
    var selected: Boolean
) {
    CHONG_LUA_DAO_MANG(
        R.drawable.ic_anonymous,
        "Chống lừa đảo mạng",
        "Ngăn chặn & cảnh báo khi người dùng truy cập vào các trang web, ứng dụng có dấu hiệu lừa đảo",
        false
    ),
    CHONG_MA_DOC_TAN_CONG_MANG(
        R.drawable.ic_bug,
        "Chống mã độc & tấn công mạng",
        "Chống tấn công mã độc, phần mềm tống tiền,tấn công mạng,...",
        false
    ),
    LUU_LICH_SU_TRUY_CAP(
        R.drawable.ic_history_bug,
        "Thống kê truy cập",
        "Thống kê các nguy hại đã xử lý và \n" +
                "thời gian sử dụng thiết bị",
        false
    );

    companion object {
        private val mapType = values().associateBy(SetupConfig::name)
        fun fromIsOwner(data: SetupConfig) = mapType[data.name]
    }
}