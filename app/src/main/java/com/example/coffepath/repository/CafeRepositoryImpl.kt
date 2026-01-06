package com.example.coffepath.repository

import com.example.coffepath.api.OverpassApi
import com.example.coffepath.database.CafeDao
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.OverpassResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class CafeRepositoryImpl @Inject constructor(
    private val cafeDao: CafeDao,
    private val api: OverpassApi
) : ICafeRepository {

    override fun getAllCafes(): Flow<List<Cafe>> {
        return cafeDao.getAll()
    }

    override fun getFavoriteCafes(): Flow<List<Cafe>> {
        return cafeDao.getFavorites()
    }

    override suspend fun getCafeById(cafeId: Long): Cafe? {
        return cafeDao.getCafeById(cafeId)
    }

    override suspend fun insertCafes(cafes: List<Cafe>) {
        cafeDao.insert(cafes)
    }

    override suspend fun updateFavoriteStatus(cafeId: Long, isFavorite: Boolean) {
        cafeDao.updateFavoriteStatus(cafeId, isFavorite)
    }

    override suspend fun fetchCafesFromApi(
        south: Double,
        west: Double,
        north: Double,
        east: Double
    ): Result<OverpassResponse> {
        return try {
            val query = OverpassApi.buildQuery(south, west, north, east)
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.getCafes(requestBody)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}