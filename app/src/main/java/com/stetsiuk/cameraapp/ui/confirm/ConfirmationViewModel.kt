package com.stetsiuk.cameraapp.ui.confirm

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.domain.TransformImageUseCase
import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.model.TransformImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationViewModel @Inject constructor(private val repository: TransformImageUseCase) :
    ViewModel() {
    private val _saveResult = MutableLiveData<Result<Uri>>()
    val saveResult: LiveData<Result<Uri>> = _saveResult

    fun saveData(
        toTransformData: TransformImage,
        rectangleArea: RectangleArea
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.saveData(
                toTransformData,
                rectangleArea
            )
            _saveResult.postValue(result)
        }
    }

    fun startToGenerateBitmap(bitmap: Bitmap) {
        repository.startToGenerateBitmap(bitmap, viewModelScope)
    }
}