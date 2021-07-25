package vn.ncsc.visafe.utils

import androidx.lifecycle.MutableLiveData

object EventUtils {
    val isCreatePass: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
}