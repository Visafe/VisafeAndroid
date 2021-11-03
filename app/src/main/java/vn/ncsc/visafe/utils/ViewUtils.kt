package vn.ncsc.visafe.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import vn.ncsc.visafe.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun setSafeClickListener(view: View?, listener: View.OnClickListener) {
    view?.let {
        RxView.clicks(it)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { listener.onClick(view) }
    }
}

fun saveImageToGallery(bitmap: Bitmap, context: Context): Uri? {
    try {
        val uri: Uri?
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + context.getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + context.getString(R.string.app_name))
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            val values = contentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            uri = Uri.fromFile(file)
        }

        return uri
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Uri.EMPTY
}

private fun contentValues(): ContentValues {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
    return values
}

private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun dpToPx(context: Context, dp: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val densityDpi = (displayMetrics.density * 160f).toInt()
    return (dp * (densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat().roundToInt()
}

fun dpToPxByYdpi(context: Context, dp: Int): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val densityDpi = displayMetrics.densityDpi
    return (dp * (densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat().roundToInt()
}

fun dpToPxByXdpi(context: Context, dp: Int): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun pxToDp(context: Context, px: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val densityDpi = (displayMetrics.density * 160f).toInt()
    return (px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat().roundToInt()
}