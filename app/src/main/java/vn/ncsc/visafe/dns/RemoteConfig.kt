package vn.ncsc.visafe.dns

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.*

/**
 * Utility class for initializing Firebase Remote Config.  Remote Configuration allows us to conduct
 * A/B tests of experimental functionality, and to enable or disable features without having to
 * release a new version of the app.
 */
object RemoteConfig {
    fun update(): Task<Boolean> {
        return try {
            val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            config.fetchAndActivate()
        } catch (e: IllegalStateException) {
            e.message?.let { Log.d("RemoteConfig update: ", it) }
            Tasks.forResult(false)
        }
    }

    private fun getExtraIPKey(domain: String): String {
        // Convert everything to lowercase for consistency.
        // The only allowed characters in a remote config key are A-Z, a-z, 0-9, and _.
        return "extra_ips_" + domain.lowercase(Locale.ROOT).replace("\\W".toRegex(), "_")
    }

    // Returns any additional IPs known for this domain, as a string containing a comma-separated
    // list, or the empty string if none are known.
    @SuppressLint("LongLogTag")
    fun getExtraIPs(domain: String): String {
        return try {
            FirebaseRemoteConfig.getInstance().getString(getExtraIPKey(domain))
        } catch (e: IllegalStateException) {
            e.message?.let { Log.d("RemoteConfig getExtraIPs: ", it) }
            ""
        }
    }

    val choirEnabled: Boolean
        @SuppressLint("LongLogTag")
        get() = try {
            FirebaseRemoteConfig.getInstance().getBoolean("choir")
        } catch (e: IllegalStateException) {
            e.message?.let { Log.d("RemoteConfig choirEnabled: ", it) }
            false
        }
}