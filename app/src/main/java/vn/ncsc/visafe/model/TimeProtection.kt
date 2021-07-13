package vn.ncsc.visafe.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeProtection(
    var startTime: String = "",
    var endTime: String = "",
    var isProtectionAllDay: Boolean = false,
    var isChecked: Boolean = false
) : Parcelable
