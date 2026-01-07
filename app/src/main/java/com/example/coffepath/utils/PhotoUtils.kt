package com.example.coffepath.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PhotoUtils {
    private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private const val PHOTO_EXTENSION = ".jpg"
    private const val PHOTO_QUALITY = 90

    fun createImageUri(context: Context, fileName: String? = null): Uri? {
        val name = fileName ?: "IMG_${
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
        }$PHOTO_EXTENSION"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String? = null): Uri? {
        return try {
            val uri = createImageUri(context, fileName) ?: return null
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, outputStream)
            }
            uri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
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

    fun deletePhoto(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        return context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.getString(columnIndex)
            } else {
                null
            }
        }
    }

    fun createTempImageFile(context: Context): File {
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("TEMP_${timestamp}_", PHOTO_EXTENSION, storageDir)
    }

    fun copyToInternalStorage(context: Context, uri: Uri, destinationFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class CameraConfiguration(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var camera: Camera? = null

    suspend fun initializeCamera(
        previewView: androidx.camera.view.PreviewView,
        lensFacing: Int = CameraSelector.LENS_FACING_BACK
    ): Boolean = suspendCoroutine { continuation ->

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                continuation.resume(true)
            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(false)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    suspend fun takePicture(outputUri: Uri): Result<Uri> = suspendCoroutine { continuation ->
        val imageCapture = imageCapture ?: run {
            continuation.resume(Result.failure(IllegalStateException("Camera not initialized")))
            return@suspendCoroutine
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "photo_${System.currentTimeMillis()}")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        continuation.resume(Result.success(uri))
                    } ?: continuation.resume(
                        Result.failure(IOException("Failed to save image"))
                    )
                }
                override fun onError(exception: ImageCaptureException) {
                    continuation.resume(Result.failure(exception))
                }
            }
        )
    }
    fun toggleFlash() {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                val currentTorchState = it.cameraInfo.torchState.value ?: TorchState.OFF
                it.cameraControl.enableTorch(currentTorchState == TorchState.OFF)
            }
        }
    }
    fun hasFlashUnit(): Boolean {
        return camera?.cameraInfo?.hasFlashUnit() ?: false
    }
}