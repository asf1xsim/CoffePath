package com.example.coffepath.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverpassResponse(
    @Json(name = "elements")
    val elements: List<OverpassElement>
)

@JsonClass(generateAdapter = true)
data class OverpassElement(
    @Json(name = "type")
    val type: String,
    
    @Json(name = "id")
    val id: Long,

    @Json(name = "lat")
    val lat: Double?,

    @Json(name = "lon")
    val lon: Double?,

    @Json(name = "tags")
    val tags: Tags?
)

@JsonClass(generateAdapter = true)
data class Tags(
    @Json(name = "name")
    val name: String?,

    @Json(name = "amenity")
    val amenity: String?,

    @Json(name = "cuisine")
    val cuisine: String?,

    @Json(name = "opening_hours")
    val openingHours: String?,

    @Json(name = "addr:street")
    val street: String?,

    @Json(name = "addr:housenumber")
    val houseNumber: String?,

    @Json(name = "addr:city")
    val city: String?,

    @Json(name = "website")
    val website: String?,

    @Json(name = "phone")
    val phone: String?
)