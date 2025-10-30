package com.example.vinylsapp.ui.theme

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
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VinylPurple,              // Botón FAB y elementos principales
    secondary = VinylPurpleLight,       // Elementos secundarios
    tertiary = Pink80,
    background = VinylDark,             // Fondo principal oscuro
    surface = VinylDarkSurface,         // Superficie de cards
    onPrimary = Color.White,            // Texto sobre elementos primary
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,         // Texto sobre fondo
    onSurface = Color.White,            // Texto sobre superficie
    surfaceVariant = VinylDarkSurface,
    onSurfaceVariant = VinylGray        // Texto secundario
)

private val LightColorScheme = lightColorScheme(
    primary = VinylPurple,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = VinylDark,             // Mantener fondo oscuro incluso en modo claro
    surface = VinylDarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = VinylDarkSurface,
    onSurfaceVariant = VinylGray
)

@Composable
fun VinylsAppTheme(
    darkTheme: Boolean = true,  // Forzar tema oscuro por defecto
    // Dynamic color deshabilitado para mantener colores personalizados
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Siempre usar el esquema oscuro para mantener el estilo del diseño
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = VinylDark.toArgb()  // Barra de estado oscura
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
