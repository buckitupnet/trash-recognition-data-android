package com.siabucketup.ecotaxi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.siabucketup.ecotaxi.ui.camera.SavedPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _picture = MutableLiveData<SavedPicture>()
    val picture: LiveData<SavedPicture> get() = _picture

    private val _maxHeight = MutableLiveData<Int>()
    val maxHeight: LiveData<Int> get() = _maxHeight

    fun setMaxHeight(int: Int){
        _maxHeight.value = int
    }

    fun setSavedPhotoUri(savedPicture: SavedPicture) {
        _picture.value = savedPicture
    }

    fun setXY(xTouch: Double, yTouch: Double) {
        _touchX.value = xTouch
        _touchY.value = yTouch
    }

    private val _touchX = MutableLiveData(DEFAULT_VALUE)
    val touchX: LiveData<Double> get() = _touchX

    private val _touchY = MutableLiveData(DEFAULT_VALUE)
    val touchY: LiveData<Double> get() = _touchY

    companion object {
        const val DEFAULT_VALUE = 300.0
    }
}