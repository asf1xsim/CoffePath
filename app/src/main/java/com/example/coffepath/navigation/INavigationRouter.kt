package com.example.coffepath.navigation

import androidx.navigation.NavController

interface INavigationRouter {
    fun getNavController(): NavController
    fun returnBack()
    fun navigateToMainMap()
    fun navigateToFavorites()
    fun navigateToSearch()
    fun navigateToCafeDetail(cafeId: Long)
    fun navigateToAddReview(cafeId: Long)
    fun navigateToProfile()
    fun navigateToSettings()
}