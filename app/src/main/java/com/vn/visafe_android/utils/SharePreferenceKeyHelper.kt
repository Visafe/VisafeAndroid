package com.vn.visafe_android.utils

import android.app.Application
import android.content.SharedPreferences


class SharePreferenceKeyHelper private constructor(context: Application) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PreferenceKey.PREF_NAME, PreferenceKey.PRIVATE_MODE)

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putString(key: String, value: String) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String): String {
        return sharedPref.getString(key, "")!!
    }

    companion object {
        private var instance: SharePreferenceKeyHelper? = null
        fun getInstance(context: Application): SharePreferenceKeyHelper {
            if (instance == null)  // NOT thread safe!
                instance = SharePreferenceKeyHelper(context)

            return instance!!
        }
    }

}