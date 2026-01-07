package com.example.coffepath.navigation

import androidx.navigation.NavController

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {

    override fun getNavController(): NavController = navController

    override fun returnBack() {
        navController.popBackStack()
    }

    override fun navigateToMainMap() {
        navController.navigate(Destination.MainMapScreen.route) {
            popUpTo(Destination.MainMapScreen.route) { inclusive = true }
        }
    }

    override fun navigateToFavorites() {
        navController.navigate(Destination.FavoritesScreen.route)
    }

    override fun navigateToSearch() {
        navController.navigate(Destination.SearchScreen.route)
    }

    override fun navigateToCafeDetail(cafeId: Long) {
        navController.navigate(Destination.CafeDetailScreen.createRoute(cafeId))
    }

    override fun navigateToAddReview(cafeId: Long) {
        navController.navigate(Destination.AddReviewScreen.createRoute(cafeId))
    }

    override fun navigateToMLKitTest() {
        navController.navigate(Destination.MLKitTestScreen.route)
    }
}