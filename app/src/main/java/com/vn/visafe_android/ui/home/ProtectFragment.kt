package com.vn.visafe_android.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProtectBinding
import com.vn.visafe_android.dns.net.doh.Transaction
import com.vn.visafe_android.dns.sys.InternalNames
import com.vn.visafe_android.dns.sys.PersistentState
import com.vn.visafe_android.dns.sys.VpnController
import com.vn.visafe_android.dns.sys.VpnState
import com.vn.visafe_android.utils.setOnSingClickListener

class ProtectFragment : BaseFragment<FragmentProtectBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var isChecked: Boolean = false

    companion object {
        const val REQUEST_CODE_PREPARE_VPN = 100
        fun newInstance() = ProtectFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Sync old settings into new preferences if necessary.
        PersistentState.syncLegacyState(context)


        // Export defaults into preferences.  See https://developer.android.com/guide/topics/ui/settings#Defaults
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)

        // Registers this class as a listener for user preference changes.

        // Registers this class as a listener for user preference changes.
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this)

        // Enable SVG support on very old versions of Android.

        // Enable SVG support on very old versions of Android.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        // Register broadcast receiver

        // Register broadcast receiver
        val intentFilter = IntentFilter(InternalNames.RESULT.name)
        intentFilter.addAction(InternalNames.DNS_STATUS.name)
        context?.let { LocalBroadcastManager.getInstance(it).registerReceiver(messageReceiver, intentFilter) }
    }

    override fun layoutRes(): Int = R.layout.fragment_protect

    override fun initView() {
        syncDnsStatus()

        binding.ivBtnProtect.setOnSingClickListener {
            if (!isChecked) {
                prepareAndStartDnsVpn()
            } else {
                stopDnsVpnService()
            }
        }

    }

    private fun maybeAutostart() {
        val controller = VpnController.getInstance()
        val state = controller.getState(context)
        if (state.activationRequested && !state.on) {
            prepareAndStartDnsVpn()
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val transaction = intent.getSerializableExtra(InternalNames.TRANSACTION.name) as Transaction?
            if (InternalNames.RESULT.name == intent.action) {
                transaction?.let {
                    updateStatsDisplay(
                        getNumRequests(), it
                    )
                }
            } else if (InternalNames.DNS_STATUS.name == intent.action) {
                syncDnsStatus()
            }
        }
    }

    private fun getNumRequests(): Long {
        val controller = VpnController.getInstance()
        return controller.getTracker(context).numRequests
    }

    private fun updateStatsDisplay(numRequests: Long, transaction: Transaction) {
        showNumRequests(numRequests)
        showTransaction(transaction)
    }

    private fun isHistoryEnabled(): Boolean {
        return VpnController.getInstance().getTracker(context).isHistoryEnabled
    }

    private fun showTransaction(transaction: Transaction) {
        if (isHistoryEnabled()) {
            Log.e("showTransaction: ", transaction.toString())
        }
    }

    private fun showNumRequests(numRequests: Long) {
//        viewBinding.numRequests.text = String.format(Locale.getDefault(), "%,d", numRequests)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (PersistentState.URL_KEY == key) {
            updateServerName()
        }
    }

    private fun startDnsVpnService() {
        VpnController.getInstance().start(context)
    }

    private fun stopDnsVpnService() {
        VpnController.getInstance().stop(context)
    }

    private fun prepareAndStartDnsVpn() {
        if (hasVpnService()) {
            if (prepareVpnService()) {
                startDnsVpnService()
//                updateServerName()
//                syncDnsStatus()
            }
        } else {
            Log.e("prepareAndStartDnsVpn: ", "Device does not support system-wide VPN mode.")
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun hasVpnService(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
    }

    @Throws(ActivityNotFoundException::class)
    private fun prepareVpnService(): Boolean {
        var prepareVpnIntent: Intent? = null
        prepareVpnIntent = try {
            VpnService.prepare(context)
        } catch (e: NullPointerException) {
            e.message?.let { Log.e("prepareVpnService: ", it) }
            // This exception is not mentioned in the documentation, but it has been encountered by Intra
            // users and also by other developers, e.g. https://stackoverflow.com/questions/45470113.
            return false
        }
        if (prepareVpnIntent != null) {
            startActivityForResult(prepareVpnIntent, REQUEST_CODE_PREPARE_VPN)
            syncDnsStatus() // Set DNS status to off in case the user does not grant VPN permissions
            return false
        }
        return true
    }

    private fun updateServerName() {
//        viewBinding.server.text = PersistentState.getServerName(this)
    }

    // Sets the UI DNS status on/off.
    private fun syncDnsStatus() {
        val status: VpnState = VpnController.getInstance().getState(context)
        // Change switch-button state
        isChecked = status.activationRequested
        Log.e("syncDnsStatus: ", "" + isChecked)
        // Change indicator text
        binding.tvProtect.text =
            if (status.activationRequested) getString(R.string.you_protect) else getString(R.string.bam_de_bat)
        context?.let {
            binding.ivEarth.setImageDrawable(
                if (status.activationRequested) ContextCompat.getDrawable(it, R.drawable.ic_earth) else
                    ContextCompat.getDrawable(it, R.drawable.ic_earth_off)
            )
            binding.ivBtnProtect.setImageDrawable(
                if (status.activationRequested) ContextCompat.getDrawable(it, R.drawable.ic_button_protect) else
                    ContextCompat.getDrawable(it, R.drawable.ic_button_protect_off)
            )
        }
    }


    override fun onActivityResult(request: Int, result: Int, data: Intent?) {
        super.onActivityResult(request, result, data)
        if (request == REQUEST_CODE_PREPARE_VPN) {
            if (result == RESULT_OK) {
                startDnsVpnService()
            } else {
                stopDnsVpnService()
            }
        }
    }
}