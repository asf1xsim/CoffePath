package com.example.coffepath.ui.screens.detail

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.R
import com.example.coffepath.model.Cafe
import com.example.coffepath.model.Review
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.components.CafeMenuSection
import com.example.coffepath.ui.screens.detail.components.DetailRow
import com.example.coffepath.ui.screens.detail.components.ReviewCard

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
                title = { Text(stringResource(R.string.cafe_details)) },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (uiState is CafeDetailUIState.Success) {
                        val cafe = (uiState as CafeDetailUIState.Success).cafe
                        IconButton(onClick = { viewModel.toggleFavorite(cafe.isFavorite) }) {
                            Icon(
                                if (cafe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(R.string.favorites),
                                tint = if (cafe.isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is CafeDetailUIState.Success) {
                val cafe = (uiState as CafeDetailUIState.Success).cafe
                FloatingActionButton(
                    onClick = { navigation.navigateToAddReview(cafe.id) }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_review)
                    )
                }
            }
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
                    reviews = state.reviews,
                    averageRating = state.averageRating,
                    reviewCount = state.reviewCount,
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
                    Text(stringResource(R.string.cafe_not_found))
                }
            }
        }
    }
}

@Composable
fun CafeDetailContent(
    cafe: Cafe,
    reviews: List<Review>,
    averageRating: Float?,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = cafe.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        if (averageRating != null && reviewCount > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format("%.1f", averageRating),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "($reviewCount ${if (reviewCount == 1) stringResource(R.string.review) else stringResource(R.string.reviews_plural)})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        cafe.address?.let { address ->
            item {
                DetailRow(label = stringResource(R.string.address), value = address)
            }
        }

        cafe.openingHours?.let { hours ->
            item {
               DetailRow(label = stringResource(R.string.opening_hours), value = hours)
            }
        }

        cafe.phone?.let { phone ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text(text = phone)
                }
            }
        }

        cafe.website?.let { website ->
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Web, contentDescription = null)
                    Text(
                        text = website,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        item {
            DetailRow(
                label = stringResource(R.string.coordinates),
                value = "${cafe.latitude}, ${cafe.longitude}"
            )
        }

        cafe.menuJson?.let { menuJson ->
            item {
                CafeMenuSection(menuJson = menuJson)
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(
                text = stringResource(R.string.reviews_count, reviews.size),
            style = MaterialTheme.typography.titleLarge
            )
        }

        if (reviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_reviews),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.be_first_review),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(reviews) { review ->
                ReviewCard(review = review)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

