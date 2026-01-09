package com.example.coffepath.ui.screens.search

import com.example.coffepath.model.CafeFilter
import com.example.coffepath.model.CafeSortOption
import com.example.coffepath.model.SearchHistory
import com.example.coffepath.model.SearchResult

data class SearchUIState(
    val query: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val searchHistory: List<SearchHistory> = emptyList(),
    val filter: CafeFilter = CafeFilter(),
    val sortOption: CafeSortOption = CafeSortOption.NAME_ASC,
    val isLoading: Boolean = false,
    val isSearchActive: Boolean = false,
    val showFilters: Boolean = false,
    val error: String? = null
) {
    val hasResults: Boolean
        get() = searchResults.isNotEmpty()

    val showEmptyState: Boolean
        get() = !isLoading && !hasResults && isSearchActive && query.isNotBlank()

    val showHistory: Boolean
        get() = !isSearchActive && searchHistory.isNotEmpty()
}