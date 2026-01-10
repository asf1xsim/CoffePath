package com.example.coffepath.mlkit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.coffepath.mlkit.model.DetectedLabel
import com.example.coffepath.mlkit.model.DrinkClassificationResult
import com.example.coffepath.mlkit.model.DrinkType
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class DrinkClassifier @Inject constructor(
    private val context: Context
) {
    private val labeler: ImageLabeler by lazy {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.3f)
            .build()
        ImageLabeling.getClient(options)
    }
    suspend fun classifyDrink(imageUri: Uri): Result<DrinkClassificationResult> {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            classifyDrinkFromImage(image)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    suspend fun classifyDrink(bitmap: Bitmap): Result<DrinkClassificationResult> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            classifyDrinkFromImage(image)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun classifyDrinkFromPath(imagePath: String): Result<DrinkClassificationResult> {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                Uri.parse(imagePath)
            )
            classifyDrink(bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun classifyDrinkFromImage(
        image: InputImage
    ): Result<DrinkClassificationResult> = suspendCancellableCoroutine { continuation ->

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val detectedLabels = labels.mapIndexed { index, label ->
                    DetectedLabel(
                        text = label.text,
                        confidence = label.confidence,
                        index = index
                    )
                }
                Log.d("DrinkClassifier", "Total labels found: ${detectedLabels.size}")
                detectedLabels.forEach { label ->
                    Log.d("DrinkClassifier", "Label: ${label.text}, confidence: ${label.confidence}")
                }

                val (drinkType, confidence) = DrinkType.fromLabels(detectedLabels)

                Log.d("DrinkClassifier", "=== Classification Results ===")
                Log.d("DrinkClassifier", "Detected type: ${drinkType.displayName}")
                Log.d("DrinkClassifier", "Confidence: $confidence")
                Log.d("DrinkClassifier", "=============================")

                val result = DrinkClassificationResult(
                    drinkType = drinkType,
                    confidence = confidence,
                    rawLabels = detectedLabels
                )

                continuation.resume(Result.success(result))
            }
            .addOnFailureListener { exception ->
                Log.e("DrinkClassifier", "Classification failed", exception)
                continuation.resumeWithException(exception)
            }
    }

    suspend fun getImageLabels(
        imageUri: Uri,
        topN: Int = 10
    ): Result<List<DetectedLabel>> = suspendCancellableCoroutine { continuation ->

        try {
            val image = InputImage.fromFilePath(context, imageUri)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val detectedLabels = labels
                        .take(topN)
                        .mapIndexed { index, label ->
                            DetectedLabel(
                                text = label.text,
                                confidence = label.confidence,
                                index = index
                            )
                        }
                    continuation.resume(Result.success(detectedLabels))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: IOException) {
            continuation.resumeWithException(e)
        }
    }

    fun close() {
        try {
            labeler.close()
        } catch (e: Exception) {
            Log.e("DrinkClassifier", "Error closing labeler", e)
        }
    }

    companion object {
        fun createForTesting(context: Context): DrinkClassifier {
            return DrinkClassifier(context)
        }
    }
}

fun DrinkClassificationResult.toReadableString(): String {
    return buildString {
        appendLine("Type: ${drinkType.displayName}")
        appendLine("Confidence: ${(confidence * 100).toInt()}%")
        appendLine("Recognized tags:")
        rawLabels.take(5).forEach { label ->
            appendLine("  - ${label.text}: ${(label.confidence * 100).toInt()}%")
        }
    }
}