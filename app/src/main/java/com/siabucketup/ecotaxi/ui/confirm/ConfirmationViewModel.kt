package com.siabucketup.ecotaxi.ui.confirm

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siabucketup.ecotaxi.app.util.*
import com.siabucketup.ecotaxi.domain.TransformImageUseCase
import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.model.TransformImage
import com.siabucketup.ecotaxi.ui.camera.SavedPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(private val repository: TransformImageUseCase) :
    ViewModel() {

    private val _saveResult = MutableLiveData<Result<Uri>>()
    val saveResult: LiveData<Result<Uri>> = _saveResult

    fun saveDataAndThenReplaceImageWithCompressed(
        toTransformData: TransformImage,
        rectangleArea: RectangleArea,
        savedPicture: SavedPicture,
        contentResolver: ContentResolver
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.saveData(
                toTransformData,
                rectangleArea
            )
            _saveResult.postValue(result)
            val compressImage = toTransformData.bitmap.compressImage()
            savedPicture.removeFromDisk(contentResolver)
            compressImage.testSaving(savedPicture.name, contentResolver)
        }
    }

    fun startToGenerateBase64Photo(bitmap: Bitmap) {
        repository.startToGenerateBitmap(bitmap, viewModelScope)
    }
}