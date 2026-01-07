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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coffepath.navigation.INavigationRouter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigation: INavigationRouter,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        ) {
            SettingsSectionHeader("Appearance")

            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Theme",
                subtitle = if (preferences.isDarkTheme) "Enabled" else "Disabled",
                trailing = {
                    Switch(
                        checked = preferences.isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("Language")

            SettingsItem(
                icon = Icons.Default.Language,
                title = "App Language",
                subtitle = when (preferences.languageCode) {
                    "cs" -> "Čeština"
                    else -> "English"
                },
                onClick = { showLanguageDialog = true }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("Notifications")

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = if (preferences.notificationsEnabled) "Enabled" else "Disabled",
                trailing = {
                    Switch(
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { viewModel.updateNotifications(it) }
                    )
                }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("About")

            SettingsItem(
                icon = Icons.Default.Info,
                title = "App Version",
                subtitle = "1.0.0"
            )

            SettingsItem(
                icon = Icons.Default.RestartAlt,
                title = "Reset Settings",
                subtitle = "Reset all settings to default",
                onClick = { showResetDialog = true }
            )
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
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
                                    contentDescription = "Selected",
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
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all settings to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}