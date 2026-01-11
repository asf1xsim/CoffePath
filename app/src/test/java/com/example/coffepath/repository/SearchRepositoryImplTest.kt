package com.example.coffepath.repository

import com.example.coffepath.database.CafeDao
import com.example.coffepath.database.ReviewDao
import com.example.coffepath.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SearchRepositoryImplTest : BaseRepositoryTest() {

    private lateinit var cafeDao: CafeDao
    private lateinit var reviewDao: ReviewDao
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var repository: SearchRepositoryImpl

    private val testCafe1 = Cafe(
        id = 1L,
        name = "Coffee House",
        latitude = 49.1951,
        longitude = 16.6068,
        address = "Main Street 1",
        isFavorite = true
    )

    private val testCafe2 = Cafe(
        id = 2L,
        name = "Tea Shop",
        latitude = 49.1952,
        longitude = 16.6069,
        address = "Second Street 2",
        isFavorite = false
    )

    private val testCafe3 = Cafe(
        id = 3L,
        name = "Cafe Central",
        latitude = 49.1953,
        longitude = 16.6070,
        address = "Central Square 3",
        isFavorite = false
    )

    @Before
    override fun setup() {
        super.setup()
        cafeDao = mockk(relaxed = true)
        reviewDao = mockk(relaxed = true)
        preferencesRepository = mockk(relaxed = true)
        repository = SearchRepositoryImpl(cafeDao, reviewDao, preferencesRepository)
    }

    @Test
    fun `searchCafes returns all cafes when query is blank`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(query = "")
        assertEquals(3, results.size)
    }

    @Test
    fun `searchCafes filters by name case insensitive`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(query = "coffee")
        assertEquals(1, results.size)
        assertEquals("Coffee House", results[0].cafe.name)
    }

    @Test
    fun `searchCafes filters by address`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(query = "Main Street")
        assertEquals(1, results.size)
        assertEquals(testCafe1.id, results[0].cafe.id)
    }

    @Test
    fun `searchCafes returns empty list when no matches`() = runTest {
        val cafes = listOf(testCafe1, testCafe2)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(query = "Nonexistent")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `searchCafes includes rating and review count`() = runTest {
        every { cafeDao.getAll() } returns flowOf(listOf(testCafe1))
        coEvery { reviewDao.getAverageRatingForCafe(1L) } returns 4.5f
        coEvery { reviewDao.getReviewCountForCafe(1L) } returns 10
        val results = repository.searchCafes(query = "")
        assertEquals(1, results.size)
        assertEquals(4.5f, results[0].averageRating)
        assertEquals(10, results[0].reviewsCount)
    }

    @Test
    fun `searchCafes filters by minimum rating`() = runTest {
        val cafes = listOf(testCafe1, testCafe2)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(1L) } returns 4.5f
        coEvery { reviewDao.getAverageRatingForCafe(2L) } returns 3.0f
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 5
        val filter = CafeFilter(minRating = 4.0f)
        val results = repository.searchCafes(query = "", filter = filter)
        assertEquals(1, results.size)
        assertEquals(testCafe1.id, results[0].cafe.id)
    }

    @Test
    fun `searchCafes filters by favorites only`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val filter = CafeFilter(isFavorite = true)
        val results = repository.searchCafes(query = "", filter = filter)
        assertEquals(1, results.size)
        assertTrue(results[0].cafe.isFavorite)
    }

    @Test
    fun `searchCafes filters by has reviews`() = runTest {
        val cafes = listOf(testCafe1, testCafe2)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(1L) } returns 5
        coEvery { reviewDao.getReviewCountForCafe(2L) } returns 0
        val filter = CafeFilter(hasReviews = true)
        val results = repository.searchCafes(query = "", filter = filter)
        assertEquals(1, results.size)
        assertEquals(testCafe1.id, results[0].cafe.id)
    }

    @Test
    fun `searchCafes sorts by name ascending`() = runTest {
        val cafes = listOf(testCafe1, testCafe3, testCafe2)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(
            query = "",
            sortOption = CafeSortOption.NAME_ASC
        )
        assertEquals("Cafe Central", results[0].cafe.name)
        assertEquals("Coffee House", results[1].cafe.name)
        assertEquals("Tea Shop", results[2].cafe.name)
    }

    @Test
    fun `searchCafes sorts by name descending`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 0
        val results = repository.searchCafes(
            query = "",
            sortOption = CafeSortOption.NAME_DESC
        )
        assertEquals("Tea Shop", results[0].cafe.name)
        assertEquals("Coffee House", results[1].cafe.name)
        assertEquals("Cafe Central", results[2].cafe.name)
    }

    @Test
    fun `searchCafes sorts by rating descending`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(1L) } returns 3.5f
        coEvery { reviewDao.getAverageRatingForCafe(2L) } returns 4.8f
        coEvery { reviewDao.getAverageRatingForCafe(3L) } returns 4.0f
        coEvery { reviewDao.getReviewCountForCafe(any()) } returns 1
        val results = repository.searchCafes(
            query = "",
            sortOption = CafeSortOption.RATING_DESC
        )
        assertEquals(4.8f, results[0].averageRating)
        assertEquals(4.0f, results[1].averageRating)
        assertEquals(3.5f, results[2].averageRating)
    }

    @Test
    fun `searchCafes sorts by reviews count descending`() = runTest {
        val cafes = listOf(testCafe1, testCafe2, testCafe3)
        every { cafeDao.getAll() } returns flowOf(cafes)
        coEvery { reviewDao.getAverageRatingForCafe(any()) } returns null
        coEvery { reviewDao.getReviewCountForCafe(1L) } returns 5
        coEvery { reviewDao.getReviewCountForCafe(2L) } returns 15
        coEvery { reviewDao.getReviewCountForCafe(3L) } returns 10
        val results = repository.searchCafes(
            query = "",
            sortOption = CafeSortOption.REVIEWS_COUNT_DESC
        )
        assertEquals(15, results[0].reviewsCount)
        assertEquals(10, results[1].reviewsCount)
        assertEquals(5, results[2].reviewsCount)
    }

    @Test
    fun `addToSearchHistory adds new query to history`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = emptyList())
        )
        coEvery { preferencesRepository.updateSearchHistory(any()) } returns Unit
        repository.addToSearchHistory("coffee")
        coVerify(exactly = 1) {
            preferencesRepository.updateSearchHistory(listOf("coffee"))
        }
    }

    @Test
    fun `addToSearchHistory does not add blank query`() = runTest {
        repository.addToSearchHistory("")
        coVerify(exactly = 0) { preferencesRepository.updateSearchHistory(any()) }
    }

    @Test
    fun `addToSearchHistory removes duplicate and adds to front`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = listOf("tea", "coffee", "latte"))
        )
        coEvery { preferencesRepository.updateSearchHistory(any()) } returns Unit
        repository.addToSearchHistory("coffee")
        coVerify(exactly = 1) {
            preferencesRepository.updateSearchHistory(listOf("coffee", "tea", "latte"))
        }
    }

    @Test
    fun `addToSearchHistory limits history to 20 items`() = runTest {
        val longHistory = (1..25).map { "query$it" }
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = longHistory)
        )
        coEvery { preferencesRepository.updateSearchHistory(any()) } returns Unit
        repository.addToSearchHistory("new query")
        coVerify {
            preferencesRepository.updateSearchHistory(match { it.size == 20 })
        }
    }

    @Test
    fun `getSearchHistory returns history from preferences`() = runTest {
        val queries = listOf("coffee", "tea", "latte")
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = queries)        )

        val history = repository.getSearchHistory()
        assertEquals(3, history.size)
        assertEquals("coffee", history[0].query)
        assertEquals("tea", history[1].query)
        assertEquals("latte", history[2].query)
    }

    @Test
    fun `getSearchHistory returns empty list when no history`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = emptyList())
        )
        val history = repository.getSearchHistory()
        assertTrue(history.isEmpty())
    }

    @Test
    fun `clearSearchHistory clears all history`() = runTest {
        coEvery { preferencesRepository.updateSearchHistory(emptyList()) } returns Unit
        repository.clearSearchHistory()
        coVerify(exactly = 1) {
            preferencesRepository.updateSearchHistory(emptyList())
        }
    }

    @Test
    fun `removeFromSearchHistory removes specific query`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = listOf("coffee", "tea", "latte"))
        )
        coEvery { preferencesRepository.updateSearchHistory(any()) } returns Unit
        repository.removeFromSearchHistory("tea")
        coVerify(exactly = 1) {
            preferencesRepository.updateSearchHistory(listOf("coffee", "latte"))
        }
    }

    @Test
    fun `removeFromSearchHistory is case insensitive`() = runTest {
        every { preferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(searchHistory = listOf("Coffee", "Tea", "Latte"))        )
        coEvery { preferencesRepository.updateSearchHistory(any()) } returns Unit
        repository.removeFromSearchHistory("tea")
        coVerify(exactly = 1) {
            preferencesRepository.updateSearchHistory(listOf("Coffee", "Latte"))
        }
    }
}