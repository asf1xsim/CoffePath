package com.example.coffepath.ui.screens.review.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.coffepath.R
import com.example.coffepath.mlkit.model.DrinkType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkTypeSelectionDialog(
    currentSelection: DrinkType?,
    onDismiss: () -> Unit,
    onSelect: (DrinkType) -> Unit
) {
    val availableDrinks = remember {
        DrinkType.entries.filter { it != DrinkType.UNKNOWN }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.select_drink_type))
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(availableDrinks) { drinkType ->
                    DrinkTypeListItem(
                        drinkType = drinkType,
                        isSelected = drinkType == currentSelection,
                        onClick = {
                            onSelect(drinkType)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DrinkTypeListItem(
    drinkType: DrinkType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = drinkType.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = stringResource(R.string.selected),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}