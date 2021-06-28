package com.vn.visafe_android

import androidx.multidex.MultiDexApplication
import com.vn.visafe_android.utils.SharePreferenceKeyHelper

class ViSafeApp : MultiDexApplication() {

    companion object {
        const val TAG = "App"
    }

    private var instance: ViSafeApp? = null

    fun getInstance(): ViSafeApp? {
        return instance
    }

    @Synchronized
    fun setInstance(app: ViSafeApp) {
        if (instance == null) instance = app
    }

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        getPreference()
    }

    fun getPreference(): SharePreferenceKeyHelper {
        return SharePreferenceKeyHelper.getInstance(this)
    }

}