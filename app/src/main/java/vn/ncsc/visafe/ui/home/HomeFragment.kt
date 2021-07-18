package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentHomeBinding
import vn.ncsc.visafe.dns.net.doh.Transaction
import vn.ncsc.visafe.dns.sys.InternalNames
import vn.ncsc.visafe.dns.sys.PersistentState
import vn.ncsc.visafe.dns.sys.VpnController
import vn.ncsc.visafe.utils.setOnSingClickListener


class HomeFragment : BaseFragment<FragmentHomeBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var isStatusEnable: Boolean = false
    private var vibrator: Vibrator? = null
    private var aniRotateClk: Animation? = null

    companion object {
        const val REQUEST_CODE_PREPARE_VPN = 100
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Sync old settings into new preferences if necessary.
        context?.let { PersistentState.instance.syncLegacyState(it) }

        // Export defaults into preferences.  See https://developer.android.com/guide/topics/ui/settings#Defaults
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)

        // Registers this class as a listener for user preference changes.
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this)

        // Enable SVG support on very old versions of Android.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Register broadcast receiver
        val intentFilter = IntentFilter(InternalNames.RESULT.name)
        intentFilter.addAction(InternalNames.DNS_STATUS.name)
        context?.let { LocalBroadcastManager.getInstance(it).registerReceiver(messageReceiver, intentFilter) }

        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        aniRotateClk = AnimationUtils.loadAnimation(context, R.anim.round_animation)
    }

    override fun layoutRes(): Int = R.layout.fragment_home

    override fun initView() {
        syncDnsStatus()

        binding.buttonActive.setOnSingClickListener {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(200)
            }
            if (!isStatusEnable) {
                binding.roundImage.visibility = View.INVISIBLE
                prepareAndStartDnsVpn()
            } else {
                stopDnsVpnService()
            }
        }

    }

    private fun maybeAutostart() {
        val controller = VpnController.instance
        val state = controller.getState(context)
        if (state.activationRequested == true && !state.on) {
            prepareAndStartDnsVpn()
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val transaction = intent.getSerializableExtra(InternalNames.TRANSACTION.name) as Transaction?
            if (InternalNames.RESULT.name == intent.action) {
                transaction?.let {
                    Log.e("onReceive: ", "" + it)
                }
            } else if (InternalNames.DNS_STATUS.name == intent.action) {
                syncDnsStatus()
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (PersistentState.URL_KEY == key) {
            updateServerName()
        }
    }

    private fun startDnsVpnService() {
        context?.let { VpnController.instance.start(it) }
    }

    private fun stopDnsVpnService() {
        context?.let { VpnController.instance.stop(it) }
    }

    private fun prepareAndStartDnsVpn() {
        if (hasVpnService()) {
            if (prepareVpnService()) {
                startDnsVpnService()
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
        try {
            val status = VpnController.instance.getState(context)
            status.let {
                // Change switch-button state
                isStatusEnable = status.activationRequested == true
                Log.e("syncDnsStatus: ", "" + isStatusEnable)
                // Change indicator text
                binding.tv1.visibility =
                    if (status.activationRequested == true) View.GONE else View.VISIBLE
                binding.status.text =
                    if (status.activationRequested == true) getString(R.string.your_device_protected) else getString(R.string.bam_de_bat)
                context?.let {
                    binding.imageStatus.setImageDrawable(
                        if (status.activationRequested == true) ContextCompat.getDrawable(it, R.drawable.ic_earth) else
                            ContextCompat.getDrawable(it, R.drawable.ic_earth_off)
                    )
                    binding.buttonStatus.setImageDrawable(
                        if (status.activationRequested == true) ContextCompat.getDrawable(it, R.drawable.on_button) else
                            ContextCompat.getDrawable(it, R.drawable.off_button)
                    )
                    binding.ivStatus.setImageDrawable(
                        if (status.activationRequested == true) ContextCompat.getDrawable(
                            it,
                            R.drawable.ic_shield_done_white
                        ) else
                            ContextCompat.getDrawable(it, R.drawable.ic_power_white)
                    )
                }
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("Ex syncDnsStatus: ", it) }
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