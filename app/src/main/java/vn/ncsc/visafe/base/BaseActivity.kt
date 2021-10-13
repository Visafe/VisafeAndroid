package vn.ncsc.visafe.base

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.BaseController
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.dns.net.setting.RandomString
import vn.ncsc.visafe.model.request.SendTokenRequest
import vn.ncsc.visafe.model.response.DeviceIdResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.widget.ProgressDialogFragment
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.ArrayList


open class BaseActivity : AppCompatActivity(), BaseController {

    private var progressDialog: ProgressDialogFragment? = null

    open fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    fun showProgressDialog() {
        val prevFragment = supportFragmentManager.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            return
        }
        if (progressDialog == null) {
            progressDialog = ProgressDialogFragment()
            progressDialog?.show(supportFragmentManager)
        }
    }

    fun dismissProgress() {
        val prevFragment = supportFragmentManager.findFragmentByTag(ProgressDialogFragment.TAG)
        if (prevFragment != null) {
            (prevFragment as ProgressDialogFragment).dismissAllowingStateLoss()
        }
        if (progressDialog != null) {
            progressDialog?.dismissAllowingStateLoss()
            progressDialog = null
        }
    }

    override fun onTimeOutSession() {
        dismissProgress()
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setCancelable(false)
            setTitle(getString(R.string.thong_bao))
            setMessage(getString(R.string.session_timed_out_content))
            setPositiveButton(
                getString(R.string.dong_y)
            ) { _, _ -> logOut() }
            show()
        }

    }

    override fun onError(baseResponse: BaseResponse) {
        dismissProgress()
        baseResponse.msg?.let { showAlert(it) }
    }

    fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(getString(R.string.thong_bao))
            setMessage(msg)
            setPositiveButton(
                getString(R.string.dong_y)
            ) { _, _ -> finish() }
            show()
        }
    }

    fun copyToClipboard(text: String) {
        if (text.isNotEmpty()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", text)
            clipboard.setPrimaryClip(clip)
            showToast("Copy success")
        }
    }

    fun shareLink(text: String) {
        val shareLink = Intent(Intent.ACTION_SEND)
        shareLink.type = "text/plain"
        shareLink.putExtra(Intent.EXTRA_TEXT, text)
        shareLink.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareLink, text))
    }

    fun logOut() {
        SharePreferenceKeyHelper.getInstance(application).clearAllData()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun handlerFragment(fragment: Fragment, rootId: Int, tag: String) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped: Boolean = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.slide_in_left_1, R.anim.slide_out_left_1,
                R.anim.slide_out_right_1, R.anim.slide_in_right_1
            )
            ft.replace(rootId, fragment)
            ft.addToBackStack(tag)
            ft.commit()
        }
    }

    fun isLogin(): Boolean {
        return SharePreferenceKeyHelper.getInstance(application).isLogin()
    }

    fun getWifiName(): String {
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val networkList = wifi.scanResults
        //get current connected SSID for comparison to ScanResult
        val wi = wifi.connectionInfo
        return wi.ssid
    }

    fun isWPA2(): Boolean {
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val networkList = wifi.scanResults
        //get current connected SSID for comparison to ScanResult
        val wi = wifi.connectionInfo
        val currentSSID = wi.bssid

        if (networkList != null) {
            for (network in networkList) {
                //check if current connected SSID
                if (currentSSID == network.BSSID) {
                    //get capabilities of current connection
                    val capabilities = network.capabilities
                    Log.e("isWPA2", wi.ssid + " " + network.SSID.toString() + " capabilities : " + capabilities)
                    when {
                        capabilities.contains("WPA2") -> {
                            return true
                        }
                        capabilities.contains("WPA") -> {
                            return false
                        }
                        capabilities.contains("WEP") -> {
                            return false
                        }
                    }
                }
            }
        }
        return false
    }

    fun checkPermissionWifi(): Boolean {
        val permissionsList: MutableList<String> = ArrayList()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissionsList.size > 0) {
            ActivityCompat.requestPermissions(
                this, permissionsList.toTypedArray(),
                MainActivity.MY_PERMISSIONS_ACCESS_COARSE_LOCATION
            )
            return false
        }
        return true
    }

    open fun getPhoneName(): String? {
        val myDevice = BluetoothAdapter.getDefaultAdapter()
        return myDevice.name + "(" + getDeviceName() + ")"
    }

    fun getDeviceName(): String {
        return Build.MANUFACTURER + " " + Build.MODEL
    }

    fun getDeviceOwnerAndDeviceName(): String {
        return (Build.MANUFACTURER
                + " " + Build.MODEL + " 2: " + Build.VERSION.RELEASE
                + " " + VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
    }

    fun getMacAddress(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("getMacAddress: ", it) }
        }
        return "02:00:00:00:00:00"
    }

    fun getIpAddress(): String {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress
        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        val ipByteArray: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
        return try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Log.e("WIFIIP", "Unable to get host address.")
            ""
        }
    }

    fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "$sdkVersion ($release)"
    }

    fun getApiVersion(): Int {
        return Build.VERSION.SDK_INT
    }

    fun isApiVersionGraterOrEqual(): Boolean {
        return Build.VERSION.SDK_INT >= getApiVersion()
    }

    //check lock
    fun doesDeviceHaveSecuritySetup(context: Context): Boolean {
        return isPatternSet(context) || isPassOrPinSet(context)
    }

    fun isAvailableFingerprint(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
    }

    private fun isPatternSet(context: Context): Boolean {
        val cr = context.contentResolver
        return try {
            val lockPatternEnable: Int = Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED)
            lockPatternEnable == 1
        } catch (e: Settings.SettingNotFoundException) {
            e.message?.let { Log.e("isPatternSet", it) }
            false
        }
    }

    private fun isPassOrPinSet(context: Context): Boolean {
        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager //api 16+
        return keyguardManager.isKeyguardSecure
    }

    fun getNewTokenFCM() {
        if (SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.TOKEN_FCM).isEmpty()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("getNewTokenFCM", "Fetching FCM registration token failed ", task.exception)
                    return@OnCompleteListener
                }
                //get new FCM registration token
                val token = task.result
                token?.let {
                    SharePreferenceKeyHelper.getInstance(application).putString(PreferenceKey.TOKEN_FCM, it)
                }
                Log.e("getNewTokenFCM", "token new $token")
                requestSendToken()
            })
        } else {
            requestSendToken()
        }
    }

    private fun requestSendToken() {
        val tokenFCM = SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.TOKEN_FCM)
        val deviceId = SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.DEVICE_ID)
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext)
            .doSendToken(SendTokenRequest(token = tokenFCM, deviceId = deviceId))
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.STATUS_SEND_TOKEN, true)
                } else {
                    SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.STATUS_SEND_TOKEN, false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.STATUS_SEND_TOKEN, false)
            }
        }))
    }

    fun checkPermission(permissions: Array<String>, onPermissionGranted: OnPermissionGranted) {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onPermissionGranted.onPermission()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    onPermissionGranted.onPermissionDenied()
                }

            })
            .setPermissions(*permissions)
            .check()
    }

    interface OnPermissionGranted {
        fun onPermission()
        fun onPermissionDenied()
    }
}