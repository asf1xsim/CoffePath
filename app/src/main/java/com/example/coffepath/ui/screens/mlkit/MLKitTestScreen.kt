package com.example.coffepath.ui.screens.mlkit

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffepath.mlkit.DrinkClassifier
import com.example.coffepath.mlkit.MenuOCRReader
import com.example.coffepath.mlkit.model.DrinkClassificationResult
import com.example.coffepath.mlkit.model.MenuOCRResult
import com.example.coffepath.mlkit.toReadableString
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.components.PhotoPicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MLKitTestUIState {
    object Idle : MLKitTestUIState()
    object Loading : MLKitTestUIState()
    data class DrinkClassified(val result: DrinkClassificationResult) : MLKitTestUIState()
    data class MenuRecognized(val result: MenuOCRResult) : MLKitTestUIState()
    data class Error(val message: String) : MLKitTestUIState()
}

@HiltViewModel
class MLKitTestViewModel @Inject constructor(
    private val drinkClassifier: DrinkClassifier,
    private val menuOCRReader: MenuOCRReader
) : ViewModel() {
    private val _uiState = MutableStateFlow<MLKitTestUIState>(MLKitTestUIState.Idle)
    val uiState: StateFlow<MLKitTestUIState> = _uiState.asStateFlow()

    fun classifyDrink(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = MLKitTestUIState.Loading

            drinkClassifier.classifyDrink(imageUri)
                .onSuccess { result ->
                    _uiState.value = MLKitTestUIState.DrinkClassified(result)
                }
                .onFailure { exception ->
                    _uiState.value = MLKitTestUIState.Error(
                        exception.message ?: "Failed to classify drink"
                    )
                }
        }
    }
    fun recognizeMenu(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = MLKitTestUIState.Loading

            menuOCRReader.recognizeMenu(imageUri)
                .onSuccess { result ->
                    _uiState.value = MLKitTestUIState.MenuRecognized(result)
                }
                .onFailure { exception ->
                    _uiState.value = MLKitTestUIState.Error(
                        exception.message ?: "Failed to recognize menu"
                    )
                }
        }
    }
    fun reset() {
        _uiState.value = MLKitTestUIState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        drinkClassifier.close()
        menuOCRReader.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLKitTestScreen(
    navigation: INavigationRouter,
    viewModel: MLKitTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var testMode by remember { mutableStateOf(TestMode.DRINK_CLASSIFICATION) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ML Kit Testing") },
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
            Text(
                text = "Select test mode:",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = testMode == TestMode.DRINK_CLASSIFICATION,
                    onClick = {
                        testMode = TestMode.DRINK_CLASSIFICATION
                        viewModel.reset()
                    },
                    label = { Text("Drink Classification") },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = testMode == TestMode.MENU_OCR,
                    onClick = {
                        testMode = TestMode.MENU_OCR
                        viewModel.reset()
                    },
                    label = { Text("Menu OCR") },
                    modifier = Modifier.weight(1f)
                )
            }

            Divider()

            PhotoPicker(
                selectedImageUri = selectedImageUri,
                onImageSelected = { uri ->
                    selectedImageUri = uri
                    viewModel.reset()
                },
                onImageRemoved = {
                    selectedImageUri = null
                    viewModel.reset()
                }
            )
            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        when (testMode) {
                            TestMode.DRINK_CLASSIFICATION -> viewModel.classifyDrink(uri)
                            TestMode.MENU_OCR -> viewModel.recognizeMenu(uri)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedImageUri != null && uiState !is MLKitTestUIState.Loading
            ) {
                Text(
                    when (testMode) {
                        TestMode.DRINK_CLASSIFICATION -> "Classify Drink"
                        TestMode.MENU_OCR -> "Recognize Menu"
                    }
                )
            }
            Divider()
            when (val state = uiState) {
                is MLKitTestUIState.Idle -> {
                    InfoCard("Select a photo and tap the button to analyze")
                }

                is MLKitTestUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MLKitTestUIState.DrinkClassified -> {
                    ResultCard(
                        title = "Classification Result",
                        content = state.result.toReadableString()
                    )
                }

                is MLKitTestUIState.MenuRecognized -> {
                    ResultCard(
                        title = "OCR Result",
                        content = state.result.toReadableString()
                    )
                }

                is MLKitTestUIState.Error -> {
                    ErrorCard(message = state.message)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ResultCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = "Error: $message",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

private enum class TestMode {
    DRINK_CLASSIFICATION,
    MENU_OCR
}