package com.example.coffepath.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.MainMapScreen.route
) {
    val navigation = remember(navController) {
        NavigationRouterImpl(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

    }
}
