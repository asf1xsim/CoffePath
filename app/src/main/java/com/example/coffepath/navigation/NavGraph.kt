package com.example.coffepath.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffepath.ui.screens.detail.CafeDetailScreen
import com.example.coffepath.ui.screens.favorites.FavoritesScreen
import com.example.coffepath.ui.screens.map.MapScreen
import com.example.coffepath.ui.screens.mlkit.MLKitTestScreen
import com.example.coffepath.ui.screens.profile.ProfileScreen
import com.example.coffepath.ui.screens.review.AddReviewScreen
import com.example.coffepath.ui.screens.search.SearchScreen
import com.example.coffepath.ui.screens.settings.SettingsScreen

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
        composable(Destination.MainMapScreen.route) {
            MapScreen(navigation = navigation)
        }

        composable(Destination.FavoritesScreen.route) {
            FavoritesScreen(navigation = navigation)
        }

        composable(Destination.SearchScreen.route) {
            SearchScreen(navigation = navigation)
        }

        composable(Destination.ProfileScreen.route) {
            ProfileScreen(navigation = navigation)
        }

        composable(Destination.SettingsScreen.route) {
            SettingsScreen(navigation = navigation)
        }

        composable(
            route = Destination.CafeDetailScreen.route,
            arguments = listOf(
                navArgument("cafeId") { type = NavType.LongType }
            )
        ) {
            CafeDetailScreen(navigation = navigation)
        }

        composable(
            route = Destination.AddReviewScreen.route,
            arguments = listOf(
                navArgument("cafeId") { type = NavType.LongType }
            )
        ) {
            AddReviewScreen(navigation = navigation)
        }

        composable(Destination.MLKitTestScreen.route) {
            MLKitTestScreen(navigation = navigation)
        }
    }
}