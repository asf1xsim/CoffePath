package com.example.coffepath.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = Cafe::class,
            parentColumns = ["id"],
            childColumns = ["cafe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cafe_id"])]
)
data class Review(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "cafe_id")
    val cafeId: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "rating")
    val rating: Float,

    @ColumnInfo(name = "comment")
    val comment: String,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,

    @ColumnInfo(name = "drink_type")
    val drinkType: String? = null,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    fun getFormattedDate(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun hasPhoto(): Boolean = !photoUri.isNullOrBlank()

    fun hasDrinkClassification(): Boolean = !drinkType.isNullOrBlank()
}