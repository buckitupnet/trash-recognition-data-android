package com.siabucketup.ecotaxi.model

import android.graphics.Bitmap
import android.net.Uri

data class TransformImage(
    val imageUri: Uri,
    val bitmap: Bitmap,
    val photoHolderWidth: Int,
    val photoHolderHeight: Int,
    val width: Int,
    val height: Int,
    val tag: String,
    val nameOfFile: String
)