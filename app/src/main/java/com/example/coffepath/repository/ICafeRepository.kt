package com.example.coffepath.repository

import com.example.coffepath.model.Cafe
import com.example.coffepath.model.OverpassResponse
import kotlinx.coroutines.flow.Flow

interface ICafeRepository {
    fun getAllCafes(): Flow<List<Cafe>>
    fun getFavoriteCafes(): Flow<List<Cafe>>
    suspend fun getCafeById(cafeId: Long): Cafe?
    fun observeCafeById(cafeId: Long): Flow<Cafe?>
    suspend fun insertCafes(cafes: List<Cafe>)
    suspend fun updateFavoriteStatus(cafeId: Long, isFavorite: Boolean)
    suspend fun fetchCafesFromApi(south: Double, west: Double, north: Double, east: Double): Result<OverpassResponse>
    suspend fun updateCafe(cafe: Cafe)
    suspend fun updateCafeMenu(cafeId: Long, menuJson: String?)
}