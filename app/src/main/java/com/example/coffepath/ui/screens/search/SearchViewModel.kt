package com.example.coffepath.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.model.*
import com.example.coffepath.repository.ISearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


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

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: ISearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadSearchHistory()

        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _uiState.update {
                            it.copy(
                                searchResults = emptyList(),
                                isSearchActive = false
                            )
                        }
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSearchActive = true) }

            try {
                val results = searchRepository.searchCafes(
                    query = query,
                    filter = _uiState.value.filter,
                    sortOption = _uiState.value.sortOption
                )

                _uiState.update {
                    it.copy(
                        searchResults = results,
                        isLoading = false,
                        error = null
                    )
                }

                if (query.isNotBlank()) {
                    searchRepository.addToSearchHistory(query)
                    loadSearchHistory()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchFromHistory(query: String) {
        updateQuery(query)
    }

    fun clearQuery() {
        updateQuery("")
        _uiState.update {
            it.copy(
                searchResults = emptyList(),
                isSearchActive = false
            )
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val history = searchRepository.getSearchHistory()
                _uiState.update { it.copy(searchHistory = history) }
            } catch (e: Exception) {

            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepository.clearSearchHistory()
            _uiState.update { it.copy(searchHistory = emptyList()) }
        }
    }

    fun removeFromHistory(query: String) {
        viewModelScope.launch {
            searchRepository.removeFromSearchHistory(query)
            loadSearchHistory()
        }
    }

    fun updateFilter(filter: CafeFilter) {
        _uiState.update { it.copy(filter = filter) }
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    fun clearFilters() {
        updateFilter(CafeFilter())
    }

    fun updateSortOption(sortOption: CafeSortOption) {
        _uiState.update { it.copy(sortOption = sortOption) }

        val query = _uiState.value.query
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    fun toggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}