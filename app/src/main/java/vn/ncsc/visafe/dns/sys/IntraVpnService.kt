package vn.ncsc.visafe.dns.sys

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.VpnService
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.preference.PreferenceManager
import android.service.quicksettings.TileService
import android.text.TextUtils
import android.util.Log
import androidx.annotation.GuardedBy
import androidx.annotation.WorkerThread
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import vn.ncsc.visafe.ui.MainActivity
import protect.Protector
import vn.ncsc.visafe.R
import vn.ncsc.visafe.dns.net.doh.Transaction
import vn.ncsc.visafe.dns.net.go.GoVpnAdapter
import java.util.*

class IntraVpnService : VpnService(), NetworkManager.NetworkListener, OnSharedPreferenceChangeListener, Protector {
    /**
     * null: There is no connection
     * NEW: The connection has not yet completed bootstrap.
     * WORKING: The last query (or bootstrap) succeeded.
     * FAILING: The last query (or bootstrap) failed.
     */
    enum class State {
        NEW, WORKING, FAILING
    }

    // The network manager is populated in onStartCommand.  Its main function is to enable delayed
    // initialization if the network is initially disconnected.
    @GuardedBy("vpnController")
    private var networkManager: NetworkManager? = null

    // The state of the device's network access, recording the latest update from networkManager.
    private var networkConnected = false

    // The VPN adapter runs within this service and is responsible for establishing the VPN and
    // passing packets to the network.  vpnAdapter is only null before startup and after shutdown,
    // but it may be atomically replaced by restartVpn().
    @GuardedBy("vpnController")
    private var vpnAdapter: GoVpnAdapter? = null

    // The URL of the DNS server.  null and "" are special values indicating the default server.
    // This value can change if the user changes their configuration after starting the VPN.
    @GuardedBy("vpnController")
    private var url: String? = null

    // The URL of a pending connection attempt, or a special value if there is no pending connection
    // attempt.  This value is only used within updateServerConnection(), where it serves to avoid
    // the creation of duplicate outstanding server connection attempts.  Whenever pendingUrl
    // indicates a pending connection, serverConnection should be null to avoid sending queries to the
    // previously selected server.
    @GuardedBy("vpnController")
    private val pendingUrl = NO_PENDING_CONNECTION
    val isOn: Boolean
        get() = vpnAdapter != null

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        if (PersistentState.APPS_KEY == key && vpnAdapter != null) {
            // Restart the VPN so the new app exclusion choices take effect immediately.
            restartVpn()
        }
        if (PersistentState.URL_KEY == key) {
            url = PersistentState.instance.getServerUrl(this)
            spawnServerUpdate()
        }
    }

    private fun spawnServerUpdate() {
        synchronized(vpnController) {
            if (networkManager != null) {
                Thread(
                    { updateServerConnection() }, "updateServerConnection-onStartCommand"
                ).start()
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        synchronized(vpnController) {
            Log.i(LOG_TAG, String.format("Starting DNS VPN service, url=%s", url))
            url = PersistentState.instance.getServerUrl(this)

            // Registers this class as a listener for user preference changes.
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
            if (networkManager != null) {
                spawnServerUpdate()
                return START_REDELIVER_INTENT
            }

            // If we're online, |networkManager| immediately calls this.onNetworkConnected(), which in turn
            // calls startVpn() to actually start.  If we're offline, the startup actions will be delayed
            // until we come online.
            networkManager = NetworkManager(this@IntraVpnService, this@IntraVpnService)

            // Mark this as a foreground service.  This is normally done to ensure that the service
            // survives under memory pressure.  Since this is a VPN service, it is presumably protected
            // anyway, but the foreground service mechanism allows us to set a persistent notification,
            // which helps users understand what's going on, and return to the app if they want.
            val mainActivityIntent = PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
            )
            var builder: Notification.Builder
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val name: CharSequence = getString(R.string.channel_name)
                val description = getString(R.string.channel_description)
                // LOW is the lowest importance that is allowed with startForeground in Android O.
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel =
                    NotificationChannel(MAIN_CHANNEL_ID, name, importance)
                channel.description = description
                val notificationManager =
                    getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
                builder = Notification.Builder(this, MAIN_CHANNEL_ID)
            } else {
                builder = Notification.Builder(this)
                // Min-priority notifications don't show an icon in the notification bar, reducing clutter.
                builder = builder.setPriority(Notification.PRIORITY_MIN)
            }
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(resources.getText(R.string.notification_title))
                .setContentText(resources.getText(R.string.notification_content))
                .setContentIntent(mainActivityIntent)
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                // Secret notifications are not shown on the lock screen.  No need for this app to show there.
                // Only available in API >= 21
                builder = builder.setVisibility(Notification.VISIBILITY_SECRET)
            }
            startForeground(SERVICE_ID, builder.notification)
            updateQuickSettingsTile()
            return START_REDELIVER_INTENT
        }
    }

    @WorkerThread
    private fun updateServerConnection() {
        synchronized(vpnController) {
            if (vpnAdapter != null) {
                vpnAdapter!!.updateDohUrl()
            }
        }
    }

    /**
     * Starts the VPN. This method performs network activity, so it must not run on the main thread.
     * This method is idempotent, and is synchronized so that it can safely be called from a
     * freshly spawned thread.
     */
    @WorkerThread
    private fun startVpn() {
        synchronized(vpnController) {
            if (vpnAdapter != null) {
                return
            }
            startVpnAdapter()
            vpnController.onStartComplete(this, vpnAdapter != null)
            if (vpnAdapter == null) {
                Log.e("startVpn: ", "Failed to startVpn VPN adapter")
                stopSelf()
            }
        }
    }

    private fun restartVpn() {
        synchronized(vpnController) {

            // Attempt seamless handoff as described in the docs for VpnService.Builder.establish().
            val oldAdapter = vpnAdapter
            vpnAdapter = makeVpnAdapter()
            oldAdapter!!.close()
            if (vpnAdapter != null) {
                vpnAdapter!!.start()
            } else {
                Log.e("restartVpn: ", "Restart failed")
            }
        }
    }

    override fun onCreate() {
        Log.d("onCreate: ", "Creating DNS VPN service")
        vpnController.intraVpnService = this
    }

    fun signalStopService(userInitiated: Boolean) {
        Log.d("signalStopService: ", String.format("Received stop signal. User initiated: %b", userInitiated))
        if (!userInitiated) {
            val vibrationPattern = longArrayOf(1000) // Vibrate for one second.
            // Show revocation warning
            var builder: Notification.Builder
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val name: CharSequence = getString(R.string.warning_channel_name)
                val description = getString(R.string.warning_channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(WARNING_CHANNEL_ID, name, importance)
                channel.description = description
                channel.enableVibration(true)
                channel.vibrationPattern = vibrationPattern
                notificationManager.createNotificationChannel(channel)
                builder = Notification.Builder(this, WARNING_CHANNEL_ID)
            } else {
                builder = Notification.Builder(this)
                builder.setVibrate(vibrationPattern)
                // Deprecated in API 26.
                builder = builder.setPriority(Notification.PRIORITY_MAX)
            }
            val mainActivityIntent = PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(resources.getText(R.string.warning_title))
                .setContentText(resources.getText(R.string.notification_content))
                .setFullScreenIntent(mainActivityIntent, true) // Open the main UI if possible.
                .setAutoCancel(true)
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                builder.setCategory(Notification.CATEGORY_ERROR)
            }
            notificationManager.notify(0, builder.notification)
        }
        stopVpnAdapter()
        stopSelf()
        updateQuickSettingsTile()
    }

    private fun makeVpnAdapter(): GoVpnAdapter {
        return GoVpnAdapter.establish(this)
    }

    private fun startVpnAdapter() {
        synchronized(vpnController) {
            if (vpnAdapter == null) {
                Log.d("startVpnAdapter: ", "Starting VPN adapter")
                vpnAdapter = makeVpnAdapter()
                if (vpnAdapter != null) {
                    vpnAdapter!!.start()
                } else {
                    Log.d("startVpnAdapter: ", "Failed to start VPN adapter!")
                }
            }
        }
    }

    private fun stopVpnAdapter() {
        synchronized(vpnController) {
            if (vpnAdapter != null) {
                vpnAdapter!!.close()
                vpnAdapter = null
                vpnController.onConnectionStateChanged(this, null)
            }
        }
    }

    private fun updateQuickSettingsTile() {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            TileService.requestListeningState(
                this,
                ComponentName(this, IntraTileService::class.java)
            )
        }
    }

    override fun onDestroy() {
        synchronized(vpnController) {
            Log.d("onDestroy: ", "Destroying DNS VPN service")
            PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
            if (networkManager != null) {
                networkManager!!.destroy()
            }
            vpnController.intraVpnService = null
            stopForeground(true)
            if (vpnAdapter != null) {
                signalStopService(false)
            }
        }
    }

    override fun onRevoke() {
        Log.d("onRevoke: ", "VPN service revoked.")
        stopSelf()

        // Disable autostart if VPN permission is revoked.
        PersistentState.instance.setVpnEnabled(this, false)
    }

    fun newBuilder(): Builder {
        var builder = Builder()
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            // Some WebRTC apps rely on the ability to bind to specific interfaces, which is only
            // possible if we allow bypass.
            builder = builder.allowBypass()
            try {
                // Workaround for any app incompatibility bugs.
                for (packageName in PersistentState.instance.getExcludedPackages(this)!!) {
                    builder = builder.addDisallowedApplication(packageName!!)
                }
                // Play Store incompatibility is a known issue, so always exclude it.
                builder = builder.addDisallowedApplication("com.android.vending")
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e("newBuilder: ", e.message!!)
                Log.e(LOG_TAG, "Failed to exclude an app", e)
            }
        }
        return builder
    }

    fun recordTransaction(transaction: Transaction) {
        transaction.responseTime = SystemClock.elapsedRealtime()
        transaction.responseCalendar = Calendar.getInstance()
        val intent = Intent(InternalNames.RESULT.name)
        intent.putExtra(InternalNames.TRANSACTION.name, transaction)
        LocalBroadcastManager.getInstance(this@IntraVpnService).sendBroadcast(intent)
        if (!networkConnected) {
            // No need to update the user-visible connection state while there is no network.
            return
        }

        // Update the connection state.  If the transaction succeeded, then the connection is working.
        // If the transaction failed, then the connection is not working.
        // If the transaction was canceled, then we don't have any new information about the status
        // of the connection, so we don't send an update.
        if (transaction.status === Transaction.Status.COMPLETE) {
            vpnController.onConnectionStateChanged(this, State.WORKING)
        } else if (transaction.status !== Transaction.Status.CANCELED) {
            vpnController.onConnectionStateChanged(this, State.FAILING)
        }
    }

    private fun setNetworkConnected(connected: Boolean) {
        networkConnected = connected
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            // Indicate that traffic will be sent over the current active network.
            // See e.g. https://issuetracker.google.com/issues/68657525
            val activeNetwork = getSystemService(ConnectivityManager::class.java).activeNetwork
            setUnderlyingNetworks(if (connected) arrayOf(activeNetwork) else null)
        }
    }

    // NetworkListener interface implementation
    override fun onNetworkConnected(networkInfo: NetworkInfo) {
        Log.d("onNetworkConnected: ", "Connected event.")
        setNetworkConnected(true)
        // This code is used to start the VPN for the first time, but startVpn is idempotent, so we can
        // call it every time. startVpn performs network activity so it has to run on a separate thread.
        Thread(
            {
                updateServerConnection()
                startVpn()
            }, "startVpn-onNetworkConnected"
        )
            .start()
    }

    override fun onNetworkDisconnected() {
        Log.d("onNetworkDisconnected: ", "Disconnected event.")
        setNetworkConnected(false)
        vpnController.onConnectionStateChanged(this, null)
    }

    // From the Protect interface.
    override fun getResolvers(): String {
        val ips: MutableList<String?> = ArrayList()
        for (ip in networkManager!!.systemResolvers) {
            val address = ip.hostAddress
            if (GoVpnAdapter.FAKE_DNS_IP != address) {
                ips.add(address)
            }
        }
        return TextUtils.join(",", ips)
    }

    companion object {
        private const val LOG_TAG = "IntraVpnService"
        private const val SERVICE_ID = 1 // Only has to be unique within this app.
        private const val MAIN_CHANNEL_ID = "vpn"
        private const val WARNING_CHANNEL_ID = "warning"
        private const val NO_PENDING_CONNECTION = "This value is not a possible URL."

        // Reference to the singleton VpnController, for convenience
        private val vpnController = VpnController.instance
    }
}