package vn.ncsc.visafe.ui.group.detail.device

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import vn.ncsc.visafe.BuildConfig
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityAddDeviceBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.ui.group.join.JoinGroupActivity
import vn.ncsc.visafe.utils.saveImageToGallery
import vn.ncsc.visafe.utils.setOnSingClickListener

class AddDeviceActivity : BaseActivity() {
    lateinit var binding: ActivityAddDeviceBinding

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }

    private var groupData: GroupData? = null
    private var qrBitmapMerge: Bitmap? = null
    private var dataQr: String? = ""

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
            binding.tvNameGroup.isSelected = true
            binding.tvLinkShare.text = it.linkInviteDevice.toString()
            val logoBitmap = BitmapFactory.decodeResource(
                applicationContext.resources,
                R.drawable.ic_loge_qr
            )
            dataQr = it.linkInviteDevice
            qRCodeGen(dataQr, logoBitmap)
        }
    }

    private fun getDeepLink(groupData: GroupData) {
        showProgressDialog()
        val builderLink =
            NetworkClient.URL_ROOT + "group/invite/device?groupId=${groupData.groupid}&groupName=${groupData.name}"
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(builderLink))
            .setDomainUriPrefix("https://visafencsc.page.link/")
            .setIosParameters(DynamicLink.IosParameters.Builder("vn.visafe").setAppStoreId("1564635388").build())
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(BuildConfig.APPLICATION_ID).apply {
                minimumVersion = BuildConfig.VERSION_CODE
            }.build())
            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
            .addOnCompleteListener { task: Task<ShortDynamicLink> ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result.shortLink
                    if (shortLink != null) {
                        Log.e("getDeepLink: ", shortLink.toString() + "| " + task.result.previewLink)
                        binding.tvLinkShare.text = shortLink.toString()
                        dismissProgress()
                    }
                }
            }
            .addOnFailureListener { e: Exception -> Log.e("addOnFailureListener: ", e.message!!) }
    }

    private fun initControl() {
        binding.ivBack.setOnSingClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.tvLinkShare.setOnSingClickListener {
            copyToClipboard(binding.tvLinkShare.text.toString().replace(" ".toRegex(), ""))
        }
        binding.btnShareLink.setOnSingClickListener {
            shareLink(binding.tvLinkShare.text.toString().replace(" ".toRegex(), ""))
        }
        binding.btnSaveQr.setOnSingClickListener {
            checkPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), object : OnPermissionGranted {
                override fun onPermission() {
                    qrBitmapMerge?.let { it1 -> saveBitmapToGallery(it1) }
                }

                override fun onPermissionDenied() {
                    showToast("Bạn cần có quyền truy cập bộ nhớ")
                }
            })
        }
        binding.tvAddDevice.setOnSingClickListener {
            val intent = Intent(this@AddDeviceActivity, JoinGroupActivity::class.java)
            intent.putExtra(JoinGroupActivity.GROUP_ID, groupData?.groupid)
            intent.putExtra(JoinGroupActivity.GROUP_NAME, groupData?.name)
            startActivity(intent)
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        saveImageToGallery(bitmap, applicationContext)
        showToast("Lưu ảnh thành công")
    }

    // draw mã QR
    private fun qRCodeGen(textQR: String?, myLogo: Bitmap?) {
        try {
            val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var smallerDimension = width.coerceAtMost(height)
            smallerDimension *= 4
            val qrgEncoder = QRGEncoder(textQR, null, QRGContents.Type.TEXT, smallerDimension)
            val bitmapQR = qrgEncoder.encodeAsBitmap()
            qrBitmapMerge = mergeBitmaps(bitmapQR, myLogo)
            binding.ivQr.setImageBitmap(qrBitmapMerge)
        } catch (e: Exception) {
            Log.e("Lỗi mã QR", e.toString())
        }
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
//        bitmapCrop = addWhiteBorder(bitmapCrop)
        return output
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
}