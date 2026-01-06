package com.example.coffepath.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,

    @ColumnInfo(name = "reviews_count")
    val reviewsCount: Int = 0,

    @ColumnInfo(name = "favorites_count")
    val favoritesCount: Int = 0,

    @ColumnInfo(name = "visited_cafes_count")
    val visitedCafesCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) : Serializable {
    fun hasPhoto(): Boolean = !photoUri.isNullOrBlank()

    fun getInitials(): String {
        val nameParts = name.trim().split(" ")
        return when {
            nameParts.size >= 2 -> {
                "${nameParts[0].first().uppercase()}${nameParts[1].first().uppercase()}"
            }
            nameParts.size == 1 && nameParts[0].isNotEmpty() -> {
                nameParts[0].take(2).uppercase()
            }
            else -> "U"
        }
    }

    fun getFormattedCreatedDate(): String {
        val date = java.util.Date(createdAt)
        val format = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun updateStats(reviews: Int, favorites: Int, visited: Int): UserProfile {
        return this.copy(
            reviewsCount = reviews,
            favoritesCount = favorites,
            visitedCafesCount = visited,
            updatedAt = System.currentTimeMillis()
        )
    }
}