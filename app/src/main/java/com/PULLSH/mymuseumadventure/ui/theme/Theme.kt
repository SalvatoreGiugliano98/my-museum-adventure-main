package com.PULLSH.mymuseumadventure.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = Color(0xFFEAEAEA),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

val segmentedButtonColors = SegmentedButtonColors(
    activeContainerColor = primary,
    activeContentColor = Color.White,
    activeBorderColor = Color.DarkGray,
    inactiveContainerColor = Color.Transparent,
    inactiveContentColor = Color.Black,
    inactiveBorderColor = Color.LightGray,
    disabledActiveContainerColor = Color.DarkGray,
    disabledActiveContentColor = Color.LightGray,
    disabledActiveBorderColor = Color.LightGray,
    disabledInactiveContainerColor = Color.DarkGray,
    disabledInactiveContentColor = Color.LightGray,
    disabledInactiveBorderColor = Color.LightGray
)

@SuppressLint("ObsoleteSdkInt")
@Composable
fun MyMuseumAdventureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
        content = content,
    )
}