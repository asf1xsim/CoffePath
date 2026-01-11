package com.example.coffepath.viewmodel

import app.cash.turbine.test
import com.example.coffepath.model.*
import com.example.coffepath.repository.ISearchRepository
import com.example.coffepath.ui.screens.search.SearchViewModel
import com.example.coffepath.viewmodel.BaseViewModelTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchRepository: ISearchRepository

    @Before
    override fun setup() {
        super.setup()
        searchRepository = mockk(relaxed = true)

        coEvery { searchRepository.getSearchHistory() } returns emptyList()
    }

    @Test
    fun `updateQuery should update search query state`() = runTest {
        viewModel = SearchViewModel(searchRepository)

        viewModel.updateQuery("coffee")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("coffee", state.query)
        }
    }

    @Test
    fun `updateQuery should perform search after debounce`() = runTest {
        val searchResults = listOf(
            SearchResult(
                cafe = Cafe(
                    id = 1L,
                    name = "Coffee House",
                    latitude = 50.0,
                    longitude = 14.0,
                    isFavorite = false
                ),
                averageRating = 4.5f,
                reviewsCount = 10
            )
        )
        coEvery { searchRepository.searchCafes(any(), any(), any()) } returns searchResults

        viewModel = SearchViewModel(searchRepository)

        viewModel.updateQuery("coffee")
        advanceTimeBy(400)

        coVerify { searchRepository.searchCafes("coffee", any(), any()) }
    }

    @Test
    fun `clearQuery should reset search state`() = runTest {
        viewModel = SearchViewModel(searchRepository)
        viewModel.updateQuery("coffee")

        viewModel.clearQuery()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.query)
            assertFalse(state.isSearchActive)
            assertTrue(state.searchResults.isEmpty())
        }
    }

    @Test
    fun `updateFilter should apply filter and trigger search`() = runTest {
        val filter = CafeFilter(isFavorite = true)
        coEvery { searchRepository.searchCafes(any(), any(), any()) } returns emptyList()

        viewModel = SearchViewModel(searchRepository)
        viewModel.updateQuery("coffee")
        advanceTimeBy(400)

        viewModel.updateFilter(filter)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(filter, state.filter)
        }
        coVerify { searchRepository.searchCafes("coffee", filter, any()) }
    }

    @Test
    fun `clearFilters should reset filter state`() = runTest {
        viewModel = SearchViewModel(searchRepository)
        viewModel.updateFilter(CafeFilter(isFavorite = true))

        viewModel.clearFilters()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.filter.hasActiveFilters())
        }
    }

    @Test
    fun `clearSearchHistory should clear history in repository`() = runTest {
        coEvery { searchRepository.clearSearchHistory() } just Runs

        viewModel = SearchViewModel(searchRepository)

        viewModel.clearSearchHistory()

        coVerify { searchRepository.clearSearchHistory() }
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchHistory.isEmpty())
        }
    }

    @Test
    fun `removeFromHistory should remove specific query`() = runTest {
        coEvery { searchRepository.removeFromSearchHistory(any()) } just Runs

        viewModel = SearchViewModel(searchRepository)

        viewModel.removeFromHistory("old search")

        coVerify { searchRepository.removeFromSearchHistory("old search") }
    }

    @Test
    fun `updateSortOption should update sort and trigger search`() = runTest {
        val sortOption = CafeSortOption.RATING_DESC
        coEvery { searchRepository.searchCafes(any(), any(), any()) } returns emptyList()

        viewModel = SearchViewModel(searchRepository)
        viewModel.updateQuery("coffee")
        advanceTimeBy(400)

        viewModel.updateSortOption(sortOption)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sortOption, state.sortOption)
        }
        coVerify { searchRepository.searchCafes("coffee", any(), sortOption) }
    }

    @Test
    fun `toggleFilters should toggle filter visibility`() = runTest {
        viewModel = SearchViewModel(searchRepository)

        viewModel.toggleFilters()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.showFilters)
        }
    }

    @Test
    fun `searchFromHistory should trigger search with history query`() = runTest {
        coEvery { searchRepository.searchCafes(any(), any(), any()) } returns emptyList()

        viewModel = SearchViewModel(searchRepository)

        viewModel.searchFromHistory("previous search")
        advanceTimeBy(400)

        coVerify { searchRepository.searchCafes("previous search", any(), any()) }
    }

    @Test
    fun `search should add query to history`() = runTest {
        coEvery { searchRepository.searchCafes(any(), any(), any()) } returns emptyList()
        coEvery { searchRepository.addToSearchHistory(any()) } just Runs

        viewModel = SearchViewModel(searchRepository)

        viewModel.updateQuery("new search")
        advanceTimeBy(400)

        coVerify { searchRepository.addToSearchHistory("new search") }
    }
}