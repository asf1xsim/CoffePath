package com.example.coffepath.repository

import com.example.coffepath.database.UserProfileDao
import com.example.coffepath.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


interface IUserProfileRepository {
    fun getUserProfile(userId: String): Flow<UserProfile?>
    suspend fun getUserProfileSnapshot(userId: String): UserProfile?
    suspend fun createOrUpdateProfile(profile: UserProfile)
    suspend fun updateProfileName(userId: String, name: String)
    suspend fun updateProfilePhoto(userId: String, photoUri: String?)
    suspend fun incrementReviewsCount(userId: String)
    suspend fun decrementReviewsCount(userId: String)
    suspend fun incrementFavoritesCount(userId: String)
    suspend fun decrementFavoritesCount(userId: String)
    suspend fun incrementVisitedCafesCount(userId: String)
    suspend fun updateStats(userId: String, reviewsCount: Int, favoritesCount: Int)
}

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val preferencesRepository: PreferencesRepository
) : IUserProfileRepository {

    override fun getUserProfile(userId: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfileFlow(userId)
    }

    override suspend fun getUserProfileSnapshot(userId: String): UserProfile? {
        return userProfileDao.getUserProfile(userId)
    }

    override suspend fun createOrUpdateProfile(profile: UserProfile) {
        val existing = userProfileDao.getUserProfile(profile.userId)
        if (existing == null) {
            userProfileDao.insert(profile)
        } else {
            userProfileDao.update(profile.copy(
                createdAt = existing.createdAt,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun updateProfileName(userId: String, name: String) {
        val profile = userProfileDao.getUserProfile(userId)
        if (profile != null) {
            userProfileDao.update(profile.copy(
                name = name,
                updatedAt = System.currentTimeMillis()
            ))
        }
        preferencesRepository.updateUserName(name)
    }

    override suspend fun updateProfilePhoto(userId: String, photoUri: String?) {
        val profile = userProfileDao.getUserProfile(userId)
        if (profile != null) {
            userProfileDao.update(profile.copy(
                photoUri = photoUri,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    override suspend fun incrementReviewsCount(userId: String) {
        userProfileDao.incrementReviewsCount(userId)
    }

    override suspend fun decrementReviewsCount(userId: String) {
        userProfileDao.decrementReviewsCount(userId)
    }

    override suspend fun incrementFavoritesCount(userId: String) {
        userProfileDao.incrementFavoritesCount(userId)
    }

    override suspend fun decrementFavoritesCount(userId: String) {
        userProfileDao.decrementFavoritesCount(userId)
    }

    override suspend fun incrementVisitedCafesCount(userId: String) {
        userProfileDao.incrementVisitedCafesCount(userId)
    }

    override suspend fun updateStats(userId: String, reviewsCount: Int, favoritesCount: Int) {
        val profile = userProfileDao.getUserProfile(userId)
        if (profile != null) {
            userProfileDao.updateStats(
                userId = userId,
                reviewsCount = reviewsCount,
                favoritesCount = favoritesCount,
                visitedCafesCount = profile.visitedCafesCount
            )
        }
    }

    suspend fun getOrCreateCurrentUserProfile(): UserProfile {
        val userId = preferencesRepository.userIdFlow.first()
        val userName = preferencesRepository.userPreferencesFlow.first().userName

        var profile = userProfileDao.getUserProfile(userId)

        if (profile == null) {
            profile = UserProfile(
                userId = userId,
                name = userName.ifEmpty { "User" },
                email = null,
                photoUri = null,
                reviewsCount = 0,
                favoritesCount = 0,
                visitedCafesCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            userProfileDao.insert(profile)
        }

        return profile
    }

}