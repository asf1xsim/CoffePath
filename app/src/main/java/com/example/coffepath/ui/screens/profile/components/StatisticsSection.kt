package com.example.coffepath.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Card
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.coffepath.R

@Composable
fun StatisticsSection(
    reviewsCount: Int,
    favoritesCount: Int,
    visitedCafesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem(
                icon = Icons.Default.RateReview,
                value = reviewsCount.toString(),
                label = stringResource(R.string.reviews)
            )

            VerticalDivider(modifier = Modifier.height(60.dp))

            StatisticItem(
                icon = Icons.Default.Favorite,
                value = favoritesCount.toString(),
                label = stringResource(R.string.favorites)
            )

            VerticalDivider(modifier = Modifier.height(60.dp))

            StatisticItem(
                icon = Icons.Default.Place,
                value = visitedCafesCount.toString(),
                label = stringResource(R.string.visited)
            )
        }
    }
}