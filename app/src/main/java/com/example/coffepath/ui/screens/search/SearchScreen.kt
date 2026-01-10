package com.example.coffepath.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.model.*
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.screens.search.components.FilterDialog
import com.example.coffepath.ui.screens.search.components.SearchControls
import com.example.coffepath.ui.screens.search.components.SearchHistoryList
import com.example.coffepath.ui.screens.search.components.SearchResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigation: INavigationRouter,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SearchTopAppBar(
                query = uiState.query,
                onQueryChange = { viewModel.updateQuery(it) },
                onClearQuery = { viewModel.clearQuery() },
                onNavigateBack = { navigation.returnBack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchControls(
                filter = uiState.filter,
                onFilterClick = { viewModel.toggleFilters() }
            )

            when {
                uiState.isLoading -> {
                    LoadingState()
                }

                uiState.showEmptyState -> {
                    EmptySearchState(query = uiState.query)
                }

                uiState.hasResults -> {
                    SearchResultsList(
                        results = uiState.searchResults,
                        onCafeClick = { cafeId -> navigation.navigateToCafeDetail(cafeId) }
                    )
                }

                uiState.showHistory -> {
                    SearchHistoryList(
                        history = uiState.searchHistory,
                        onHistoryClick = { query -> viewModel.searchFromHistory(query) },
                        onHistoryRemove = { query -> viewModel.removeFromHistory(query) },
                        onClearHistory = { viewModel.clearSearchHistory() }
                    )
                }

                else -> {
                    InitialSearchState()
                }
            }
        }
    }

    if (uiState.showFilters) {
        FilterDialog(
            filter = uiState.filter,
            onFilterUpdate = { viewModel.updateFilter(it) },
            onDismiss = { viewModel.toggleFilters() },
            onClearFilters = { viewModel.clearFilters() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search cafes...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun SearchResultsList(
    results: List<SearchResult>,
    onCafeClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "${results.size} ${if (results.size == 1) "result" else "results"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(results) { result ->
            SearchResultCard(
                result = result,
                onClick = { onCafeClick(result.cafe.id) }
            )
        }
    }
}

@Composable
fun InitialSearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Search for cafes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptySearchState(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Try searching for \"$query\" with different filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}