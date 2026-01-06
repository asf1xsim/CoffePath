package com.example.coffepath.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.model.Cafe
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.repository.ICafeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CafeDetailUIState {
    object Loading : CafeDetailUIState()
    data class Success(val cafe: Cafe) : CafeDetailUIState()
    object NotFound : CafeDetailUIState()
}

@HiltViewModel
class CafeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ICafeRepository
) : ViewModel() {

    private val cafeId: Long = savedStateHandle.get<Long>("cafeId") ?: -1L

    private val _uiState = MutableStateFlow<CafeDetailUIState>(CafeDetailUIState.Loading)
    val uiState: StateFlow<CafeDetailUIState> = _uiState.asStateFlow()

    init {
        loadCafe()
    }

    private fun loadCafe() {
        viewModelScope.launch {
            val cafe = repository.getCafeById(cafeId)
            _uiState.value = if (cafe != null) {
                CafeDetailUIState.Success(cafe)
            } else {
                CafeDetailUIState.NotFound
            }
        }
    }

    fun toggleFavorite(currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(cafeId, !currentStatus)
            loadCafe()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeDetailScreen(
    navigation: INavigationRouter,
    viewModel: CafeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cafe Details") },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is CafeDetailUIState.Success) {
                        val cafe = (uiState as CafeDetailUIState.Success).cafe
                        IconButton(onClick = { viewModel.toggleFavorite(cafe.isFavorite) }) {
                            Icon(
                                if (cafe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (cafe.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CafeDetailUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CafeDetailUIState.Success -> {
                CafeDetailContent(
                    cafe = state.cafe,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is CafeDetailUIState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cafe not found")
                }
            }
        }
    }
}

@Composable
fun CafeDetailContent(
    cafe: Cafe,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name
        Text(
            text = cafe.name,
            style = MaterialTheme.typography.headlineMedium
        )

        // Address
        cafe.address?.let { address ->
            DetailRow(
                label = "Address",
                value = address
            )
        }

        // Opening hours
        cafe.openingHours?.let { hours ->
            DetailRow(
                label = "Opening Hours",
                value = hours
            )
        }

        // Phone
        cafe.phone?.let { phone ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Text(text = phone)
            }
        }

        // Website
        cafe.website?.let { website ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Web, contentDescription = null)
                Text(text = website)
            }
        }

        // Coordinates
        DetailRow(
            label = "Coordinates",
            value = "${cafe.latitude}, ${cafe.longitude}"
        )
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}