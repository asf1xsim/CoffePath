package com.example.coffepath.navigation

sealed class Destination(val route: String) {
    object MainMapScreen : Destination("main_map")
    object FavoritesScreen : Destination("favorites")
    object SearchScreen : Destination("search")
    object CafeDetailScreen : Destination("cafe_detail/{cafeId}") {
        fun createRoute(cafeId: Long) = "cafe_detail/$cafeId"
    }
    object MLKitTestScreen : Destination("mlkit_test")
}