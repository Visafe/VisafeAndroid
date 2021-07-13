package vn.ncsc.visafe.utils

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

fun setSafeClickListener(view: View?, listener: View.OnClickListener) {
    view?.let {
        RxView.clicks(it)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { listener.onClick(view) }
    }
}