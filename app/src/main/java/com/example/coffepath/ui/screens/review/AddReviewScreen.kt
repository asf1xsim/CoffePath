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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.R
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
            // Error handling can be added here if needed
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_review)) },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                    text = stringResource(R.string.review_for, uiState.cafeName),
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
                        Text(stringResource(R.string.analyzing_drink))
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
                                    text = stringResource(R.string.detected_drink),
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
                                        contentDescription = stringResource(R.string.reclassify)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.clearDrinkType() }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.detectedDrinkType == null && uiState.photoUri != null) {
                OutlinedButton(
                    onClick = { /* Show dialog for manual selection */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_drink_manually))
                }
            }

            Divider()

            Text(
                text = stringResource(R.string.rating_required),
                style = MaterialTheme.typography.titleMedium
            )

            RatingBar(
                rating = uiState.rating,
                onRatingChanged = { viewModel.updateRating(it) },
                enabled = !uiState.isSaving
            )

            if (uiState.rating > 0) {
                Text(
                    text = stringResource(R.string.stars, uiState.rating.toInt().toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            Text(
                text = stringResource(R.string.comment_required),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.updateComment(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text(stringResource(R.string.comment_hint)) },
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
                                contentDescription = stringResource(R.string.dismiss),
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
                Text(
                    stringResource(
                        if (uiState.isSaving) R.string.saving else R.string.submit_review
                    )
                )
            }

            Text(
                text = stringResource(R.string.required_fields),
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
                    contentDescription = stringResource(R.string.rating),
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