package com.example.coffepath.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable

@Entity(tableName = "cafes")
data class Cafe(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "address")
    val address: String? = null,
    @ColumnInfo(name = "opening_hours")
    val openingHours: String? = null,
    @ColumnInfo(name = "website")
    val website: String? = null,
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
) : Serializable, ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String? {
        return address
    }

    override fun getZIndex(): Float? {
        return 0.0f
    }
}