package vn.ncsc.visafe.ui.group.detail.device

import android.Manifest
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityAddDeviceBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.utils.saveImageToGallery
import vn.ncsc.visafe.utils.setOnSingClickListener

class AddDeviceActivity : BaseActivity() {
    lateinit var binding: ActivityAddDeviceBinding

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }

    private var groupData: GroupData? = null
    private var qrBitmapMerge: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            intent?.let {
                groupData = it.getParcelableExtra(KEY_DATA)
            }
        }
        initView()
        initControl()
    }

    private fun initView() {
        groupData?.let {
            binding.tvNameGroup.text = it.name
            val logoBitmap = BitmapFactory.decodeResource(
                applicationContext.resources,
                R.drawable.ic_logo_text
            )
            val myLogo = getCroppedBitmap(logoBitmap)
            it.groupid?.let { data -> qRCodeGen(data, myLogo) }
        }
    }

    private fun initControl() {
        binding.ivBack.setOnSingClickListener { finish() }
        binding.tvLinkShare.setOnSingClickListener {
            copyToClipboard(binding.tvLinkShare.text.toString().replace(" ".toRegex(), ""))
        }
        binding.btnShareLink.setOnSingClickListener {
            shareLink(binding.tvLinkShare.text.toString().replace(" ".toRegex(), ""))
        }
        binding.btnSaveQr.setOnSingClickListener {
            checkPermission(object : OnPermissionGranted {
                override fun onPermission() {
                    qrBitmapMerge?.let { it1 -> saveBitmapToGallery(it1) }
                }
            })
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        saveImageToGallery(bitmap, applicationContext)
        showToast("Lưu ảnh thành công")
    }

    // draw mã QR
    private fun qRCodeGen(textQR: String, myLogo: Bitmap?) {
        try {
            val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var smallerDimension = width.coerceAtMost(height)
            smallerDimension = smallerDimension * 3 / 4
            val qrgEncoder = QRGEncoder(textQR, null, QRGContents.Type.TEXT, smallerDimension)
            val bitmapQR = qrgEncoder.encodeAsBitmap()
            qrBitmapMerge = mergeBitmaps(bitmapQR, myLogo)
            binding.ivQr.setImageBitmap(qrBitmapMerge)
        } catch (e: Exception) {
            Log.e("Lỗi mã QR", e.toString())
        }
    }

    private fun checkPermission(onPermissionGranted: OnPermissionGranted) {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onPermissionGranted.onPermission()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast("Bạn cần có quyền truy cập bộ nhớ")
                }

            })
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    // merge mã QR và logo
    private fun mergeBitmaps(qrCode: Bitmap, logo: Bitmap?): Bitmap {
        val combined = Bitmap.createBitmap(qrCode.width, qrCode.height, qrCode.config)
        val canvas = Canvas(combined)
        val canvasWidth = canvas.width
        val canvasHeight = canvas.height
        canvas.drawBitmap(qrCode, Matrix(), null)
        val resizeLogo = logo?.let { Bitmap.createScaledBitmap(it, canvasWidth / 5, canvasHeight / 5, true) }
        resizeLogo?.let { resizeLogo ->
            val centreX = (canvasWidth - resizeLogo.width) / 2
            val centreY = (canvasHeight - resizeLogo.height) / 2
            canvas.drawBitmap(resizeLogo, centreX.toFloat(), centreY.toFloat(), null)
        }
        return combined
    }

    // crop ảnh sang hình tròn
    private fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        paint.flags = Paint.ANTI_ALIAS_FLAG
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
            bitmap.width / 2.toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        var bitmapCrop = Bitmap.createScaledBitmap(output, 60, 60, false)
        bitmapCrop = addWhiteBorder(bitmapCrop)
        return bitmapCrop
    }

    // thêm viền trắng cho logo
    private fun addWhiteBorder(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val radius = (h / 2).coerceAtMost(w / 2)
        val output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888)
        val p = Paint()
        p.isAntiAlias = true
        val c = Canvas(output)
        c.drawARGB(0, 0, 0, 0)
        p.style = Paint.Style.FILL
        c.drawCircle(w / 2 + 4.toFloat(), h / 2 + 4.toFloat(), radius.toFloat(), p)
        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        c.drawBitmap(bitmap, 4f, 4f, p)
        p.xfermode = null
        p.style = Paint.Style.STROKE
        p.color = Color.WHITE
        p.strokeWidth = 3f
        c.drawCircle(w / 2 + 4.toFloat(), h / 2 + 4.toFloat(), radius.toFloat(), p)
        return output
    }

    internal interface OnPermissionGranted {
        fun onPermission()
    }
}