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
                onNavigateBack = { navigation.returnBack() },
                filterCount = uiState.filter.getActiveFiltersCount()
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
                sortOption = uiState.sortOption,
                onFilterClick = { viewModel.toggleFilters() },
                onSortClick = { /* Show sort dialog */ }
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
    onNavigateBack: () -> Unit,
    filterCount: Int
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
fun SearchControls(
    filter: CafeFilter,
    sortOption: CafeSortOption,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = filter.hasActiveFilters(),
            onClick = onFilterClick,
            label = {
                Text(
                    if (filter.hasActiveFilters()) {
                        "Filters (${filter.getActiveFiltersCount()})"
                    } else {
                        "Filters"
                    }
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
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
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = result.cafe.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            result.cafe.address?.let { address ->
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (result.averageRating != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", result.averageRating),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (result.reviewsCount > 0) {
                    Text(
                        text = "${result.reviewsCount} ${if (result.reviewsCount == 1) "review" else "reviews"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (result.cafe.isFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHistoryList(
    history: List<SearchHistory>,
    onHistoryClick: (String) -> Unit,
    onHistoryRemove: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onClearHistory) {
                Text("Clear All")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(history) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryClick(item.query) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column {
                            Text(
                                text = item.query,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = item.getFormattedDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = { onHistoryRemove(item.query) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Divider()
            }
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

@Composable
fun FilterDialog(
    filter: CafeFilter,
    onFilterUpdate: (CafeFilter) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit
) {
    var localFilter by remember { mutableStateOf(filter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Minimum Rating: ${localFilter.minRating?.let { String.format("%.1f", it) } ?: "Any"}")
                Slider(
                    value = localFilter.minRating ?: 0f,
                    onValueChange = { localFilter = localFilter.copy(minRating = if (it > 0) it else null) },
                    valueRange = 0f..5f,
                    steps = 9
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Has Reviews")
                    Switch(
                        checked = localFilter.hasReviews,
                        onCheckedChange = { localFilter = localFilter.copy(hasReviews = it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Favorites Only")
                    Switch(
                        checked = localFilter.isFavorite,
                        onCheckedChange = { localFilter = localFilter.copy(isFavorite = it) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFilterUpdate(localFilter)
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    localFilter = CafeFilter()
                    onClearFilters()
                }) {
                    Text("Clear")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}