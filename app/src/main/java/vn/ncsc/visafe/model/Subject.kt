package vn.ncsc.visafe.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subject(
    var title: String,
    var value: String,
    val icon: Int,
    var isChecked: Boolean = false
) : Parcelable