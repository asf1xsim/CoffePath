package com.example.coffepath.ui.screens.map

import com.example.coffepath.model.Cafe
import com.example.coffepath.model.CafeMapItem
import com.example.coffepath.model.MapFilterState
import com.example.coffepath.model.MapSettings
import java.io.Serializable

sealed class MainMapUIState {
    object Loading : MainMapUIState()

    data class Success(
        val cafeItems: List<CafeMapItem> = emptyList(),
        val filteredItems: List<CafeMapItem> = emptyList(),
        val filter: MapFilterState = MapFilterState(),
        val mapSettings: MapSettings = MapSettings(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showFilterSheet: Boolean = false
    ) : MainMapUIState() {
        val hasActiveFilters: Boolean
            get() = filter.hasActiveFilters()
    }

    data class Error(val message: String) : MainMapUIState()
}