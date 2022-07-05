package com.stetsiuk.cameraapp.domain

import android.net.Uri
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.model.JsonImage

interface JsonRepository {
    fun saveData(
        toSaveData: JsonImage
    ): Result<Uri>
}