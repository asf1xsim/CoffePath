package com.example.coffepath.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coffepath.R
import com.example.coffepath.model.MapFilterState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapFilterBottomSheet(
    filter: MapFilterState,
    onFilterUpdate: (MapFilterState) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit
) {
    var localFilter by remember { mutableStateOf(filter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filters),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    localFilter = MapFilterState()
                    onClearFilters()
                }) {
                    Text(stringResource(R.string.clear_all))
                }
            }

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(R.string.favorites_only)) },
                supportingContent = { Text(stringResource(R.string.show_favorites_only_hint)) },
                leadingContent = {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Switch(
                        checked = localFilter.showOnlyFavorites,
                        onCheckedChange = { localFilter = localFilter.copy(showOnlyFavorites = it) }
                    )
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.has_reviews)) },
                supportingContent = { Text(stringResource(R.string.show_with_reviews_hint)) },
                leadingContent = {
                    Icon(Icons.Default.RateReview, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    Switch(
                        checked = localFilter.showOnlyWithReviews,
                        onCheckedChange = { localFilter = localFilter.copy(showOnlyWithReviews = it) }
                    )
                }
            )

            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.minimum_rating), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = localFilter.minRating?.let { it.roundToInt().toString() } ?: stringResource(R.string.rating_any),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = localFilter.minRating ?: 0f,
                    onValueChange = { rawValue ->
                        val roundedValue = rawValue.roundToInt().toFloat()
                        localFilter = localFilter.copy(minRating = if (roundedValue > 0) roundedValue else null)
                    },
                    valueRange = 0f..5f,
                    steps = 4
                )
            }
            Button(
                onClick = {
                    onFilterUpdate(localFilter)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply_filters))
            }
        }
    }
}