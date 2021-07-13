package vn.ncsc.visafe.dns.sys

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// Singleton class to maintain state related to VPN Tunnel service.
class VpnController private constructor() : Cloneable {
    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        throw CloneNotSupportedException()
    }

    var viSafeVpnService: ViSafeVpnService? = null
    private var connectionState: ViSafeVpnService.State? = null

    @Synchronized
    fun onConnectionStateChanged(context: Context, state: ViSafeVpnService.State?) {
        if (viSafeVpnService == null) {
            // User clicked disable while the connection state was changing.
            return
        }
        connectionState = state
        stateChanged(context)
    }

    private fun stateChanged(context: Context) {
        val broadcast = Intent(InternalNames.DNS_STATUS.name)
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast)
    }

    @Synchronized
    fun start(context: Context) {
        if (viSafeVpnService != null) {
            return
        }
        PersistentState.instance.setVpnEnabled(context, true)
        stateChanged(context)
        val startServiceIntent = Intent(context, ViSafeVpnService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startServiceIntent)
        } else {
            context.startService(startServiceIntent)
        }
    }

    @Synchronized
    fun onStartComplete(context: Context, succeeded: Boolean) {
        if (!succeeded) {
            // VPN setup only fails if VPN permission has been revoked.  If this happens, clear the
            // user intent state and reset to the default state.
            stop(context)
        } else {
            stateChanged(context)
        }
    }

    @Synchronized
    fun stop(context: Context) {
        PersistentState.instance.setVpnEnabled(context, false)
        connectionState = null
        if (viSafeVpnService != null) {
            viSafeVpnService!!.signalStopService(true)
        }
        viSafeVpnService = null
        stateChanged(context)
    }

    @Synchronized
    fun getState(context: Context?): VpnState {
        val requested = context?.let { PersistentState.instance.getVpnEnabled(it) }
        val on = viSafeVpnService != null && viSafeVpnService!!.isOn
        return VpnState(requested, on, connectionState)
    }

    companion object {
        private var dnsVpnServiceState: VpnController? = null

        @get:Synchronized
        val instance: VpnController
            get() {
                if (dnsVpnServiceState == null) {
                    dnsVpnServiceState = VpnController()
                }
                return dnsVpnServiceState as VpnController
            }
    }
}