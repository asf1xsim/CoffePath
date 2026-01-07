package com.example.coffepath.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.model.Cafe
import com.example.coffepath.navigation.INavigationRouter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigation: INavigationRouter,
    viewModel: MainMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CoffeePath") },
                actions = {
                    IconButton(onClick = { navigation.navigateToMLKitTest() }) {
                        Icon(Icons.Default.Science, contentDescription = "Test ML Kit")
                    }
                    IconButton(onClick = { navigation.navigateToSearch() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navigation.navigateToFavorites() }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MainMapUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MainMapUIState.Success -> {
                MainMapContent(
                    modifier = Modifier.padding(paddingValues),
                    cafes = state.cafes,
                    isLoading = state.isLoading,
                    error = state.error,
                    actions = viewModel,
                    navigation = navigation
                )
            }
            is MainMapUIState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: ${state.message}")
                }
            }
        }
    }
}

@Composable
fun MainMapContent(
    modifier: Modifier = Modifier,
    cafes: List<Cafe>,
    isLoading: Boolean,
    error: String?,
    actions: MapActions,
    navigation: INavigationRouter
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
                myLocationButtonEnabled = false
            )
        ) {
            cafes.forEach { cafe ->
                Marker(
                    state = MarkerState(position = cafe.position),
                    title = cafe.title,
                    snippet = cafe.snippet,
                    onClick = {
                        navigation.navigateToCafeDetail(cafe.id)
                        true
                    }
                )
            }
        }

        Button(
            onClick = {
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                if (bounds != null) {
                    actions.loadCafesInArea(
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
            Text(if (hasLoadedInitialData) "Reload Cafes in Area" else "Load Cafes")
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
        }

        error?.let {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text(text = it)
            }
        }
    }
}