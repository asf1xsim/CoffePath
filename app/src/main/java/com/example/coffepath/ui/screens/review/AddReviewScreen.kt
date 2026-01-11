package com.example.coffepath.ui.screens.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import com.example.coffepath.utils.PhotoPicker
import com.example.coffepath.ui.screens.review.components.DrinkTypeSelectionDialog
import com.example.coffepath.ui.screens.review.components.RatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    navigation: INavigationRouter,
    viewModel: AddReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDrinkTypeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            navigation.returnBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_review)) },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
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
                Text(stringResource(R.string.review_for, uiState.cafeName), style = MaterialTheme.typography.titleLarge)
            }

            SectionHeader(stringResource(R.string.add_photo))
            PhotoPicker(
                selectedImageUri = uiState.photoUri,
                onImageSelected = { viewModel.setPhoto(it, autoClassify = true) },
                onImageRemoved = viewModel::removePhoto,
                enabled = !uiState.isSaving
            )

            if (uiState.isClassifying) {
                StatusCard(stringResource(R.string.analyzing_drink), true)
            }

            uiState.detectedDrinkType?.takeIf { it != DrinkType.UNKNOWN }?.let { drinkType ->
                InfoCard(
                    title = stringResource(R.string.detected_drink),
                    content = drinkType.displayName,
                    onRefresh = viewModel::reclassifyDrink,
                    onClose = viewModel::clearDrinkType,
                    isLoading = uiState.isClassifying
                )
            }

            if (uiState.detectedDrinkType == null && uiState.photoUri != null) {
                OutlinedButton(onClick = { showDrinkTypeDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.select_drink_manually))
                }
            }

            HorizontalDivider()
            SectionHeader(stringResource(R.string.add_menu_optional))

            PhotoPicker(
                selectedImageUri = uiState.menuPhotoUri,
                onImageSelected = { viewModel.setMenuPhoto(it, autoRecognize = true) },
                onImageRemoved = viewModel::removeMenuPhoto,
                enabled = !uiState.isSaving
            )

            if (uiState.isRecognizingMenu) {
                StatusCard(stringResource(R.string.recognizing_menu), true)
            }

            uiState.menuOCRResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.menu_recognized),
                                    style = MaterialTheme.typography.labelMedium
                                )
                                if (result.detectedItems.isNotEmpty()) {
                                    Text(
                                        text = stringResource(R.string.menu_items_found, result.detectedItems.size),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { viewModel.rerecognizeMenu() },
                                    enabled = !uiState.isRecognizingMenu
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.rerecognize)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeMenuPhoto() }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                        }

                        if (result.detectedItems.isNotEmpty()) {
                            Divider()
                            Text(
                                text = stringResource(R.string.menu_items),
                                style = MaterialTheme.typography.labelSmall
                            )
                            result.detectedItems.take(5).forEach { item ->
                                Text(
                                    text = "• ${item.getFullDescription()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (result.detectedItems.size > 5) {
                                Text(
                                    text = stringResource(R.string.and_more, result.detectedItems.size - 5),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()
            SectionHeader(stringResource(R.string.rating_required))
            RatingBar(uiState.rating, onRatingChanged = viewModel::updateRating, enabled = !uiState.isSaving)

            SectionHeader(stringResource(R.string.comment_required))
            OutlinedTextField(
                value = uiState.comment,
                onValueChange = viewModel::updateComment,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text(stringResource(R.string.comment_hint)) },
                enabled = !uiState.isSaving
            )

            uiState.error?.let { error ->
                ErrorMessage(error, viewModel::clearError)
            }

            Button(
                onClick = viewModel::saveReview,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isValid && !uiState.isSaving
            ) {
                if (uiState.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text(stringResource(R.string.submit_review))
            }
        }
    }

    if (showDrinkTypeDialog) {
        DrinkTypeSelectionDialog(
            currentSelection = uiState.detectedDrinkType,
            onDismiss = { showDrinkTypeDialog = false },
            onSelect = { viewModel.setDrinkType(it) }
        )
    }
}
@Composable
private fun SectionHeader(text: String) = Text(text, style = MaterialTheme.typography.titleMedium)

@Composable
private fun StatusCard(text: String, showProgress: Boolean) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
        MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            Modifier.padding(16.dp),
            Arrangement.spacedBy(12.dp),
            Alignment.CenterVertically) {
            if (showProgress) CircularProgressIndicator(
                Modifier.size(24.dp),
                strokeWidth = 2.dp)
            Text(text)
        }
    }
}

@Composable
private fun InfoCard(title: String, content: String, onRefresh: () -> Unit, onClose: () -> Unit, isLoading: Boolean, extraContent: @Composable (() -> Unit)? = null) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        content,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row {
                    IconButton(onClick = onRefresh, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, null)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }
            extraContent?.invoke()
        }
    }
}

@Composable
private fun ErrorMessage(error: String, onDismiss: () -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer)) {
        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(error, Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, null)
            }
        }
    }
}