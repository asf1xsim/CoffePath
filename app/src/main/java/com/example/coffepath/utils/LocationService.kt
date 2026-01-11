package com.example.coffepath.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.tasks.await

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        UPDATE_INTERVAL_MS
    ).apply {
        setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MS)
        setWaitForAccurateLocation(false)
    }.build()

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getCurrentLocation(): Result<LatLng> {
        if (!hasLocationPermission()) {
            return Result.failure(SecurityException("Location permission not granted"))
        }

        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                Result.success(LatLng(location.latitude, location.longitude))
            } else {
                Result.failure(Exception("Location is null"))
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception getting location", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location", e)
            Result.failure(e)
        }
    }

    fun getLocationUpdates(): Flow<LatLng> = callbackFlow {
        if (!hasLocationPermission()) {
            Log.e(TAG, "Location permission not granted")
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                    trySend(LatLng(location.latitude, location.longitude))
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception requesting location updates", e)
            close(e)
        }

        awaitClose {
            Log.d(TAG, "Stopping location updates")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun isWithinRadius(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Double = VISIT_RADIUS_METERS
    ): Boolean {
        val distance = calculateDistance(userLat, userLon, targetLat, targetLon)
        return distance <= radiusMeters
    }

    companion object {
        private const val TAG = "LocationService"
        private const val UPDATE_INTERVAL_MS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_MS = 5000L
        const val VISIT_RADIUS_METERS = 50.0
    }
}