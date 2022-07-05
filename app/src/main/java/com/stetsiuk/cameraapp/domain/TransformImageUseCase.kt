package com.stetsiuk.cameraapp.domain

import android.graphics.Bitmap
import android.net.Uri
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.model.TransformImage
import kotlinx.coroutines.CoroutineScope

interface TransformImageUseCase {
    fun startToGenerateBitmap(
        bitmap: Bitmap,
        scope: CoroutineScope
    )

    suspend fun saveData(
        toTransformData: TransformImage,
        rectangleArea: RectangleArea
    ) : Result<Uri>
}