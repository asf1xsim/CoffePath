package com.example.coffepath.mlkit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.coffepath.mlkit.model.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class MenuOCRReader @Inject constructor(
    private val context: Context
) {

    private val recognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    suspend fun recognizeMenu(imageUri: Uri): Result<MenuOCRResult> {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            recognizeTextFromImage(image)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    suspend fun recognizeMenu(bitmap: Bitmap): Result<MenuOCRResult> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizeTextFromImage(image)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun recognizeText(imageUri: Uri): Result<String> {
        return recognizeMenu(imageUri).map { it.fullText }
    }
    suspend fun recognizeText(bitmap: Bitmap): Result<String> {
        return recognizeMenu(bitmap).map { it.fullText }
    }

    private suspend fun recognizeTextFromImage(
        image: InputImage
    ): Result<MenuOCRResult> = suspendCancellableCoroutine { continuation ->

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                try {
                    val fullText = visionText.text
                    val textBlocks = visionText.textBlocks.map { block ->
                        TextBlock(
                            text = block.text,
                            confidence = calculateBlockConfidence(block.lines.size),
                            boundingBox = block.boundingBox?.let { box ->
                                BoundingBox(
                                    left = box.left,
                                    top = box.top,
                                    right = box.right,
                                    bottom = box.bottom
                                )
                            }
                        )
                    }

                    val menuItems = MenuParser.parseMenu(fullText, textBlocks)
                    val confidence = calculateOverallConfidence(textBlocks)
                    val result = MenuOCRResult(
                        fullText = fullText,
                        textBlocks = textBlocks,
                        detectedItems = menuItems,
                        confidence = confidence
                    )

                    continuation.resume(Result.success(result))
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    private fun calculateBlockConfidence(lineCount: Int): Float {
        return when {
            lineCount >= 3 -> 0.9f
            lineCount == 2 -> 0.8f
            lineCount == 1 -> 0.7f
            else -> 0.5f
        }
    }

    private fun calculateOverallConfidence(blocks: List<TextBlock>): Float {
        if (blocks.isEmpty()) return 0f

        val avgConfidence = blocks.map { it.confidence }.average().toFloat()

        val blockBonus = when {
            blocks.size >= 10 -> 0.1f
            blocks.size >= 5 -> 0.05f
            else -> 0f
        }

        return (avgConfidence + blockBonus).coerceIn(0f, 1f)
    }
    suspend fun recognizeMenuCategory(
        imageUri: Uri,
        category: MenuCategory
    ): Result<List<MenuItem>> {
        return recognizeMenu(imageUri).map { result ->
            result.detectedItems.filter { it.category == category }
        }
    }
    suspend fun hasText(imageUri: Uri): Result<Boolean> {
        return recognizeText(imageUri).map { text ->
            text.isNotBlank() && text.length > 10
        }
    }
    fun close() {
        try {
            recognizer.close()
        } catch (e: Exception) {

        }
    }
    companion object {
        fun createForTesting(context: Context): MenuOCRReader {
            return MenuOCRReader(context)
        }
    }
}

fun MenuOCRResult.toReadableString(): String {
    return buildString {
        appendLine("=== Recognized menu ===")
        appendLine("Confidence: ${(confidence * 100).toInt()}%")
        appendLine("Text blocks: ${textBlocks.size}")
        appendLine("Menu items: ${detectedItems.size}")
        appendLine()

        if (detectedItems.isNotEmpty()) {
            appendLine("Menu items:")
            detectedItems.forEach { item ->
                appendLine("  ${item.getFullDescription()}")
            }
        } else {
            appendLine("Full text:")
            appendLine(fullText)
        }
    }
}
fun MenuOCRResult.searchItems(query: String): List<MenuItem> {
    if (query.isBlank()) return detectedItems

    val lowerQuery = query.lowercase()
    return detectedItems.filter { item ->
        item.name.lowercase().contains(lowerQuery) ||
                item.description?.lowercase()?.contains(lowerQuery) == true
    }
}

fun MenuOCRResult.groupByCategory(): Map<MenuCategory, List<MenuItem>> {
    return detectedItems.groupBy { it.category }
}