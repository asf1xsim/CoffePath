package com.example.coffepath.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CoffeeBrownLight,
    onPrimary = OnCoffeeDark,
    primaryContainer = BrownDark,
    onPrimaryContainer = OnDarkSurface,

    secondary = BrownDark,
    onSecondary = OnDarkSurface,
    secondaryContainer = CoffeeDark,
    onSecondaryContainer = OnDarkSurface,

    tertiary = CoffeeOrangeBright,
    onTertiary = OnCoffeeDark,

    background = CoffeeBackground,
    onBackground = OnDarkSurface,

    surface = CoffeeDark,
    onSurface = OnDarkSurface,
    surfaceVariant = CoffeeDark,
    onSurfaceVariant = OnDarkSurface,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = OnCoffee,
    primaryContainer = BrownLight,
    onPrimaryContainer = OnCream,

    secondary = BrownLight,
    onSecondary = OnCoffee,
    secondaryContainer = CreamLight,
    onSecondaryContainer = OnCream,

    tertiary = CoffeeOrange,
    onTertiary = OnCoffee,

    background = Color(0xFFFFFBFE),
    onBackground = OnCream,

    surface = CreamLight,
    onSurface = OnCream,
    surfaceVariant = CreamLight,
    onSurfaceVariant = OnCream,

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun CoffeePathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}