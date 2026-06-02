package com.example.stockhome.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ShColorScheme = lightColorScheme(
    primary = Sh.brand,
    onPrimary = Sh.surface,
    background = Sh.bg,
    onBackground = Sh.ink,
    surface = Sh.surface,
    onSurface = Sh.ink,
)

@Composable
fun StockHomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShColorScheme,
        typography = ShTypography,
        content = content,
    )
}
