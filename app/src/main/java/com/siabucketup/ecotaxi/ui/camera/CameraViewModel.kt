package com.siabucketup.ecotaxi.ui.camera

import android.content.ContentValues
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.siabucketup.ecotaxi.app.PICTURE_MIME_TYPE
import com.siabucketup.ecotaxi.app.RELATIVE_PICTURE_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    fun generatePictureName(): String {
        return "${GENERATE_PICTURE_NAME}${SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())}"
    }

    fun generateContentValues(pictureName: String): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, pictureName)
            put(MediaStore.MediaColumns.MIME_TYPE, PICTURE_MIME_TYPE)
            put(MediaStore.Images.Media.RELATIVE_PATH, RELATIVE_PICTURE_PATH)
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd_HH-mm-ss"
        private const val GENERATE_PICTURE_NAME = "photo_"
    }
}