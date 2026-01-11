package com.example.coffepath.repository

import com.example.coffepath.database.ReviewDao
import com.example.coffepath.database.UserProfileDao
import com.example.coffepath.model.Review
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface IReviewRepository {
    fun getAllReviews(): Flow<List<Review>>
    fun getReviewsForCafe(cafeId: Long): Flow<List<Review>>
    fun getReviewsByUser(userId: String): Flow<List<Review>>
    suspend fun getReviewById(reviewId: Long): Review?
    suspend fun getAverageRatingForCafe(cafeId: Long): Float?
    suspend fun getReviewCountForCafe(cafeId: Long): Int
    suspend fun insertReview(review: Review): Long
    suspend fun updateReview(review: Review)
    suspend fun deleteReview(review: Review)
    suspend fun hasUserReviewedCafe(cafeId: Long, userId: String): Boolean
}

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val userProfileDao: UserProfileDao
) : IReviewRepository {

    override fun getAllReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews()
    }

    override fun getReviewsForCafe(cafeId: Long): Flow<List<Review>> {
        return reviewDao.getReviewsForCafe(cafeId)
    }

    override fun getReviewsByUser(userId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByUser(userId)
    }

    override suspend fun getReviewById(reviewId: Long): Review? {
        return reviewDao.getReviewById(reviewId)
    }

    override suspend fun getAverageRatingForCafe(cafeId: Long): Float? {
        return reviewDao.getAverageRatingForCafe(cafeId)
    }

    override suspend fun getReviewCountForCafe(cafeId: Long): Int {
        return reviewDao.getReviewCountForCafe(cafeId)
    }

    override suspend fun insertReview(review: Review): Long {
        val reviewId = reviewDao.insert(review)
        try {
            val profileExists = userProfileDao.exists(review.userId)
            if (!profileExists) {
                val basicProfile = com.example.coffepath.model.UserProfile(
                    userId = review.userId,
                    name = "User",
                    email = null,
                    photoUri = null,
                    reviewsCount = 0,
                    favoritesCount = 0,
                    visitedCafesCount = 0,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                userProfileDao.insert(basicProfile)
            }
            userProfileDao.incrementReviewsCount(review.userId)
        } catch (e: Exception) {
            android.util.Log.e("ReviewRepository", "Failed to increment review count", e)
        }

        return reviewId
    }

    override suspend fun updateReview(review: Review) {
        reviewDao.update(review)
    }

    override suspend fun deleteReview(review: Review) {
        reviewDao.delete(review)
        try {
            val profileExists = userProfileDao.exists(review.userId)
            if (profileExists) {
                userProfileDao.decrementReviewsCount(review.userId)
            }
        } catch (e: Exception) {
            android.util.Log.e("ReviewRepository", "Failed to decrement review count", e)
        }
    }
    override suspend fun hasUserReviewedCafe(cafeId: Long, userId: String): Boolean {
        return reviewDao.hasUserReviewedCafe(cafeId, userId)
    }
}