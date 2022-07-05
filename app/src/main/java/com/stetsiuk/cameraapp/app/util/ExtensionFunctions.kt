package com.stetsiuk.cameraapp.app.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.stetsiuk.cameraapp.app.BITMAP_COMPRESS_FORMAT
import com.stetsiuk.cameraapp.app.BITMAP_COMPRESS_VALUE
import com.stetsiuk.cameraapp.app.RECTANGLE_AREA_TAG
import com.stetsiuk.cameraapp.model.RectangleArea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream

fun RectangleArea.logE() {
    Log.e(RECTANGLE_AREA_TAG, this.toString())
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