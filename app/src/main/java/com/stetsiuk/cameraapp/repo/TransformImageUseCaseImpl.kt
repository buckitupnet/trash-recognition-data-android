package com.stetsiuk.cameraapp.repo

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.stetsiuk.cameraapp.app.SAVING_ERROR_TAG
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.app.util.toBase64Async
import com.stetsiuk.cameraapp.domain.JsonRepository
import com.stetsiuk.cameraapp.domain.TransformImageUseCase
import com.stetsiuk.cameraapp.model.Point
import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.model.JsonImage
import com.stetsiuk.cameraapp.model.TransformImage
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

            val pointLeftUpper = Point(
                rectangleArea.leftUpX * ratioX,
                rectangleArea.leftUpY * ratioY
            )
            val pointCenter = Point(

                rectangleArea.centerX() * ratioX,
                rectangleArea.centerY() * ratioY
            )

            val image = base64Deferred.await()

            val toSaveData = JsonImage(
                imageData = image,
                pointLeftUpper = pointLeftUpper,
                pointCenter = pointCenter,
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