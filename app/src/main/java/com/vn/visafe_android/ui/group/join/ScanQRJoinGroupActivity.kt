package com.vn.visafe_android.ui.group.join

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.zxing.Result
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityScanQrJoinGroupBinding
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler

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
        Toast.makeText(this, rawResult?.text, Toast.LENGTH_LONG).show()
        val intent = Intent(this, JoinGroupActivity::class.java)
        intent.putExtra(JoinGroupActivity.SCAN_CODE, rawResult?.text)
        startActivity(intent)
        finish()
    }

}