package com.siabucketup.ecotaxi.repo

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.siabucketup.ecotaxi.app.SAVING_ERROR_TAG
import com.siabucketup.ecotaxi.app.util.Result
import com.siabucketup.ecotaxi.app.util.toBase64Async
import com.siabucketup.ecotaxi.domain.JsonRepository
import com.siabucketup.ecotaxi.domain.TransformImageUseCase
import com.siabucketup.ecotaxi.model.Point
import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.model.JsonImage
import com.siabucketup.ecotaxi.model.TransformImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

class TransformImageUseCaseImpl(private val saveDataImpl: JsonRepository) :
    TransformImageUseCase {

    private lateinit var base64Deferred: Deferred<String>

    override fun startToGenerateBitmap(bitmap: Bitmap, scope: CoroutineScope) {
        base64Deferred = bitmap.toBase64Async(scope)
    }

    override suspend fun saveData(
        toTransformData: TransformImage,
        rectangleArea: RectangleArea
        ): Result<Uri> {
        return try {
            val ratioX =
                toTransformData.width.toFloat() / toTransformData.photoHolderWidth.toFloat()
            val ratioY =
                toTransformData.height.toFloat() / toTransformData.photoHolderHeight.toFloat()

            val pointLeftTop = Point(
                rectangleArea.leftUpX * ratioX,
                rectangleArea.leftUpY * ratioY
            )
            val pointRightBottom = Point(
                rectangleArea.rightDownX * ratioX,
                rectangleArea.rightDownY * ratioY
            )

            val image = base64Deferred.await()

            val toSaveData = JsonImage(
                imageData = image,
                pointLeftTop = pointLeftTop,
                pointRightBottom = pointRightBottom,
                tag = toTransformData.tag,
                imageWidth = toTransformData.width,
                imageHeight = toTransformData.height,
                imageUri = toTransformData.imageUri,
                nameOfFile = toTransformData.nameOfFile
            )
            saveDataImpl.saveData(
                toSaveData
            )
        } catch (e: Exception) {
            Log.e(SAVING_ERROR_TAG, e.message.toString())
            Result.Error(e)
        }
    }
}