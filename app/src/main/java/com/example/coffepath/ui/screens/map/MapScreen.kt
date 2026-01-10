package com.example.coffepath.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.R
import com.example.coffepath.model.CafeMapItem
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.screens.map.components.MapFilterBottomSheet
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigation: INavigationRouter,
    viewModel: MainMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MapTopAppBar(
                navigation = navigation,
                hasActiveFilters = (uiState as? MainMapUIState.Success)?.hasActiveFilters ?: false,
                filterCount = (uiState as? MainMapUIState.Success)?.filter?.getActiveFiltersCount() ?: 0,
                onFilterClick = { viewModel.toggleFilterSheet() }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MainMapUIState.Loading -> {
                LoadingMapState(paddingValues)
            }

            is MainMapUIState.Success -> {
                MapContent(
                    modifier = Modifier.padding(paddingValues),
                    cafeItems = state.filteredItems,
                    enableClustering = state.mapSettings.enableClustering,
                    isLoading = state.isLoading,
                    error = state.error,
                    onLoadCafes = { south, west, north, east ->
                        viewModel.loadCafesInArea(south, west, north, east)
                    },
                    onCafeClick = { cafeId ->
                        navigation.navigateToCafeDetail(cafeId)
                    },
                    onToggleFavorite = { cafeId, isFavorite ->
                        viewModel.toggleFavorite(cafeId, isFavorite)
                    }
                )

                if (state.showFilterSheet) {
                    MapFilterBottomSheet(
                        filter = state.filter,
                        onFilterUpdate = { viewModel.updateFilter(it) },
                        onDismiss = { viewModel.toggleFilterSheet() },
                        onClearFilters = { viewModel.clearFilters() }
                    )
                }
            }

            is MainMapUIState.Error -> {
                ErrorMapState(paddingValues, state.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopAppBar(
    navigation: INavigationRouter,
    hasActiveFilters: Boolean,
    filterCount: Int,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            BadgedBox(
                badge = {
                    if (hasActiveFilters) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text("$filterCount")
                        }
                    }
                }
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filters),
                        tint = if (hasActiveFilters) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            IconButton(onClick = { navigation.navigateToSearch() }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }

            IconButton(onClick = { navigation.navigateToFavorites() }) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.favorites)
                )
            }

            IconButton(onClick = { navigation.navigateToProfile() }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile)
                )
            }
        }
    )
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    cafeItems: List<CafeMapItem>,
    enableClustering: Boolean,
    isLoading: Boolean,
    error: String?,
    onLoadCafes: (Double, Double, Double, Double) -> Unit,
    onCafeClick: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit
) {
    val defaultPosition = LatLng(49.1951, 16.6068)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 12f)
    }

    var hasLoadedInitialData by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            )
        ) {
            if (enableClustering) {
                Clustering(
                    items = cafeItems,
                    onClusterItemClick = { item ->
                        onCafeClick(item.cafe.id)
                        true
                    }
                )
            } else {
                cafeItems.forEach { item ->
                    Marker(
                        state = MarkerState(position = item.position),
                        title = item.title,
                        snippet = item.snippet,
                        onClick = {
                            onCafeClick(item.cafe.id)
                            true
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null) {
                    onLoadCafes(
                        bounds.southwest.latitude,
                        bounds.southwest.longitude,
                        bounds.northeast.latitude,
                        bounds.northeast.longitude
                    )
                    hasLoadedInitialData = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                stringResource(
                    if (hasLoadedInitialData) R.string.reload_cafes else R.string.load_cafes
                )
            )
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }

        error?.let {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { /* Clear error */ }) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            ) {
                Text(text = it)
            }
        }
    }
}

@Composable
fun LoadingMapState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(stringResource(R.string.loading_map))
        }
    }
}

@Composable
fun ErrorMapState(paddingValues: PaddingValues, message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.error_loading_map),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}