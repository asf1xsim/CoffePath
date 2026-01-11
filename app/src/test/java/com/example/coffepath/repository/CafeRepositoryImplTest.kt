package com.example.coffepath.repository

import app.cash.turbine.test
import com.example.coffepath.api.OverpassApi
import com.example.coffepath.database.CafeDao
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.OverpassElement
import com.example.coffepath.model.OverpassResponse
import com.example.coffepath.model.Tags
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CafeRepositoryImplTest : BaseRepositoryTest() {

    private lateinit var cafeDao: CafeDao
    private lateinit var api: OverpassApi
    private lateinit var repository: CafeRepositoryImpl

    private val testCafe = Cafe(
        id = 1L,
        name = "Test Cafe",
        latitude = 49.1951,
        longitude = 16.6068,
        address = "Test Street 1",
        openingHours = "Mo-Fr 08:00-18:00",
        isFavorite = false
    )

    @Before
    override fun setup() {
        super.setup()
        cafeDao = mockk(relaxed = true)
        api = mockk(relaxed = true)
        repository = CafeRepositoryImpl(cafeDao, api)
    }

    @Test
    fun `getAllCafes returns flow from dao`() = runTest {
        val cafes = listOf(testCafe, testCafe.copy(id = 2L, name = "Cafe 2"))
        every { cafeDao.getAll() } returns flowOf(cafes)
        repository.getAllCafes().test {
            assertEquals(cafes, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getAllCafes returns empty list when no cafes`() = runTest {
        every { cafeDao.getAll() } returns flowOf(emptyList())
        repository.getAllCafes().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getFavoriteCafes returns only favorite cafes`() = runTest {
        val favoriteCafes = listOf(
            testCafe.copy(isFavorite = true),
            testCafe.copy(id = 2L, name = "Cafe 2", isFavorite = true)        )
        every { cafeDao.getFavorites() } returns flowOf(favoriteCafes)
        repository.getFavoriteCafes().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.isFavorite })
            awaitComplete()
        }
    }

    @Test
    fun `getCafeById returns cafe when exists`() = runTest {
        coEvery { cafeDao.getCafeById(1L) } returns testCafe
        val result = repository.getCafeById(1L)
        assertNotNull(result)
        assertEquals(testCafe.id, result?.id)
        assertEquals(testCafe.name, result?.name)
    }

    @Test
    fun `getCafeById returns null when cafe does not exist`() = runTest {
        coEvery { cafeDao.getCafeById(999L) } returns null
        val result = repository.getCafeById(999L)
        assertNull(result)
    }

    @Test
    fun `observeCafeById returns flow with cafe updates`() = runTest {
        every { cafeDao.observeCafeById(1L) } returns flowOf(testCafe)
        repository.observeCafeById(1L).test {
            assertEquals(testCafe, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `insertCafes calls dao insert with correct list`() = runTest {
        val cafes = listOf(testCafe, testCafe.copy(id = 2L))
        coEvery { cafeDao.insert(cafes) } returns Unit
        repository.insertCafes(cafes)
        coVerify(exactly = 1) { cafeDao.insert(cafes) }
    }

    @Test
    fun `updateFavoriteStatus calls dao with correct parameters`() = runTest {
        coEvery { cafeDao.updateFavoriteStatus(1L, true) } returns Unit
        repository.updateFavoriteStatus(1L, true)
        coVerify(exactly = 1) { cafeDao.updateFavoriteStatus(1L, true) }
    }

    @Test
    fun `updateFavoriteStatus can toggle favorite status`() = runTest {
        coEvery { cafeDao.updateFavoriteStatus(any(), any()) } returns Unit
        repository.updateFavoriteStatus(1L, true)
        repository.updateFavoriteStatus(1L, false)
        coVerify(exactly = 1) { cafeDao.updateFavoriteStatus(1L, true) }
        coVerify(exactly = 1) { cafeDao.updateFavoriteStatus(1L, false) }
    }

    @Test
    fun `fetchCafesFromApi returns success when api call succeeds`() = runTest {
        val element = OverpassElement(
            type = "node",
            id = 1L,
            lat = 49.1951,
            lon = 16.6068,
            tags = Tags(
                name = "Test Cafe",
                amenity = "cafe",
                cuisine = null,
                openingHours = "Mo-Fr 08:00-18:00",
                street = "Test Street",
                houseNumber = "1",
                city = "Brno",
                website = null,
                phone = null
            )
        )
        val apiResponse = OverpassResponse(elements = listOf(element))
        val retrofitResponse = Response.success(apiResponse)
        coEvery { api.getCafes(any()) } returns retrofitResponse
        val result = repository.fetchCafesFromApi(49.0, 16.0, 49.5, 16.5)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.elements?.size)
    }

    @Test
    fun `fetchCafesFromApi returns failure when api call fails`() = runTest {
        val errorResponse = Response.error<OverpassResponse>(
            404,
            "Not Found".toResponseBody("text/plain".toMediaType())
        )
        coEvery { api.getCafes(any()) } returns errorResponse
        val result = repository.fetchCafesFromApi(49.0, 16.0, 49.5, 16.5)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("404") == true)
    }

    @Test
    fun `fetchCafesFromApi returns failure when exception thrown`() = runTest {
        coEvery { api.getCafes(any()) } throws Exception("Network error")
        val result = repository.fetchCafesFromApi(49.0, 16.0, 49.5, 16.5)
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateCafe calls dao update with correct cafe`() = runTest {
        val updatedCafe = testCafe.copy(name = "Updated Name")
        coEvery { cafeDao.update(updatedCafe) } returns Unit
        repository.updateCafe(updatedCafe)
        coVerify(exactly = 1) { cafeDao.update(updatedCafe) }
    }

    @Test
    fun `updateCafeMenu calls dao with correct parameters`() = runTest {
        val menuJson = """{"items": ["Coffee", "Tea"]}"""
        coEvery { cafeDao.updateMenuJson(1L, menuJson) } returns Unit
        repository.updateCafeMenu(1L, menuJson)
        coVerify(exactly = 1) { cafeDao.updateMenuJson(1L, menuJson) }
    }

    @Test
    fun `updateCafeMenu can set menu to null`() = runTest {
        coEvery { cafeDao.updateMenuJson(1L, null) } returns Unit
        repository.updateCafeMenu(1L, null)
        coVerify(exactly = 1) { cafeDao.updateMenuJson(1L, null) }
    }
}