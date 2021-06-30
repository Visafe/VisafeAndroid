package com.vn.visafe_android.dns

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.MainContentBinding
import com.vn.visafe_android.dns.net.doh.Transaction
import com.vn.visafe_android.dns.sys.*
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class DNSActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
    }

//    lateinit var viewBinding: MainContentBinding
//
//
//    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val transaction = intent.getSerializableExtra(InternalNames.TRANSACTION.name) as Transaction?
//            if (InternalNames.RESULT.name == intent.action) {
//                transaction?.let {
//                    updateStatsDisplay(
//                        getNumRequests(), it
//                    )
//                }
//            } else if (InternalNames.DNS_STATUS.name == intent.action) {
//                syncDnsStatus()
//            }
//        }
//    }
//
//    private fun getNumRequests(): Long {
//        val controller = VpnController.getInstance()
//        return controller.getTracker(this).numRequests
//    }
//
//    private fun updateStatsDisplay(numRequests: Long, transaction: Transaction) {
//        showNumRequests(numRequests)
//        showTransaction(transaction)
//    }
//
//    private fun isHistoryEnabled(): Boolean {
//        return VpnController.getInstance().getTracker(this).isHistoryEnabled
//    }
//
//    private fun showTransaction(transaction: Transaction) {
//        if (isHistoryEnabled()) {
//            Log.e("showTransaction: ", transaction.toString())
//        }
//    }
//
//    private fun showNumRequests(numRequests: Long) {
//        viewBinding.numRequests.text = String.format(Locale.getDefault(), "%,d", numRequests)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewBinding = MainContentBinding.inflate(layoutInflater)
//
//        // Sync old settings into new preferences if necessary.
//        PersistentState.syncLegacyState(this)
//
//
//        // Export defaults into preferences.  See https://developer.android.com/guide/topics/ui/settings#Defaults
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
//
//        // Registers this class as a listener for user preference changes.
//
//        // Registers this class as a listener for user preference changes.
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
//
//        // Enable SVG support on very old versions of Android.
//
//        // Enable SVG support on very old versions of Android.
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        setContentView(viewBinding.root)
//        // Register broadcast receiver
//
//        // Register broadcast receiver
//        val intentFilter = IntentFilter(InternalNames.RESULT.name)
//        intentFilter.addAction(InternalNames.DNS_STATUS.name)
//        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter)
//        // Autostart if necessary
//        maybeAutostart()
//
//        viewBinding.dnsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                prepareAndStartDnsVpn()
//            } else {
//                stopDnsVpnService()
//            }
//        }
//    }
//
//
//    private fun maybeAutostart() {
//        val controller = VpnController.getInstance()
//        val state = controller.getState(this)
//        if (state.activationRequested && !state.on) {
//            prepareAndStartDnsVpn()
//        }
//    }
//
//    private fun startDnsVpnService() {
//        VpnController.getInstance().start(this)
//    }
//
//    private fun stopDnsVpnService() {
//        VpnController.getInstance().stop(this)
//    }
//
//    private fun prepareAndStartDnsVpn() {
//        if (hasVpnService()) {
//            if (prepareVpnService()) {
//                startDnsVpnService()
////                updateServerName()
////                syncDnsStatus()
//            }
//        } else {
//            Log.e("prepareAndStartDnsVpn: ", "Device does not support system-wide VPN mode.")
//        }
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private fun hasVpnService(): Boolean {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
//    }
//
//    @Throws(ActivityNotFoundException::class)
//    private fun prepareVpnService(): Boolean {
//        var prepareVpnIntent: Intent? = null
//        prepareVpnIntent = try {
//            VpnService.prepare(this)
//        } catch (e: NullPointerException) {
//            // This exception is not mentioned in the documentation, but it has been encountered by Intra
//            // users and also by other developers, e.g. https://stackoverflow.com/questions/45470113.
//            return false
//        }
//        if (prepareVpnIntent != null) {
//            startActivityForResult(prepareVpnIntent, REQUEST_CODE_PREPARE_VPN)
//            syncDnsStatus() // Set DNS status to off in case the user does not grant VPN permissions
//            return false
//        }
//        return true
//    }
//
//
//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        if (PersistentState.URL_KEY.equals(key)) {
//            updateServerName()
//        }
//    }
//
//    private fun updateServerName() {
//        viewBinding.server.text = PersistentState.getServerName(this)
//    }
//
//    // Sets the UI DNS status on/off.
//    private fun syncDnsStatus() {
//        val status: VpnState = VpnController.getInstance().getState(this)
//        // Change switch-button state
//        viewBinding.dnsSwitch.isChecked = status.activationRequested
//
//        // Change indicator text
//        viewBinding.indicator.setText(if (status.activationRequested) "On" else "OFF")
//
//        // Hide server change button by default
//        viewBinding.tryAllServersButton.visibility = View.INVISIBLE
//
//        // Change status and explanation text
//        val statusId: Int
//        val explanationId: Int
//        var privateDnsMode: PrivateDnsMode = PrivateDnsMode.NONE
//        if (status.activationRequested) {
//            if (status.connectionState == null) {
//                statusId = R.string.status_waiting
//                explanationId = R.string.explanation_offline
//            } else if (status.connectionState === IntraVpnService.State.NEW) {
//                statusId = R.string.status_starting
//                explanationId = R.string.explanation_starting
//            } else if (status.connectionState === IntraVpnService.State.WORKING) {
//                statusId = R.string.status_protected
//                explanationId = R.string.explanation_protected
//            } else {
//                // status.connectionState == ServerConnection.State.FAILING
//                statusId = R.string.status_failing
//                explanationId = R.string.explanation_failing
//                viewBinding.tryAllServersButton.visibility = View.VISIBLE
//            }
//        } else if (isAnotherVpnActive()) {
//            statusId = R.string.status_exposed
//            explanationId = R.string.explanation_vpn
//        } else {
//            privateDnsMode = getPrivateDnsMode()
//            if (privateDnsMode == PrivateDnsMode.STRICT) {
//                statusId = R.string.status_strict
//                explanationId = R.string.explanation_strict
//            } else if (privateDnsMode == PrivateDnsMode.UPGRADED) {
//                statusId = R.string.status_upgraded
//                explanationId = R.string.explanation_upgraded
//            } else {
//                statusId = R.string.status_exposed
//                explanationId = R.string.explanation_exposed
//            }
//        }
//        val colorId: Int
//        if (status.on) {
//            colorId = if (status.connectionState !== IntraVpnService.State.FAILING) R.color.accent_color else R.color.secondary_color
//        } else if (privateDnsMode == PrivateDnsMode.STRICT) {
//            // If the VPN is off but we're in strict mode, show the status in white.  This isn't a bad
//            // state, but Intra isn't helping.
//            colorId = R.color.red
//        } else {
//            colorId = R.color.secondary_color
//        }
//        val color = ContextCompat.getColor(this, colorId)
//        viewBinding.status.setTextColor(color)
//        viewBinding.status.setText(statusId)
//        viewBinding.explanation.setText(explanationId)
//
////        // Change graph foreground and background tint.
////        val graph: HistoryGraph = controlView.findViewById(R.id.graph)
////        graph.setColor(color)
////        val backdrop = controlView.findViewById<ImageView>(R.id.graph_backdrop)
////        backdrop.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
////
////        // Show/hide secure/insecure details
////        val systemDetails = controlView.findViewById<View>(R.id.system_details)
////        systemDetails.visibility = if (status.on) View.VISIBLE else View.GONE
////        val insecureSystemDetails = controlView.findViewById<View>(R.id.insecure_system_details)
////        insecureSystemDetails.visibility = if (status.on) View.GONE else View.VISIBLE
////        if (!status.on) {
////            val defaultProtocol = controlView.findViewById<TextView>(R.id.default_protocol)
////            val tls = privateDnsMode != PrivateDnsMode.NONE
////            defaultProtocol.setText(if (tls) R.string.tls_transport else R.string.insecure_transport)
////            val defaultProtocolIcon = controlView.findViewById<ImageView>(R.id.default_protocol_icon)
////            defaultProtocolIcon.setImageResource(
////                if (tls) R.drawable.ic_lock_black_24dp else R.drawable.ic_lock_open_black_24dp
////            )
////            defaultProtocolIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
////            var protocolTarget: app.intra.ui.MainActivity.InfoPage = MainActivity.InfoPage.DEFAULT_PROTOCOL
////            if (privateDnsMode == PrivateDnsMode.STRICT) {
////                protocolTarget = MainActivity.InfoPage.STRICT_MODE_PROTOCOL
////            } else if (tls) {
////                protocolTarget = MainActivity.InfoPage.UPGRADED_PROTOCOL
////            }
////            setInfoClicker(R.id.default_protocol_box, protocolTarget)
////            var systemDnsServer: String = getSystemDnsServer()
////            if (systemDnsServer == null) {
////                systemDnsServer = resources.getText(R.string.unknown_server).toString()
////            }
////            val serverLabel = controlView.findViewById<TextView>(R.id.default_server_value)
////            serverLabel.text = systemDnsServer
////            val defaultServerIcon = controlView.findViewById<ImageView>(R.id.default_server_icon)
////            defaultServerIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
////            setInfoClicker(
////                R.id.default_server_box,
////                if (privateDnsMode == PrivateDnsMode.STRICT) MainActivity.InfoPage.STRICT_MODE_SERVER else MainActivity.InfoPage.DEFAULT_SERVER
////            )
////        }
//    }
//
//    private fun getSystemDnsServer(): String {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            // getDnsServers requires Lollipop or later.
//            return ""
//        }
//        val linkProperties = getLinkProperties() ?: return ""
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val privateDnsServerName = linkProperties.privateDnsServerName
//            if (privateDnsServerName != null) {
//                return privateDnsServerName
//            }
//        }
//        for (address in linkProperties.dnsServers) {
//            // Show the first DNS server on the list.
//            return address.hostAddress
//        }
//        return ""
//    }
//
//    private enum class PrivateDnsMode {
//        NONE,  // The setting is "Off" or "Opportunistic", and the DNS connection is not using TLS.
//        UPGRADED,  // The setting is "Opportunistic", and the DNS connection has upgraded to TLS.
//        STRICT // The setting is "Strict".
//    }
//
//    private fun getPrivateDnsMode(): PrivateDnsMode {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            // Private DNS was introduced in P.
//            return PrivateDnsMode.NONE
//        }
//        val linkProperties: LinkProperties = getLinkProperties() ?: return PrivateDnsMode.NONE
//        if (linkProperties.privateDnsServerName != null) {
//            return PrivateDnsMode.STRICT
//        }
//        return if (linkProperties.isPrivateDnsActive) {
//            PrivateDnsMode.UPGRADED
//        } else PrivateDnsMode.NONE
//    }
//
//    private fun getLinkProperties(): LinkProperties? {
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            // getActiveNetwork() requires M or later.
//            return null
//        }
//        val activeNetwork = connectivityManager.activeNetwork ?: return null
//        return connectivityManager.getLinkProperties(activeNetwork)
//    }
//
//    private fun isAnotherVpnActive(): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//            val activeNetwork = connectivityManager.activeNetwork ?: return false
//            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
//                ?: // It's not clear when this can happen, but it has occurred for at least one user.
//                return false
//            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
//        }
//        // For pre-M versions, return true if there's any network whose name looks like a VPN.
//        try {
//            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
//            while (networkInterfaces.hasMoreElements()) {
//                val networkInterface = networkInterfaces.nextElement()
//                val name = networkInterface.name
//                if (networkInterface.isUp && name != null &&
//                    (name.startsWith("tun") || name.startsWith("pptp") || name.startsWith("l2tp"))
//                ) {
//                    return true
//                }
//            }
//        } catch (e: SocketException) {
//            Log.e("isAnotherVpnActive: ", "" + e.message)
//        }
//        return false
//    }
//
//    private val REQUEST_CODE_PREPARE_VPN = 100
//
//    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
//        super.onActivityResult(request, result, data)
//        if (request == REQUEST_CODE_PREPARE_VPN) {
//            if (result == RESULT_OK) {
//                startDnsVpnService()
//            } else {
//                stopDnsVpnService()
//            }
//        }
//    }
}