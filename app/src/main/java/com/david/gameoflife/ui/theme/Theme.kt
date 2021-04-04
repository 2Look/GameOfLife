package com.david.gameoflife.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = purple700,
    secondary = teal200
)

@Composable
fun DeleteTheme(content: @Composable () -> Unit){
    MaterialTheme (
        colors = darkColors(primary = red700),
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
fun GameOfLifeTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        colors = DarkColorPalette,
        typography = typography,
        shapes = shapes,
        content = content
    )
}