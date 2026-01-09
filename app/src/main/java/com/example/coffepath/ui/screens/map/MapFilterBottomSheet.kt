package com.example.coffepath.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coffepath.model.MapFilterState

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Map Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = {
                    localFilter = MapFilterState()
                    onClearFilters()
                }) {
                    Text("Clear All")
                }
            }

            Divider()

            FilterSwitch(
                icon = Icons.Default.Favorite,
                title = "Favorites Only",
                subtitle = "Show only cafes marked as favorite",
                checked = localFilter.showOnlyFavorites,
                onCheckedChange = {
                    localFilter = localFilter.copy(showOnlyFavorites = it)
                }
            )

            Divider()

            FilterSwitch(
                icon = Icons.Default.RateReview,
                title = "Has Reviews",
                subtitle = "Show only cafes with reviews",
                checked = localFilter.showOnlyWithReviews,
                onCheckedChange = {
                    localFilter = localFilter.copy(showOnlyWithReviews = it)
                }
            )

            Divider()

            FilterSwitch(
                icon = Icons.Default.Schedule,
                title = "Open Now",
                subtitle = "Show only cafes open right now",
                checked = localFilter.showOpenNow,
                onCheckedChange = {
                    localFilter = localFilter.copy(showOpenNow = it)
                },
                enabled = false
            )

            Divider()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Minimum Rating",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = localFilter.minRating?.let {
                            String.format("%.1f", it)
                        } ?: "Any",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = localFilter.minRating ?: 0f,
                    onValueChange = {
                        localFilter = localFilter.copy(
                            minRating = if (it > 0) it else null
                        )
                    },
                    valueRange = 0f..5f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(6) { index ->
                        Text(
                            text = if (index == 0) "Any" else "$index★",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onFilterUpdate(localFilter)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }
        }
    }
}

@Composable
fun FilterSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    }
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}