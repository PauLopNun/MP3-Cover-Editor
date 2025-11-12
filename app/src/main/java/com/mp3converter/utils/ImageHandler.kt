package com.mp3converter.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File

class ImageHandler(private val context: Context) {

    /**
     * Reads an image from URI and converts to ByteArray
     */
    fun imageUriToByteArray(uri: Uri): ByteArray? = try {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Compresses image to reduce file size
     */
    fun compressImage(imageData: ByteArray, quality: Int = 85): ByteArray = try {
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        imageData
    }

    /**
     * Gets bitmap from URI for preview
     */
    fun getBitmapFromUri(uri: Uri): Bitmap? = try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Gets bitmap from ByteArray for preview
     */
    fun getBitmapFromByteArray(byteArray: ByteArray): Bitmap? = try {
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
