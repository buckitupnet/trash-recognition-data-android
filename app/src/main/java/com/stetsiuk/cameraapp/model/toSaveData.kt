package com.stetsiuk.cameraapp.model

import android.net.Uri

data class JsonImage(
    val imageData: String,
    val pointLeftUpper: Point,
    val pointCenter: Point,
    val tag: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageUri: Uri,
    val nameOfFile: String
)
