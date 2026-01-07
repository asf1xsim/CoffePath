package com.example.coffepath.ui.screens.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.mlkit.model.DrinkType
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.components.PhotoPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    navigation: INavigationRouter,
    viewModel: AddReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            navigation.returnBack()
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Review") },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.cafeName.isNotEmpty()) {
                Text(
                    text = "Review for ${uiState.cafeName}",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Divider()

            PhotoPicker(
                selectedImageUri = uiState.photoUri,
                onImageSelected = { uri ->
                    viewModel.setPhoto(uri, autoClassify = true)
                },
                onImageRemoved = {
                    viewModel.removePhoto()
                },
                enabled = !uiState.isSaving
            )

            if (uiState.isClassifying) {
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Analyzing drink...")
                    }
                }
            }

            uiState.detectedDrinkType?.let { drinkType ->
                if (drinkType != DrinkType.UNKNOWN) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Detected drink:",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = drinkType.displayName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { viewModel.reclassifyDrink() },
                                    enabled = !uiState.isClassifying
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Reclassify"
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.clearDrinkType() }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.detectedDrinkType == null && uiState.photoUri != null) {
                OutlinedButton(
                    onClick = { /* Показать диалог выбора */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select drink type manually")
                }
            }

            Divider()

            Text(
                text = "Rating *",
                style = MaterialTheme.typography.titleMedium
            )

            RatingBar(
                rating = uiState.rating,
                onRatingChanged = { viewModel.updateRating(it) },
                enabled = !uiState.isSaving
            )

            if (uiState.rating > 0) {
                Text(
                    text = "${uiState.rating} stars",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            Text(
                text = "Comment *",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.updateComment(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Share your experience...") },
                maxLines = 6,
                enabled = !uiState.isSaving
            )

            Divider()

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.saveReview() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isValid && !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isSaving) "Saving..." else "Submit Review")
            }

            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..maxStars) {
            IconButton(
                onClick = { if (enabled) onRatingChanged(i.toFloat()) },
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}