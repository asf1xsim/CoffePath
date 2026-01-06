package com.example.coffepath.database

import androidx.room.*
import com.example.coffepath.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE cafe_id = :cafeId ORDER BY timestamp DESC")
    fun getReviewsForCafe(cafeId: Long): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getReviewsByUser(userId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Long): Review?

    @Query("SELECT AVG(rating) FROM reviews WHERE cafe_id = :cafeId")
    suspend fun getAverageRatingForCafe(cafeId: Long): Float?

    @Query("SELECT COUNT(*) FROM reviews WHERE cafe_id = :cafeId")
    suspend fun getReviewCountForCafe(cafeId: Long): Int

    @Query("SELECT COUNT(*) FROM reviews WHERE user_id = :userId")
    suspend fun getReviewCountByUser(userId: String): Int

    @Query("SELECT * FROM reviews WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReviewsByUser(userId: String, limit: Int = 5): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE photo_uri IS NOT NULL ORDER BY timestamp DESC")
    fun getReviewsWithPhotos(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE drink_type = :drinkType ORDER BY timestamp DESC")
    fun getReviewsByDrinkType(drinkType: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)

    @Update
    suspend fun update(review: Review)

    @Delete
    suspend fun delete(review: Review)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteById(reviewId: Long)

    @Query("DELETE FROM reviews WHERE user_id = :userId")
    suspend fun deleteAllByUser(userId: String)

    @Query("DELETE FROM reviews")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM reviews WHERE cafe_id = :cafeId AND user_id = :userId)")
    suspend fun hasUserReviewedCafe(cafeId: Long, userId: String): Boolean
}