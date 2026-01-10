package com.example.coffepath.repository

import com.example.coffepath.database.CafeDao
import com.example.coffepath.database.ReviewDao
import com.example.coffepath.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ISearchRepository {
    suspend fun searchCafes(
        query: String,
        filter: CafeFilter = CafeFilter(),
        sortOption: CafeSortOption = CafeSortOption.NAME_ASC
    ): List<SearchResult>

    suspend fun addToSearchHistory(query: String)
    suspend fun getSearchHistory(): List<SearchHistory>
    suspend fun clearSearchHistory()
    suspend fun removeFromSearchHistory(query: String)
}

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val cafeDao: CafeDao,
    private val reviewDao: ReviewDao,
    private val preferencesRepository: PreferencesRepository
) : ISearchRepository {
    override suspend fun searchCafes(
        query: String,
        filter: CafeFilter,
        sortOption: CafeSortOption
    ): List<SearchResult> {
        val cafes = cafeDao.getAll().first()

        val filteredCafes = if (query.isBlank()) {
            cafes
        } else {
            cafes.filter { cafe ->
                cafe.name.contains(query, ignoreCase = true) ||
                        cafe.address?.contains(query, ignoreCase = true) == true
            }
        }
        val searchResults = filteredCafes.map { cafe ->
            val averageRating = reviewDao.getAverageRatingForCafe(cafe.id)
            val reviewsCount = reviewDao.getReviewCountForCafe(cafe.id)

            SearchResult(
                cafe = cafe,
                averageRating = averageRating,
                reviewsCount = reviewsCount,
                distance = null
            )
        }

        val filtered = if (filter.hasActiveFilters()) {
            searchResults.filter { it.matchesFilter(filter) }
        } else {
            searchResults
        }

        return sortResults(filtered, sortOption)
    }

    private fun sortResults(
        results: List<SearchResult>,
        sortOption: CafeSortOption
    ): List<SearchResult> {
        return when (sortOption) {
            CafeSortOption.NAME_ASC ->
                results.sortedBy { it.cafe.name.lowercase() }

            CafeSortOption.NAME_DESC ->
                results.sortedByDescending { it.cafe.name.lowercase() }

            CafeSortOption.RATING_DESC ->
                results.sortedByDescending { it.averageRating ?: 0f }

            CafeSortOption.RATING_ASC ->
                results.sortedBy { it.averageRating ?: 0f }

            CafeSortOption.DISTANCE_ASC ->
                results.sortedBy { it.distance ?: Float.MAX_VALUE }

            CafeSortOption.DISTANCE_DESC ->
                results.sortedByDescending { it.distance ?: 0f }

            CafeSortOption.REVIEWS_COUNT_DESC ->
                results.sortedByDescending { it.reviewsCount }

            CafeSortOption.REVIEWS_COUNT_ASC ->
                results.sortedBy { it.reviewsCount }
        }
    }

    override suspend fun addToSearchHistory(query: String) {
        if (query.isBlank()) return

        val history = getSearchHistory().toMutableList()

        history.removeAll { it.query.equals(query, ignoreCase = true) }

        history.add(0, SearchHistory(query))

        val limitedHistory = history.take(20)

        preferencesRepository.updateSearchHistory(
            limitedHistory.map { it.query }
        )
    }

    override suspend fun getSearchHistory(): List<SearchHistory> {
        val queries = preferencesRepository.userPreferencesFlow.first().searchHistory
        return queries.map { SearchHistory(it) }
    }

    override suspend fun clearSearchHistory() {
        preferencesRepository.updateSearchHistory(emptyList())
    }

    override suspend fun removeFromSearchHistory(query: String) {
        val history = getSearchHistory().toMutableList()
        history.removeAll { it.query.equals(query, ignoreCase = true) }

        preferencesRepository.updateSearchHistory(
            history.map { it.query }
        )
    }
}