package com.stetsiuk.cameraapp.model

import android.net.Uri

data class TransformImage(
    val imageUri: Uri,
    val photoHolderWidth: Int,
    val photoHolderHeight: Int,
    val width: Int,
    val height: Int,
    val tag: String,
    val nameOfFile: String
)