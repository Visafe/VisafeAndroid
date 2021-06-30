package com.vn.visafe_android.utils

import com.vn.visafe_android.R

enum class SetupConfig(
    val image: Int,
    val title: String,
    val content: String,
    var selected: Boolean
) {
    CHONG_LUA_DAO_MANG(R.drawable.ic_anonymous, "Chống lừa đảo mạng", "Ngăn chặn & cảnh báo khi người dùng truy cập vào các trang web ứng dụng có dấu hiệu lừa đảo.", false),
    CHONG_MA_DOC_TAN_CONG_MANG(R.drawable.ic_bug, "Chống mã độc & tấn công mạng", "Chống tấn công mã độc, phần mềm tống tiền, tấn công mạng, ...", false),
    LUU_LICH_SU_TRUY_CAP(R.drawable.ic_history_bug, "Lưu trữ lịch sử truy cập", "Thống kê năng suất học tập/làm việc, thời gian sử dụng", false)
}