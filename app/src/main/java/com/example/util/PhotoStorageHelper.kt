package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object PhotoStorageHelper {
    /**
     * Copies an image from a selected content Uri into the app's persistent internal storage
     * so that it can be loaded reliably and safely without requiring runtime storage permissions.
     */
    fun saveImageFromUri(context: Context, uri: Uri, prefix: String = "photo"): String? {
        return try {
            val photosDir = File(context.filesDir, "patient_photos").apply {
                if (!exists()) mkdirs()
            }
            val destinationFile = File(photosDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a captured Bitmap directly from the camera preview into the app's persistent internal storage.
     */
    fun saveBitmap(context: Context, bitmap: Bitmap, prefix: String = "photo"): String? {
        return try {
            val photosDir = File(context.filesDir, "patient_photos").apply {
                if (!exists()) mkdirs()
            }
            val destinationFile = File(photosDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(destinationFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
