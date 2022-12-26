package com.siabucketup.ecotaxi.app.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import android.widget.Toast
import com.siabucketup.ecotaxi.app.BITMAP_COMPRESS_FORMAT
import com.siabucketup.ecotaxi.app.BITMAP_COMPRESS_VALUE
import com.siabucketup.ecotaxi.app.RECTANGLE_AREA_TAG
import com.siabucketup.ecotaxi.app.RELATIVE_PICTURE_PATH
import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.ui.camera.SavedPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.*

fun RectangleArea.logE() {
    Log.e(RECTANGLE_AREA_TAG, this.toString())
}

fun SavedPicture.removeFromDisk(contentResolver: ContentResolver) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        contentResolver.delete(this.savedUri, null)
    } else {
        contentResolver.delete(this.savedUri, null, null)
    }
}

fun Bitmap.compressImage(compress: Int = 40): Bitmap {
    val baos = ByteArrayOutputStream()
    val options = compress
    this.compress(Bitmap.CompressFormat.JPEG, options, baos) // options%baos
    //this should be used if we would need compress image to some quality.
    //        image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 100baos
//        while (baos.toByteArray().size / 1024 > 100) { // 100kb,
//            baos.reset() // baosbaos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos) // options%baos
//            options -= 10
//        }
    val isBm = ByteArrayInputStream(
        baos.toByteArray()
    )
    return BitmapFactory.decodeStream(isBm, null, null)!!
}

fun Bitmap.testSaving(
    filename: String,
    resolver: ContentResolver
) {
    val directorySaved = RELATIVE_PICTURE_PATH
    //Output stream
    var fos: OutputStream? = null
    //For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //getting the contentResolver
        //Content resolver will process the contentvalues
        val contentValues = ContentValues().apply {
            //putting file information in content values
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, directorySaved)
        }
        //Inserting the contentValues to contentResolver and getting the Uri
        val imageUri: Uri? =
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        //Opening an outputstream with the Uri that we got
        fos = imageUri?.let { resolver.openOutputStream(it) }

    } else {
        //These for devices running on android < Q
        //So I don't think an explanation is needed here
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(directorySaved)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        //Finally writing the bitmap to the output stream that we opened
        this.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
}

fun Context.showShortToast(msg: String) {
    val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast.show()
}

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap {
    val source = ImageDecoder.createSource(contentResolver, this)
    return ImageDecoder.decodeBitmap(source)
}

fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(BITMAP_COMPRESS_FORMAT, BITMAP_COMPRESS_VALUE, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Bitmap.toBase64Async(scope: CoroutineScope) = scope.async(Dispatchers.Default) {
    return@async this@toBase64Async.toBase64()
}

fun View.captureView(window: Window, width: Int = this.width, height: Int = this.height, bitmapCallback: (Bitmap) -> Unit) {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val location = IntArray(2)
    this.getLocationInWindow(location)
    PixelCopy.request(
        window,
        Rect(location[0], location[1], location[0] + width, location[1] + height),
        bitmap,
        {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        },
        Handler(Looper.getMainLooper())
    )
}