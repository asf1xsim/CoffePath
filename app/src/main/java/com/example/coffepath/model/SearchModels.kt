package com.example.coffepath.model

import java.io.Serializable

data class CafeFilter(
    val minRating: Float? = null,
    val maxDistance: Float? = null,
    val isOpenNow: Boolean = false,
    val hasReviews: Boolean = false,
    val isFavorite: Boolean = false
) : Serializable {
    fun hasActiveFilters(): Boolean {
        return minRating != null ||
                maxDistance != null ||
                isOpenNow ||
                hasReviews ||
                isFavorite
    }

    fun clear(): CafeFilter {
        return CafeFilter()
    }

    fun getActiveFiltersCount(): Int {
        var count = 0
        if (minRating != null) count++
        if (maxDistance != null) count++
        if (isOpenNow) count++
        if (hasReviews) count++
        if (isFavorite) count++
        return count
    }
}

enum class CafeSortOption(val displayName: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    RATING_DESC("Rating (High to Low)"),
    RATING_ASC("Rating (Low to High)"),
    DISTANCE_ASC("Distance (Near to Far)"),
    DISTANCE_DESC("Distance (Far to Near)"),
    REVIEWS_COUNT_DESC("Most Reviews"),
    REVIEWS_COUNT_ASC("Least Reviews");

    companion object {
        fun getDefaultSort(): CafeSortOption = NAME_ASC
    }
}

data class SearchResult(
    val cafe: Cafe,
    val averageRating: Float? = null,
    val reviewsCount: Int = 0,
    val distance: Float? = null
) : Serializable {

    fun matchesFilter(filter: CafeFilter): Boolean {
        if (filter.minRating != null && (averageRating == null || averageRating < filter.minRating)) {
            return false
        }

        if (filter.maxDistance != null && (distance == null || distance > filter.maxDistance)) {
            return false
        }

        if (filter.hasReviews && reviewsCount == 0) {
            return false
        }

        if (filter.isFavorite && !cafe.isFavorite) {
            return false
        }
        return true
    }
}

data class SearchHistory(
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    fun getFormattedDate(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} min ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            diff < 604800_000 -> "${diff / 86400_000} days ago"
            else -> "Long ago"
        }
    }
}