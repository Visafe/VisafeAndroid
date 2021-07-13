package vn.ncsc.visafe.model

data class DeviceData(
    val nameDevice: String? = null,
    val nameUser: String? = null,
    val isProtected: Boolean = false,
    val dayBlock: Int = 0
)
