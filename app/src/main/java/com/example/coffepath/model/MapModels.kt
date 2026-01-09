package com.example.coffepath.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable

data class MapFilterState(
    val showOnlyFavorites: Boolean = false,
    val showOnlyWithReviews: Boolean = false,
    val minRating: Float? = null,
    val showOpenNow: Boolean = false
) : Serializable {
    fun hasActiveFilters(): Boolean {
        return showOnlyFavorites ||
                showOnlyWithReviews ||
                minRating != null ||
                showOpenNow
    }

    fun getActiveFiltersCount(): Int {
        var count = 0
        if (showOnlyFavorites) count++
        if (showOnlyWithReviews) count++
        if (minRating != null) count++
        if (showOpenNow) count++
        return count
    }

    fun clear(): MapFilterState {
        return MapFilterState()
    }
}

data class CafeMapItem(
    val cafe: Cafe,
    val averageRating: Float? = null,
    val reviewsCount: Int = 0,
    val isOpen: Boolean? = null
) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(cafe.latitude, cafe.longitude)
    }

    override fun getTitle(): String {
        return cafe.name
    }

    override fun getSnippet(): String {
        return buildSnippet()
    }

    override fun getZIndex(): Float? {
        return 0.0f
    }

    private fun buildSnippet(): String {
        val parts = mutableListOf<String>()

        averageRating?.let { rating ->
            parts.add("★ ${String.format("%.1f", rating)}")
        }

        if (reviewsCount > 0) {
            parts.add("$reviewsCount reviews")
        }

        if (cafe.isFavorite) {
            parts.add("♥ Favorite")
        }

        return if (parts.isNotEmpty()) {
            parts.joinToString(" • ")
        } else {
            cafe.address ?: "No info"
        }
    }

    fun matchesFilter(filter: MapFilterState): Boolean {
        if (filter.showOnlyFavorites && !cafe.isFavorite) {
            return false
        }

        if (filter.showOnlyWithReviews && reviewsCount == 0) {
            return false
        }

        if (filter.minRating != null) {
            if (averageRating == null || averageRating < filter.minRating) {
                return false
            }
        }

        if (filter.showOpenNow && isOpen != true) {
            return false
        }

        return true
    }
}

enum class MapType(val displayName: String) {
    NORMAL("Normal"),
    SATELLITE("Satellite"),
    TERRAIN("Terrain"),
    HYBRID("Hybrid");

    companion object {
        fun getDefault(): MapType = NORMAL
    }
}

data class MapSettings(
    val mapType: MapType = MapType.NORMAL,
    val showTraffic: Boolean = false,
    val enableClustering: Boolean = true,
    val clusterRadius: Int = 100
) : Serializable