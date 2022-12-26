package com.siabucketup.ecotaxi.domain

import android.net.Uri
import com.siabucketup.ecotaxi.app.util.Result
import com.siabucketup.ecotaxi.model.JsonImage

interface JsonRepository {
    fun saveData(
        toSaveData: JsonImage
    ): Result<Uri>
}