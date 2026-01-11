package com.example.coffepath.database

import androidx.room.*
import com.example.coffepath.model.Cafe
import kotlinx.coroutines.flow.Flow

@Dao
interface CafeDao {
    @Query("SELECT * FROM cafes")
    fun getAll(): Flow<List<Cafe>>

    @Query("SELECT * FROM cafes WHERE is_favorite = 1")
    fun getFavorites(): Flow<List<Cafe>>

    @Query("SELECT * FROM cafes WHERE id = :cafeId")
    suspend fun getCafeById(cafeId: Long): Cafe?

    @Query("SELECT * FROM cafes WHERE id = :cafeId")
    fun observeCafeById(cafeId: Long): Flow<Cafe?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cafes: List<Cafe>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(cafe: Cafe)

    @Update
    suspend fun update(cafe: Cafe)

    @Query("DELETE FROM cafes")
    suspend fun deleteAll()

    @Query("UPDATE cafes SET is_favorite = :isFavorite WHERE id = :cafeId")
    suspend fun updateFavoriteStatus(cafeId: Long, isFavorite: Boolean)
}