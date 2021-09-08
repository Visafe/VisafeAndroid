package vn.ncsc.visafe.utils

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import vn.ncsc.visafe.model.UserInfo


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

    fun isLogin(): Boolean {
        return sharedPref.getBoolean(PreferenceKey.ISLOGIN, false)
    }

    fun isFirstShowOnBoarding(): Boolean {
        return sharedPref.getBoolean(PreferenceKey.IS_FIRST_SHOW_ON_BOARDING, true)
    }

    fun getUserInfo(): UserInfo {
        val ch = sharedPref.getString(PreferenceKey.USER_INFO, "{}")!!
        val gson = Gson()
        return gson.fromJson(ch, UserInfo::class.java)
    }

    fun isEnableProtectedWifiHome(): Boolean {
        return sharedPref.getBoolean(PreferenceKey.IS_ENABLE_PROTECTED_WIFI_HOME, false)
    }

    fun clearAllData() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.putBoolean(PreferenceKey.IS_FIRST_SHOW_ON_BOARDING, false)
        editor.apply()
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