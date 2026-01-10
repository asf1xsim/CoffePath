package com.example.coffepath.ui.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffepath.model.CafeFilter

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