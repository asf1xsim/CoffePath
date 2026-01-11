package com.example.coffepath.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PhotoUtils {
    private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private const val PHOTO_EXTENSION = ".jpg"
    private const val PHOTO_QUALITY = 90

    fun createImageUri(context: Context, fileName: String? = null): Uri? {
        val name = fileName ?: "IMG_${
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
        }$PHOTO_EXTENSION"

        return try {
            val storageDir = File(context.cacheDir, "images")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val imageFile = File(storageDir, name)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String? = null): Uri? {
        return try {
            val name = fileName ?: "IMG_${
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis())
            }$PHOTO_EXTENSION"
            val storageDir = File(context.filesDir, "images")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val imageFile = File(storageDir, name)

            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, outputStream)
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun loadBitmapFromUri(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
            val scale = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxWidth,
                maxHeight
            )
            val decodedOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, decodedOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var inSampleSize = 1
        if (height > maxHeight || width > maxWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= maxHeight &&
                (halfWidth / inSampleSize) >= maxWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun correctImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                }

                if (!matrix.isIdentity) {
                    Bitmap.createBitmap(
                        bitmap,
                        0, 0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                } else {
                    bitmap
                }
            } ?: bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    fun compressBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {
            return bitmap
        }
        val ratio = (bitmap.width.toFloat() / bitmap.height.toFloat())
        val (newWidth, newHeight) = if (ratio > 1) {
            maxWidth to (maxWidth / ratio).toInt()
        } else {
            (maxHeight * ratio).toInt() to maxHeight
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun cleanupOldImages(context: Context, daysOld: Int = 7) {
        try {
            val storageDir = File(context.cacheDir, "images")
            if (!storageDir.exists()) return

            val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)

            storageDir.listFiles()?.forEach { file ->
                if (file.lastModified() < cutoffTime) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}