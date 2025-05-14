package com.emermelada.artcenter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color


// Definición de la paleta de colores inspirada en La noche estrellada de Van Gogh
val DarkBlue = Color(0xFF1E2A47)  // Azul oscuro, inspirado en el cielo nocturno
val MutedBlue = Color(0xFF335B72)  // Azul apagado, en referencia a los tonos del cielo
val LightBlue = Color(0xFF4F7B8A)  // Azul suave, para los detalles de las estrellas
val Yellow = Color(0xFFFFE600)  // Amarillo brillante, inspirado en las estrellas
val LightGray = Color(0xFFB4C6C1)  // Gris claro, para los elementos de fondo más suaves

// Colores de fondo y superficie
val background = LightGray  // Fondo más claro inspirado en la calma del cielo nocturno
val surface = MutedBlue
val onPrimary = Color.White
val onSecondary = Color.White
val onBackground = Color.Black
val onSurface = Color.DarkGray

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlue,
    secondary = Yellow,
    background = background,
    surface = surface,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = Yellow,
    background = background,
    surface = surface,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface
)

// Función principal para aplicar el tema
@Composable
fun ArtCenterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        content = content
    )
}
