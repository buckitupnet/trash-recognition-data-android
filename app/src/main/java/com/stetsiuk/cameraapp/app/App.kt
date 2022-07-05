package com.stetsiuk.cameraapp.app

import android.app.Application
import android.graphics.Bitmap
import dagger.hilt.android.HiltAndroidApp

const val SAVING_ERROR_TAG = "Saving error"
const val RECTANGLE = "RECTANGLE"
const val OPEN_MODE_WRITING = "w"
const val RECTANGLE_AREA_TAG = "RectangleAreaTag"
const val BROWSER_LINK = "http://eco-taxi.ge/"
const val RELATIVE_PICTURE_PATH = "Download/eco-taxi"
const val PICTURE_MIME_TYPE = "image/jpeg"
const val JSON_FILE_MIME_TYPE = "application/json"
const val JSON_VERSION = "4.5.12"
const val BITMAP_COMPRESS_VALUE = 100
val BITMAP_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG
val TAGS = arrayListOf(
    "01 PET bottles",
    "01 PET other",
    "02 HDPE",
    "05 PP",
    "ALUM cans",
    "TIN",
    "GLASS",
    "PAP paper",
    "PAP cardboard",
    "Batteries",
    "Plastic cards",
    "Tooth brushes",
    "Electronics",
)


@HiltAndroidApp
class App : Application()