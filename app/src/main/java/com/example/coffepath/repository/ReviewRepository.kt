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
            userProfileDao.incrementReviewsCount(review.userId)
        } catch (e: Exception) {

        }

        return reviewId
    }

    override suspend fun updateReview(review: Review) {
        reviewDao.update(review)
    }

    override suspend fun deleteReview(review: Review) {
        reviewDao.delete(review)
        try {
            userProfileDao.decrementReviewsCount(review.userId)
        } catch (e: Exception) {

        }
    }
    override suspend fun hasUserReviewedCafe(cafeId: Long, userId: String): Boolean {
        return reviewDao.hasUserReviewedCafe(cafeId, userId)
    }
    fun getRecentReviewsByUser(userId: String, limit: Int = 5): Flow<List<Review>> {
        return reviewDao.getRecentReviewsByUser(userId, limit)
    }
    fun getReviewsWithPhotos(): Flow<List<Review>> {
        return reviewDao.getReviewsWithPhotos()
    }
}