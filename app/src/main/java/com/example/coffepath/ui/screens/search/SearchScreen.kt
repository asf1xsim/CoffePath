package com.example.coffepath.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffepath.navigation.INavigationRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navigation: INavigationRouter) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Cafes") },
                navigationIcon = {
                    IconButton(onClick = { navigation.returnBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Search functionality will be implemented in the next phase",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}