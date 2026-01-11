package com.example.coffepath.utils

import android.util.Log
import com.example.coffepath.model.Cafe
import com.example.coffepath.repository.IUserProfileRepository
import com.example.coffepath.repository.PreferencesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CafeVisitTracker @Inject constructor(
    private val locationService: LocationService,
    private val userProfileRepository: IUserProfileRepository,
    private val preferencesRepository: PreferencesRepository
) {
    private val visitedCafesInSession = mutableSetOf<Long>()
    suspend fun checkCafeVisit(userLocation: LatLng, cafes: List<Cafe>) {
        cafes.forEach { cafe ->
            checkSingleCafeVisit(userLocation, cafe)
        }
    }
    suspend fun checkSingleCafeVisit(userLocation: LatLng, cafe: Cafe) {
        if (visitedCafesInSession.contains(cafe.id)) {
            return
        }

        val isWithinRadius = locationService.isWithinRadius(
            userLat = userLocation.latitude,
            userLon = userLocation.longitude,
            targetLat = cafe.latitude,
            targetLon = cafe.longitude
        )

        if (isWithinRadius) {
            Log.d(TAG, "User visited cafe: ${cafe.name} (ID: ${cafe.id})")
            recordCafeVisit(cafe.id)
        }
    }

    private suspend fun recordCafeVisit(cafeId: Long) {
        try {
            visitedCafesInSession.add(cafeId)
            val userId = preferencesRepository.userIdFlow.first()
            userProfileRepository.incrementVisitedCafesCount(userId)

            Log.d(TAG, "Cafe visit recorded for cafe ID: $cafeId, user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record cafe visit", e)
        }
    }

    fun clearVisitedSession() {
        visitedCafesInSession.clear()
        Log.d(TAG, "Visited cafes session cleared")
    }

    fun getVisitedInSession(): Set<Long> {
        return visitedCafesInSession.toSet()
    }

    companion object {
        private const val TAG = "CafeVisitTracker"
    }
}