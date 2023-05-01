package com.fyp.pokedex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Color.Yellow,
    background =Black,
    onBackground = Color.White,
    surface = Color(0xFF303030),
    onSurface = Color.White,
    secondary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Color.Blue,
    background = LightBlue,
    onBackground = Black,
    surface = Color.White,
    onSurface = Color(0xFF121212),
    secondary = Color.DarkGray

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@Composable
fun PokedexTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

    val systemUiController = rememberSystemUiController()
    if (darkTheme) {
        systemUiController.setSystemBarsColor(
            color = Black,
        )
    } else {
        systemUiController.setSystemBarsColor(
            color = LightBlue
        )
    }
}


