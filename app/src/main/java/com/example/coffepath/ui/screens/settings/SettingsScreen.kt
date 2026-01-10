package com.example.coffepath.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.R
import com.example.coffepath.navigation.INavigationRouter
import com.example.coffepath.ui.screens.settings.components.SettingsItem
import com.example.coffepath.ui.screens.settings.components.SettingsSectionHeader
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigation: INavigationRouter,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            if (message.contains("Language changed")) {
                delay(100)
                (context as? android.app.Activity)?.recreate()
            } else {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsSectionHeader(stringResource(R.string.appearance))

            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.dark_theme),
                subtitle = stringResource(
                    if (preferences.isDarkTheme) R.string.enabled else R.string.disabled
                ),
                trailing = {
                    Switch(
                        checked = preferences.isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader(stringResource(R.string.language))

            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.app_language),
                subtitle = viewModel.getLanguageDisplayName(preferences.languageCode),
                onClick = { showLanguageDialog = true }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader(stringResource(R.string.notifications))

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.push_notifications),
                subtitle = stringResource(
                    if (preferences.notificationsEnabled) R.string.enabled else R.string.disabled
                ),
                trailing = {
                    Switch(
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { viewModel.updateNotifications(it) }
                    )
                }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader(stringResource(R.string.about))

            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.app_version),
                subtitle = "1.0.0"
            )

            SettingsItem(
                icon = Icons.Default.RestartAlt,
                title = stringResource(R.string.reset_settings),
                subtitle = stringResource(R.string.reset_settings_subtitle),
                onClick = { showResetDialog = true }
            )
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                Column {
                    viewModel.getAvailableLanguages().forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.changeLanguage(code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name)
                            if (preferences.languageCode == code) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = stringResource(R.string.selected),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_settings)) },
            text = { Text(stringResource(R.string.reset_settings_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetDialog = false
                    }
                ) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}