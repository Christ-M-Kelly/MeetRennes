package com.meetrennes.app.ui.theme

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


// Thème sombre
private val DarkColorScheme = darkColorScheme(
    primary = BleuRennesLight,
    onPrimary = Color.White,
    secondary = RougeBriqueLight,
    onSecondary = Color.White,
    tertiary = VertBreton,
    background = DarkBg,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White
)

// Thème clair
private val LightColorScheme = lightColorScheme(
    primary = BleuRennes,
    onPrimary = Color.White,
    secondary = RougeBrique,
    onSecondary = Color.White,
    tertiary = VertBreton,
    background = WhiteCream,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MeetRennesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // false → on garde nos couleurs et on évite d'utiliser les couleurs du fond d'écran de l'utilisateur
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
