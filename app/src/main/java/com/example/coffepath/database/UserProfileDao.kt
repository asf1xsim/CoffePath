package com.example.coffepath.database

import androidx.room.*
import com.example.coffepath.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE user_id = :userId")
    suspend fun getUserProfile(userId: String): UserProfile?

    @Query("SELECT * FROM user_profile WHERE user_id = :userId")
    fun getUserProfileFlow(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile ORDER BY created_at DESC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Update
    suspend fun update(userProfile: UserProfile)

    @Delete
    suspend fun delete(userProfile: UserProfile)

    @Query("DELETE FROM user_profile WHERE user_id = :userId")
    suspend fun deleteById(userId: String)

    @Query("UPDATE user_profile SET name = :name, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun updateName(userId: String, name: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET photo_uri = :photoUri, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun updatePhoto(userId: String, photoUri: String?, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET email = :email, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun updateEmail(userId: String, email: String?, timestamp: Long = System.currentTimeMillis())

    @Query("""
        UPDATE user_profile 
        SET reviews_count = :reviewsCount, 
            favorites_count = :favoritesCount, 
            visited_cafes_count = :visitedCafesCount,
            updated_at = :timestamp 
        WHERE user_id = :userId
    """)
    suspend fun updateStats(
        userId: String,
        reviewsCount: Int,
        favoritesCount: Int,
        visitedCafesCount: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE user_profile SET reviews_count = reviews_count + 1, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun incrementReviewsCount(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET reviews_count = CASE WHEN reviews_count > 0 THEN reviews_count - 1 ELSE 0 END, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun decrementReviewsCount(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET favorites_count = favorites_count + 1, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun incrementFavoritesCount(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET favorites_count = CASE WHEN favorites_count > 0 THEN favorites_count - 1 ELSE 0 END, updated_at = :timestamp WHERE user_id = :userId")
    suspend fun decrementFavoritesCount(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT EXISTS(SELECT 1 FROM user_profile WHERE user_id = :userId)")
    suspend fun exists(userId: String): Boolean
}