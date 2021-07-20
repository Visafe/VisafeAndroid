package vn.ncsc.visafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
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
import vn.ncsc.visafe.dns.sys.*
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.utils.setOnSingClickListener
import java.net.NetworkInterface
import java.net.SocketException


class HomeFragment : BaseFragment<FragmentHomeBinding>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var isStatusEnable: Boolean = false
    private var vibrator: Vibrator? = null
    private var aniRotateClk: Animation? = null
    private var status_button = 1
    private var count_noti_on = 0
    private var count_noti_off = 0
    private var sendNotificationWhenClickButtonOnOff = false

    companion object {
        const val REQUEST_CODE_PREPARE_VPN = 100
        fun newInstance() = HomeFragment()
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
        binding.buttonActive.setOnSingClickListener {
            sendNotificationWhenClickButtonOnOff = true
            if (VERSION.SDK_INT >= 26) {
                vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator?.vibrate(200)
            }
            if (status_button == 0) {
                status_button = 1
                stopDnsVpnService()
            } else {
                status_button = 0
                binding.roundImage.visibility = View.INVISIBLE
                prepareAndStartDnsVpn()
            }
        }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (PersistentState.URL_KEY == key) {
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
        return VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
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

    // Sets the UI DNS status on/off.
    private fun syncDnsStatus() {
        try {
            val status = VpnController.instance.getState(context)
            status.let {
                binding.roundImage.startAnimation(aniRotateClk)
                // Change status and explanation text
                var privateDnsMode: PrivateDnsMode? = PrivateDnsMode.NONE
                if (it.activationRequested == true) {
                    status_button = 0
                    when {
                        status.connectionState == null -> {
                            binding.ivStatus.visibility = View.GONE
                            binding.tvTap.visibility = View.GONE
                            binding.status.text = getString(R.string.status_waiting)
                            binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                            binding.buttonStatus.setImageResource(R.drawable.off_button)
                        }
                        status.connectionState === ViSafeVpnService.State.NEW -> {
                            binding.ivStatus.visibility = View.GONE
                            binding.tvTap.visibility = View.GONE
                            binding.status.text = getString(R.string.status_starting)
                            binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                            binding.buttonStatus.setImageResource(R.drawable.off_button)
                        }
                        status.connectionState === ViSafeVpnService.State.WORKING -> {
                            binding.ivStatus.setImageDrawable(
                                context?.let { it1 ->
                                    ContextCompat.getDrawable(
                                        it1, R.drawable.ic_shield_done_white
                                    )
                                }
                            )
                            binding.ivStatus.visibility = View.VISIBLE
                            binding.tvTap.visibility = View.GONE
                            binding.status.text = getString(R.string.status_protected)
                            if (count_noti_on == 0 && sendNotificationWhenClickButtonOnOff) {
                                sendNotification("Đã kích hoạt chế độ bảo vệ!", "Chế độ chống lừa đảo, mã độc, tấn công mạng đã được kích hoạt!")
                                count_noti_on++
                                count_noti_off = 0
                                sendNotificationWhenClickButtonOnOff = false
                            }
                            binding.imageStatus.setImageResource(R.drawable.ic_earth)
                            binding.buttonStatus.setImageResource(R.drawable.on_button)
                            binding.roundImage.clearAnimation()
                        }
                        else -> {
                            binding.ivStatus.visibility = View.GONE
                            binding.tvTap.visibility = View.GONE
                            binding.status.text = getString(R.string.status_failing)
                            binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                            binding.buttonStatus.setImageResource(R.drawable.off_button)
                        }
                    }
                } else if (isAnotherVpnActive()) {
                    binding.ivStatus.setImageDrawable(
                        context?.let { it1 ->
                            ContextCompat.getDrawable(
                                it1, R.drawable.ic_power_white
                            )
                        }
                    )
                    binding.ivStatus.visibility = View.VISIBLE
                    binding.tvTap.visibility = View.VISIBLE
                    binding.status.text = getString(R.string.bam_de_bat)
                    binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                    binding.buttonStatus.setImageResource(R.drawable.off_button)
                    binding.roundImage.clearAnimation()
                    if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff) {
                        sendNotification("Bạn đã tắt chế độ bảo vệ!", "Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng")
                        count_noti_off++
                        count_noti_on = 0
                        sendNotificationWhenClickButtonOnOff = false
                    }
                } else {
                    privateDnsMode = getPrivateDnsMode()
                    if (privateDnsMode == PrivateDnsMode.STRICT) {
                        binding.ivStatus.visibility = View.GONE
                        binding.tvTap.visibility = View.GONE
                        binding.status.text = getString(R.string.status_strict)
                        binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                        binding.buttonStatus.setImageResource(R.drawable.off_button)
                        binding.roundImage.clearAnimation()
                    } else if (privateDnsMode == PrivateDnsMode.UPGRADED) {
                        binding.ivStatus.setImageDrawable(
                            context?.let { it1 ->
                                ContextCompat.getDrawable(
                                    it1, R.drawable.ic_power_white
                                )
                            }
                        )
                        binding.ivStatus.visibility = View.VISIBLE
                        binding.tvTap.visibility = View.VISIBLE
                        binding.status.text = getString(R.string.bam_de_bat)
                        binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                        binding.buttonStatus.setImageResource(R.drawable.off_button)
                        binding.roundImage.clearAnimation()
                        if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff) {
                            sendNotification("Bạn đã tắt chế độ bảo vệ!", "Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng")
                            count_noti_off++
                            count_noti_on = 0
                            sendNotificationWhenClickButtonOnOff = false
                        }
                    } else {
                        binding.tvTap.visibility = View.VISIBLE
                        binding.status.text = getString(R.string.bam_de_bat)
                        binding.ivStatus.setImageDrawable(
                            context?.let { it1 ->
                                ContextCompat.getDrawable(
                                    it1, R.drawable.ic_power_white
                                )
                            }
                        )
                        binding.ivStatus.visibility = View.VISIBLE
                        binding.status.text = getString(R.string.bam_de_bat)
                        binding.imageStatus.setImageResource(R.drawable.ic_earth_off)
                        binding.buttonStatus.setImageResource(R.drawable.off_button)
                        binding.roundImage.clearAnimation()
                        if (count_noti_off == 0 && sendNotificationWhenClickButtonOnOff) {
                            sendNotification("Bạn đã tắt chế độ bảo vệ!", "Thiết bị của bạn có thể bị ảnh hưởng bởi tấn công mạng")
                            count_noti_off++
                            count_noti_on = 0
                            sendNotificationWhenClickButtonOnOff = false
                        }
                    }
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

    private fun isAnotherVpnActive(): Boolean {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: // It's not clear when this can happen, but it has occurred for at least one user.
                return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
        // For pre-M versions, return true if there's any network whose name looks like a VPN.
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val name = networkInterface.name
                if (networkInterface.isUp && name != null &&
                    (name.startsWith("tun") || name.startsWith("pptp") || name.startsWith("l2tp"))
                ) {
                    return true
                }
            }
        } catch (e: SocketException) {
            e.message?.let { Log.e("isAnotherVpnActive: ", it) }
        }
        return false
    }

    private fun sendNotification(title: String, body: String) {
        var builder: Notification.Builder
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.warning_channel_name)
            val description = getString(R.string.warning_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance)
            channel.description = description
            channel.enableVibration(false)
            channel.vibrationPattern = null
            notificationManager.createNotificationChannel(channel)
            builder = Notification.Builder(requireContext(), getString(R.string.notification_channel_id))
        } else {
            builder = Notification.Builder(requireContext())
            builder.setVibrate(null)
            // Deprecated in API 26.
            builder = builder.setPriority(Notification.PRIORITY_MAX)
        }
        val mainActivityIntent = PendingIntent.getActivity(
            context, 0, Intent(requireContext(), MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentInfo("Hello")
            .setLights(Color.RED, 1000, 100)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_logo_noti)
            .setNumber(++MyFirebaseService.numMessages)
            .setStyle(
                Notification.BigTextStyle()
                    .bigText(body)
            )
            .setFullScreenIntent(mainActivityIntent, true)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_ERROR)
        }
        notificationManager.notify(0, builder.notification)
    }

    private enum class PrivateDnsMode {
        NONE,  // The setting is "Off" or "Opportunistic", and the DNS connection is not using TLS.
        UPGRADED,  // The setting is "Opportunistic", and the DNS connection has upgraded to TLS.
        STRICT // The setting is "Strict".
    }

    private fun getLinkProperties(): LinkProperties? {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            return null
        }
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        return connectivityManager.getLinkProperties(activeNetwork)
    }

    private fun getPrivateDnsMode(): PrivateDnsMode? {
        if (VERSION.SDK_INT < VERSION_CODES.P) {
            // Private DNS was introduced in P.
            return PrivateDnsMode.NONE
        }
        val linkProperties: LinkProperties = getLinkProperties() ?: return PrivateDnsMode.NONE
        if (linkProperties.privateDnsServerName != null) {
            return PrivateDnsMode.STRICT
        }
        return if (linkProperties.isPrivateDnsActive) {
            PrivateDnsMode.UPGRADED
        } else PrivateDnsMode.NONE
    }
}