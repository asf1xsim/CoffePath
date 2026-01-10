package com.example.coffepath.ui.screens.map.components

interface MapActions {
    fun loadCafesInArea(south: Double, west: Double, north: Double, east: Double)
    fun onCafeClicked(cafeId: Long)
    fun toggleFavorite(cafeId: Long, currentStatus: Boolean)
}