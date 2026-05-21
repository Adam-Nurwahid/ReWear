package com.damtoy.rewear.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ══════════════════════════════════════════════
// Light Color Scheme
// ══════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    inversePrimary = Green80,

    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal10,

    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,

    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,

    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,

    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    surfaceTint = Green40,
    scrim = Color.Black,
)

// ══════════════════════════════════════════════
// Dark Color Scheme
// ══════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    inversePrimary = Green40,

    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,

    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,

    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,

    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral6,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,

    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    surfaceTint = Green80,
    scrim = Color.Black,
)

// ══════════════════════════════════════════════
// Theme Composable
// ══════════════════════════════════════════════
@Composable
fun ReWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor to false by default to ensure brand colors are used consistently
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ReWearTypography,
        shapes = ReWearShapes,
        content = content
    )
}

// ══════════════════════════════════════════════
// Dimension Tokens
// ══════════════════════════════════════════════
object Dimens {
    val paddingSmall = 8.dp
    val paddingMedium = 16.dp
    val paddingLarge = 24.dp
    val paddingExtraLarge = 32.dp

    val cardElevation = 4.dp
    val buttonHeight = 50.dp
    val iconSize = 32.dp
    val circularProgressSize = 200.dp
    val circularProgressStroke = 16.dp

    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusLarge = 16.dp
    val cornerRadiusExtraLarge = 28.dp
}
