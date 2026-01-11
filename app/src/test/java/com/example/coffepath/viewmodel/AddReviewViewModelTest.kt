package com.example.coffepath.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.coffepath.mlkit.DrinkClassifier
import com.example.coffepath.mlkit.MenuOCRReader
import com.example.coffepath.mlkit.model.DetectedLabel
import com.example.coffepath.mlkit.model.DrinkClassificationResult
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.mlkit.model.MenuOCRResult
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.IReviewRepository
import com.example.coffepath.repository.PreferencesRepository
import com.example.coffepath.ui.screens.review.AddReviewViewModel
import com.example.coffepath.viewmodel.BaseViewModelTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddReviewViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: AddReviewViewModel
    private lateinit var cafeRepository: ICafeRepository
    private lateinit var reviewRepository: IReviewRepository
    private lateinit var drinkClassifier: DrinkClassifier
    private lateinit var menuOCRReader: MenuOCRReader
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    override fun setup() {
        super.setup()
        cafeRepository = mockk(relaxed = true)
        reviewRepository = mockk(relaxed = true)
        drinkClassifier = mockk(relaxed = true)
        menuOCRReader = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)

        savedStateHandle = SavedStateHandle(mapOf("cafeId" to 1L))

        val testCafe = Cafe(
            id = 1L,
            name = "Test Cafe",
            latitude = 50.0,
            longitude = 14.0,
            isFavorite = false
        )
        coEvery { cafeRepository.getCafeById(1L) } returns testCafe
    }

    @Test
    fun `initial state should be invalid without rating and comment`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isValid)
            assertEquals(0f, state.rating, 0.01f)
            assertTrue(state.comment.isEmpty())
        }
    }

    @Test
    fun `updateRating should update state`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.updateRating(4.5f)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(4.5f, state.rating, 0.01f)
        }
    }

    @Test
    fun `updateComment should update state`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.updateComment("Great coffee!")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Great coffee!", state.comment)
        }
    }

    @Test
    fun `setPhoto should update state and classify drink`() = runTest {
        val testUri = mockk<Uri>()
        val classificationResult = DrinkClassificationResult(
            drinkType = DrinkType.ESPRESSO,
            confidence = 0.8f,
            rawLabels = listOf(
                DetectedLabel(text = "Coffee", confidence = 0.9f, index = 0)
            )
        )
        coEvery { drinkClassifier.classifyDrink(testUri) } returns Result.success(classificationResult)

        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.setPhoto(testUri, autoClassify = true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testUri, state.photoUri)
            assertEquals(DrinkType.ESPRESSO, state.detectedDrinkType)
        }
    }

    @Test
    fun `setDrinkType should update state`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.setDrinkType(DrinkType.CAPPUCCINO)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(DrinkType.CAPPUCCINO, state.detectedDrinkType)
        }
    }

    @Test
    fun `saveReview should fail with invalid form`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.saveReview()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertFalse(state.isSuccess)
        }
    }

    @Test
    fun `saveReview should succeed with valid form`() = runTest {
        every { preferencesRepository.userIdFlow } returns flowOf("user123")
        coEvery { reviewRepository.insertReview(any()) } returns 1L

        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.updateRating(4.5f)
        viewModel.updateComment("Great coffee!")
        viewModel.saveReview()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertNull(state.error)
        }
    }

    @Test
    fun `clearDrinkType should remove detected drink`() = runTest {
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )
        viewModel.setDrinkType(DrinkType.LATTE)

        viewModel.clearDrinkType()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.detectedDrinkType)
        }
    }

    @Test
    fun `removePhoto should clear photo and drink type`() = runTest {
        val testUri = mockk<Uri>()
        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )
        viewModel.setPhoto(testUri, autoClassify = false)
        viewModel.setDrinkType(DrinkType.CAPPUCCINO)

        viewModel.removePhoto()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.photoUri)
            assertNull(state.detectedDrinkType)
        }
    }

    @Test
    fun `setMenuPhoto should update state and recognize menu`() = runTest {
        val testUri = mockk<Uri>()
        val ocrResult = MenuOCRResult(
            fullText = "Test menu text",
            textBlocks = emptyList(),
            detectedItems = emptyList(),
            confidence = 0.9f
        )
        coEvery { menuOCRReader.recognizeMenu(testUri) } returns Result.success(ocrResult)

        viewModel = AddReviewViewModel(
            savedStateHandle,
            cafeRepository,
            reviewRepository,
            drinkClassifier,
            menuOCRReader,
            preferencesRepository
        )

        viewModel.setMenuPhoto(testUri, autoRecognize = true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testUri, state.menuPhotoUri)
            assertEquals(ocrResult, state.menuOCRResult)
        }
    }
}