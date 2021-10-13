package vn.ncsc.visafe.ui.group.join

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityScanQrJoinGroupBinding

class ScanQRJoinGroupActivity : BaseActivity(), ResultHandler {
    lateinit var binding: ActivityScanQrJoinGroupBinding

    private var mTitle: String? = ""

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1001
        const val DATA_TITLE = "DATA_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrJoinGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            mTitle = it.getStringExtra(DATA_TITLE)
        }
        initView()
        initControl()

    }

    private fun initView() {
        binding.tvTitle.text = mTitle
    }

    private fun initControl() {
        binding.ivBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.qrCodeScanner.setResultHandler(this) // Register ourselves as a handler for scan results.
                binding.qrCodeScanner.startCamera() // Start camera on resume
            }, 300)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.qrCodeScanner.stopCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                binding.qrCodeScanner.setResultHandler(this) // Register ourselves as a handler for scan results.
                binding.qrCodeScanner.startCamera() // Start camera on resume
            } else {
                Toast.makeText(applicationContext, "Need permission", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun handleResult(rawResult: Result?) {
        Log.e("handleResult: ", "dataqr: " + rawResult.toString())
        if (rawResult != null) {
            handlerReceiverFromShortDynamicLink(rawResult.text.toString())
            return
        }
        binding.qrCodeScanner.setResultHandler(this) // Register ourselves as a handler for scan results.
        binding.qrCodeScanner.startCamera() // Start camera on resume
    }

    @SuppressLint("LongLogTag")
    private fun handlerReceiverFromShortDynamicLink(urlLink: String) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(Uri.parse(urlLink))
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    var groupId = ""
                    var groupName = ""
                    Log.e("handlerReceiverFromShortDynamicLink: ", pendingDynamicLinkData.link.toString())
                    val dataUrl = pendingDynamicLinkData.link.toString()
                    if (dataUrl.contains("group/invite/device?")) {
                        val subStringUrl =
                            dataUrl.substring(0, dataUrl.indexOf("group/invite/device?") + "group/invite/device?".length)
                        val dataString = dataUrl.replace(subStringUrl, "")
                        val items: Array<String> = dataString.split("&".toRegex()).toTypedArray()
                        try {
                            for (item in items) {
                                val parted = item.split("=".toRegex(), 2).toTypedArray()
                                if (parted.size < 2 || "" == parted[1].trim { it <= ' ' }) continue
                                val key = parted[0]
                                val value = parted[1]
                                when (key) {
                                    "groupId" -> groupId = value
                                    "groupName" -> groupName = value
                                }
                            }
                            val intent = Intent(this@ScanQRJoinGroupActivity, JoinGroupActivity::class.java)
                            intent.putExtra(JoinGroupActivity.GROUP_ID, groupId)
                            intent.putExtra(JoinGroupActivity.GROUP_NAME, groupName)
                            resultJoinGroupActivity.launch(intent)
                        } catch (e: Exception) {
                            e.message?.let { Log.e("convertData: ", it) }
                        }
                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.e("getDynamicLink:onFailure", e.toString()) }
    }

    private var resultJoinGroupActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                setResult(RESULT_OK)
                finish()
            }
        }

}