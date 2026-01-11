package com.example.coffepath.repository

import app.cash.turbine.test
import com.example.coffepath.database.ReviewDao
import com.example.coffepath.database.UserProfileDao
import com.example.coffepath.model.Review
import com.example.coffepath.model.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReviewRepositoryImplTest : BaseRepositoryTest() {

    private lateinit var reviewDao: ReviewDao
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var repository: ReviewRepositoryImpl

    private val testReview = Review(
        id = 1L,
        cafeId = 100L,
        userId = "user123",
        rating = 4.5f,
        comment = "Great coffee!",
        photoUri = null,
        drinkType = "Cappuccino",
        timestamp = System.currentTimeMillis()
    )

    private val testUserProfile = UserProfile(
        userId = "user123",
        name = "Test User",
        email = "test@example.com",
        reviewsCount = 5,
        favoritesCount = 3,
        visitedCafesCount = 10
    )

    @Before
    override fun setup() {
        super.setup()
        reviewDao = mockk(relaxed = true)
        userProfileDao = mockk(relaxed = true)
        repository = ReviewRepositoryImpl(reviewDao, userProfileDao)
    }

    @Test
    fun `getAllReviews returns flow from dao`() = runTest {
        val reviews = listOf(testReview, testReview.copy(id = 2L, rating = 5f))
        every { reviewDao.getAllReviews() } returns flowOf(reviews)
        repository.getAllReviews().test {
            assertEquals(reviews, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getAllReviews returns empty list when no reviews`() = runTest {
        every { reviewDao.getAllReviews() } returns flowOf(emptyList())
        repository.getAllReviews().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getReviewsForCafe returns reviews for specific cafe`() = runTest {
        val cafeReviews = listOf(
            testReview.copy(cafeId = 100L),
            testReview.copy(id = 2L, cafeId = 100L)
        )
        every { reviewDao.getReviewsForCafe(100L) } returns flowOf(cafeReviews)
        repository.getReviewsForCafe(100L).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.cafeId == 100L })
            awaitComplete()
        }
    }

    @Test
    fun `getReviewsForCafe returns empty list for cafe with no reviews`() = runTest {
        every { reviewDao.getReviewsForCafe(999L) } returns flowOf(emptyList())
        repository.getReviewsForCafe(999L).test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getReviewsByUser returns reviews for specific user`() = runTest {
        val userReviews = listOf(
            testReview.copy(userId = "user123"),
            testReview.copy(id = 2L, userId = "user123")
        )
        every { reviewDao.getReviewsByUser("user123") } returns flowOf(userReviews)
        repository.getReviewsByUser("user123").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.userId == "user123" })
            awaitComplete()
        }
    }

    @Test
    fun `getReviewById returns review when exists`() = runTest {
        coEvery { reviewDao.getReviewById(1L) } returns testReview
        val result = repository.getReviewById(1L)
        assertNotNull(result)
        assertEquals(testReview.id, result?.id)
        assertEquals(testReview.comment, result?.comment)
    }

    @Test
    fun `getReviewById returns null when review does not exist`() = runTest {
        coEvery { reviewDao.getReviewById(999L) } returns null
        val result = repository.getReviewById(999L)
        assertNull(result)
    }

    @Test
    fun `getAverageRatingForCafe returns correct average`() = runTest {
        coEvery { reviewDao.getAverageRatingForCafe(100L) } returns 4.3f
        val result = repository.getAverageRatingForCafe(100L)
        assertEquals(4.3f, result)
    }

    @Test
    fun `getAverageRatingForCafe returns null when no reviews`() = runTest {
        coEvery { reviewDao.getAverageRatingForCafe(999L) } returns null
        val result = repository.getAverageRatingForCafe(999L)
        assertNull(result)
    }

    @Test
    fun `getReviewCountForCafe returns correct count`() = runTest {
        coEvery { reviewDao.getReviewCountForCafe(100L) } returns 5
        val result = repository.getReviewCountForCafe(100L)
        assertEquals(5, result)
    }

    @Test
    fun `getReviewCountForCafe returns zero when no reviews`() = runTest {
        coEvery { reviewDao.getReviewCountForCafe(999L) } returns 0
        val result = repository.getReviewCountForCafe(999L)
        assertEquals(0, result)
    }

    @Test
    fun `insertReview inserts review and increments user profile count when profile exists`() = runTest {
        coEvery { reviewDao.insert(testReview) } returns 1L
        coEvery { userProfileDao.exists("user123") } returns true
        coEvery { userProfileDao.incrementReviewsCount("user123", any()) } returns Unit
        val reviewId = repository.insertReview(testReview)
        assertEquals(1L, reviewId)
        coVerify(exactly = 1) { reviewDao.insert(testReview) }
        coVerify(exactly = 1) { userProfileDao.incrementReviewsCount("user123", any()) }
        coVerify(exactly = 0) { userProfileDao.insert(any()) }
    }

    @Test
    fun `insertReview creates basic profile when user profile does not exist`() = runTest {
        coEvery { reviewDao.insert(testReview) } returns 1L
        coEvery { userProfileDao.exists("user123") } returns false
        coEvery { userProfileDao.insert(any()) } returns Unit
        coEvery { userProfileDao.incrementReviewsCount("user123", any()) } returns Unit
        val reviewId = repository.insertReview(testReview)
        assertEquals(1L, reviewId)
        coVerify(exactly = 1) { reviewDao.insert(testReview) }
        coVerify(exactly = 1) { userProfileDao.insert(any()) }
        coVerify(exactly = 1) { userProfileDao.incrementReviewsCount("user123", any()) }
    }

    @Test
    fun `insertReview continues even if profile update fails`() = runTest {
        coEvery { reviewDao.insert(testReview) } returns 1L
        coEvery { userProfileDao.exists("user123") } throws Exception("Database error")
        val reviewId = repository.insertReview(testReview)
        assertEquals(1L, reviewId)
        coVerify(exactly = 1) { reviewDao.insert(testReview) }
    }

    @Test
    fun `updateReview calls dao update with correct review`() = runTest {
        val updatedReview = testReview.copy(comment = "Updated comment")
        coEvery { reviewDao.update(updatedReview) } returns Unit
        repository.updateReview(updatedReview)
        coVerify(exactly = 1) { reviewDao.update(updatedReview) }
    }

    @Test
    fun `deleteReview deletes review and decrements user profile count when profile exists`() = runTest {
        coEvery { reviewDao.delete(testReview) } returns Unit
        coEvery { userProfileDao.exists("user123") } returns true
        coEvery { userProfileDao.decrementReviewsCount("user123", any()) } returns Unit
        repository.deleteReview(testReview)
        coVerify(exactly = 1) { reviewDao.delete(testReview) }
        coVerify(exactly = 1) { userProfileDao.decrementReviewsCount("user123", any()) }
    }

    @Test
    fun `deleteReview does not decrement count when profile does not exist`() = runTest {
        coEvery { reviewDao.delete(testReview) } returns Unit
        coEvery { userProfileDao.exists("user123") } returns false
        repository.deleteReview(testReview)
        coVerify(exactly = 1) { reviewDao.delete(testReview) }
        coVerify(exactly = 0) { userProfileDao.decrementReviewsCount(any()) }
    }

    @Test
    fun `deleteReview continues even if profile update fails`() = runTest {
        coEvery { reviewDao.delete(testReview) } returns Unit
        coEvery { userProfileDao.exists("user123") } throws Exception("Database error")
        repository.deleteReview(testReview)
        coVerify(exactly = 1) { reviewDao.delete(testReview) }
    }

    @Test
    fun `hasUserReviewedCafe returns true when user has reviewed cafe`() = runTest {
        coEvery { reviewDao.hasUserReviewedCafe(100L, "user123") } returns true
        val result = repository.hasUserReviewedCafe(100L, "user123")
        assertTrue(result)
    }

    @Test
    fun `hasUserReviewedCafe returns false when user has not reviewed cafe`() = runTest {
        coEvery { reviewDao.hasUserReviewedCafe(100L, "user123") } returns false
        val result = repository.hasUserReviewedCafe(100L, "user123")
        assertFalse(result)
    }
}