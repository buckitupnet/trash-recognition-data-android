package com.siabucketup.ecotaxi.domain

import android.graphics.Bitmap
import android.net.Uri
import com.siabucketup.ecotaxi.app.util.Result
import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.model.TransformImage
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