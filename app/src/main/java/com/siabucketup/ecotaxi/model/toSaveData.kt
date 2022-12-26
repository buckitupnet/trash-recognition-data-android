package com.siabucketup.ecotaxi.model

import android.net.Uri

data class JsonImage(
    val imageData: String,
    val pointLeftTop: Point,
    val pointRightBottom: Point,
    val tag: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageUri: Uri,
    val nameOfFile: String
)
