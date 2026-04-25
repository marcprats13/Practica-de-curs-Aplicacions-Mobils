package com.example.tripletriad.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TripleTriadColorScheme = darkColorScheme(
    primary          = TtBluePrimary,
    onPrimary        = TtTextPrimary,
    secondary        = TtGold,
    onSecondary      = TtBgDeep,
    tertiary         = TtBlueLight,
    background       = TtBgDeep,
    onBackground     = TtTextPrimary,
    surface          = TtBgSurface,
    onSurface        = TtTextPrimary,
    surfaceVariant   = TtBgCard,
    onSurfaceVariant = TtTextSecondary,
    outline          = TtBorder,
    error            = TtOpponentRed,
)

@Composable
fun TripleTriadTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TtBgDeep.toArgb()
            window.navigationBarColor = TtBgDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = TripleTriadColorScheme,
        typography  = Typography,
        content     = content
    )
}