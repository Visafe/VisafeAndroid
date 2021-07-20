package vn.ncsc.visafe.dns.sys

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import android.util.Log
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.dns.net.setting.RandomString
import vn.ncsc.visafe.dns.net.setting.Untemplate.strip
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class PersistentState {

    companion object {
        const val LOG_TAG = "PersistentState"
        const val APPS_KEY = "pref_apps"
        const val URL_KEY = "pref_server_url"
        const val APPROVED_KEY = "approved"
        const val ENABLED_KEY = "enabled"
        const val SERVER_KEY = "server"
        const val INTERNAL_STATE_NAME = "MainActivity"

        // The approval state is currently stored in a separate preferences file.
        // TODO: Unify preferences into a single file.
        const val APPROVAL_PREFS_NAME = "IntroState"

        private var persistentState: PersistentState? = null

        @get:Synchronized
        val instance: PersistentState
            get() {
                if (persistentState == null) {
                    persistentState = PersistentState()
                }
                return persistentState as PersistentState
            }
    }

    private fun getInternalState(context: Context): SharedPreferences {
        return context.getSharedPreferences(INTERNAL_STATE_NAME, Context.MODE_PRIVATE)
    }

    private fun getUserPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getVpnEnabled(context: Context): Boolean {
        return getInternalState(context).getBoolean(ENABLED_KEY, false)
    }

    fun setVpnEnabled(context: Context, enabled: Boolean) {
        val editor = getInternalState(context).edit()
        editor.putBoolean(ENABLED_KEY, enabled)
        editor.apply()
    }

    fun syncLegacyState(context: Context) {
        // Copy the domain choice into the new URL setting, if necessary.
        if (getServerUrl(context) != null) {
            // New server URL is already populated
            return
        }

        // There is no URL setting, so read the legacy server name.
        val settings = getInternalState(context)
        val domain = settings.getString(SERVER_KEY, null)
            ?: // Legacy setting is in the default state, so we can leave the new URL setting in the default
            // state as well.
            return
        val urls = context.resources.getStringArray(R.array.urls)
        val defaultDomain = context.resources.getString(R.string.legacy_domain0)
        var url: String? = null
        if (domain == defaultDomain) {
            // Common case: the domain is dns.google.com, which now corresponds to dns.google (url 0).
            url = urls[0]
        } else {
            // In practice, we expect that domain will always be cloudflare-dns.com at this point, because
            // that was the only other option in the relevant legacy versions of Intra.
            // Look for the corresponding URL among the builtin servers.
            for (u in urls) {
                val parsed = Uri.parse(u)
                if (domain == parsed.host) {
                    url = u
                    break
                }
            }
        }
        if (url == null) {
            Log.d("syncLegacyState: ", "Legacy domain is unrecognized")
            return
        }
        setServerUrl(context, url)
    }

    private fun setServerUrl(context: Context, url: String?) {
        val editor = getUserPreferences(context).edit()
        editor.putString(URL_KEY, url)
        editor.apply()
    }

    fun getServerUrl(context: Context): String? {
        val urlTemplate: String = context.let { getUserPreferences(it).getString(URL_KEY, null) }
            ?: return null
        return strip(urlTemplate)
    }

    private fun extractHost(url: String): String? {
        return try {
            val parsed = URL(url)
            parsed.host
        } catch (e: MalformedURLException) {
            Log.d("extractHost: ", "URL is corrupted")
            null
        }
    }

    private var urlDefault: String = ""
    private var idDefault: String = ""

    // Converts a null url into the actual default url.  Otherwise, returns url unmodified.
    fun expandUrl(context: Context, url: String?): String {
        var userid = ""
        val share = context.applicationContext.getSharedPreferences("userID", 0)
        val shareVip = context.applicationContext.getSharedPreferences("VIP_MODE", 0)
        userid = share.getString("userID", "").toString()
        val reqString = (Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
        return if (userid === "") {
            if (url == null || url.isEmpty()) {
                val rd = RandomString()
                val randomId: String = rd.getAlphaNumericString(12)
                idDefault = randomId.lowercase(Locale.getDefault())
                urlDefault = NetworkClient.DOMAIN + randomId.lowercase(Locale.getDefault())
                val editor = share.edit()
                editor.putString("userID", randomId.lowercase(Locale.getDefault()))
                editor.putString(
                    "deviceName",
                    reqString.replace("\\s+".toRegex(), "").replace("[\\+\\.\\^\\-:,%&@*$;!#~=_<>?()]".toRegex(), "")
                        .lowercase(Locale.getDefault())
                )
                editor.apply()
                if (shareVip.getString("domainVIP", "") != "") {
                    urlDefault = shareVip.getString("domainVIP", "") + share.getString("userID", "")
                }
                Log.d("urlDefault: ", urlDefault)
                return urlDefault
            }
            urlDefault
        } else {
            if (shareVip.getString("domainVIP", "") != "") {
                println(shareVip.getString("domainVIP", "") + share.getString("userID", ""))
                shareVip.getString("domainVIP", "") + share.getString("userID", "")
            } else {
                println(NetworkClient.DOMAIN + share.getString("userID", ""))
                NetworkClient.DOMAIN + share.getString("userID", "")
            }
        }
    }

    // Returns a domain representing the current configured server URL, for use as a display name.
    fun getServerName(context: Context): String? {
        return extractHost(expandUrl(context, getServerUrl(context)))
    }

    /**
     * Extract the hostname from the URL, while avoiding disclosure of custom servers.
     *
     * @param url A DoH server configuration url (or url template).
     * @return Returns the domain name in the URL, or "CUSTOM_SERVER" if the url is not one of the
     * built-in servers.
     */
    fun extractHostForAnalytics(context: Context, url: String?): String? {
        val expanded = expandUrl(context, url)
        val urls = context.resources.getStringArray(R.array.urls)
        return if (listOf(*urls).contains(expanded)) {
            extractHost(expanded)
        } else InternalNames.CUSTOM_SERVER.name
    }

    private fun getApprovalSettings(context: Context): SharedPreferences {
        return context.getSharedPreferences(APPROVAL_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getWelcomeApproved(context: Context): Boolean {
        return getApprovalSettings(context).getBoolean(APPROVED_KEY, false)
    }

    fun setWelcomeApproved(context: Context, approved: Boolean) {
        val editor = getApprovalSettings(context).edit()
        editor.putBoolean(APPROVED_KEY, approved)
        editor.apply()
    }

    fun getExcludedPackages(context: Context): Set<String?>? {
        return getUserPreferences(context).getStringSet(APPS_KEY, HashSet())
    }
}