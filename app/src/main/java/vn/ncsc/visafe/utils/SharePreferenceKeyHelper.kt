package vn.ncsc.visafe.utils

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.WorkspaceGroupData


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

    fun getWorkspaceChoose(): WorkspaceGroupData {
        val ch = sharedPref.getString(PreferenceKey.WORKSPACE_CHOOSE, "{}")!!
        val gson = Gson()
        return gson.fromJson(ch, WorkspaceGroupData::class.java)
    }

    fun isEnableProtectedWifiHome(): Boolean {
        return sharedPref.getBoolean(PreferenceKey.IS_ENABLE_PROTECTED_WIFI_HOME, false)
    }

    fun getHostName(): String? {
        return sharedPref.getString(PreferenceKey.HOST_NAME, NetworkClient.DOMAIN)
    }

    fun clearAllData() {
        val editor = sharedPref.edit()
        val isProtectedDevice = sharedPref.getBoolean(PreferenceKey.STATUS_OPEN_VPN, false)
        val deviceId = sharedPref.getString(PreferenceKey.DEVICE_ID, "")
        val timeScan = sharedPref.getString(PreferenceKey.TIME_LAST_SCAN, "")
        val numberOfError = sharedPref.getString(PreferenceKey.NUMBER_OF_ERROR, "")
        editor.clear()
        editor.putBoolean(PreferenceKey.IS_FIRST_SHOW_ON_BOARDING, false)
        editor.putBoolean(PreferenceKey.STATUS_OPEN_VPN, isProtectedDevice)
        editor.putString(PreferenceKey.DEVICE_ID, deviceId)
        editor.putString(PreferenceKey.TIME_LAST_SCAN, timeScan)
        editor.putString(PreferenceKey.NUMBER_OF_ERROR, numberOfError)
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