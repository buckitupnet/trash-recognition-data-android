package com.stetsiuk.cameraapp.repo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.underscore.U
import com.stetsiuk.cameraapp.MainActivity
import com.stetsiuk.cameraapp.app.*
import com.stetsiuk.cameraapp.app.util.Result
import com.stetsiuk.cameraapp.domain.JsonRepository
import com.stetsiuk.cameraapp.model.Flags
import com.stetsiuk.cameraapp.model.Json
import com.stetsiuk.cameraapp.model.Shape
import com.stetsiuk.cameraapp.model.JsonImage
import java.io.FileOutputStream
import javax.inject.Inject

class JsonRepositoryImpl @Inject constructor(private val appContext: Context) : JsonRepository {

    override fun saveData(
        toSaveData: JsonImage
    ): Result<Uri> {
        return try {
            val json = formJsonClass(
                toSaveData
            )
            val contentValues = generateContentValues(toSaveData.nameOfFile)
            saveJson(json, contentValues)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun formJsonClass(
        toSaveData: JsonImage
    ): String {
        val points = ArrayList<ArrayList<Double>>()
        points.add(arrayListOf(toSaveData.pointLeftUpper.x, toSaveData.pointLeftUpper.y))
        points.add(arrayListOf(toSaveData.pointCenter.x, toSaveData.pointCenter.y))

        val shape = Shape(
            flags = Flags(),
            group_id = null,
            label = toSaveData.tag,
            points = points,
            shape_type = RECTANGLE
        )

        val jsonClass = Json(
            flags = Flags(),
            imageData = toSaveData.imageData,
            imageHeight = toSaveData.imageHeight,
            imageWidth = toSaveData.imageWidth,
            shapes = listOf(shape),
            version = JSON_VERSION,
            imagePath = getPicturePath(toSaveData.imageUri)
        )

        val objectMapper = ObjectMapper().writerWithDefaultPrettyPrinter()
        val writeValueAsString: String = objectMapper.writeValueAsString(jsonClass)
        return U.formatJson(writeValueAsString)
    }

    private fun saveJson(json: String, contentValues: ContentValues): Result<Uri> {
        val uri = appContext.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )
        uri?.let { return alterDocument(it, json) }
        return Result.Error(Exception("Json uri is null"))
    }

    private fun alterDocument(uri: Uri, json: String): Result<Uri> {
        appContext.contentResolver.openFileDescriptor(uri, OPEN_MODE_WRITING)?.use { it ->
            FileOutputStream(it.fileDescriptor).use {
                it.write(
                    json.toByteArray()
                )
            }
        }
        return Result.Success(uri)
    }

    private fun getPicturePath(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Downloads.DATA)
            cursor = appContext.contentResolver.query(contentUri, proj, null, null, null)
            cursor?.let {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
                cursor.moveToFirst()
                return cursor.getString(columnIndex)
            }
        } catch (ex: Exception) {
            Log.e(MainActivity.TAG, "Getting picture path exception", ex)
        } finally {
            cursor?.close()
        }
        return String()
    }

    private fun generateContentValues(name: String): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, JSON_FILE_MIME_TYPE)
            put(MediaStore.MediaColumns.RELATIVE_PATH, RELATIVE_PICTURE_PATH)
        }
    }
}