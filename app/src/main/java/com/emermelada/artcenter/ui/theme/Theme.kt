package com.emermelada.artcenter.ui.theme

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

/**
 * Azul oscuro inspirado en el cielo nocturno.
 */
val DarkBlue = Color(0xFF1E2A47)

/**
 * Azul apagado en referencia a los tonos del cielo.
 */
val MutedBlue = Color(0xFF335B72)

/**
 * Azul suave para los detalles de las estrellas.
 */
val LightBlue = Color(0xFF4F7B8A)

/**
 * Amarillo brillante inspirado en las estrellas.
 */
val Yellow = Color(0xFFFFF9C4)

/**
 * Gris claro para los elementos de fondo más suaves.
 */
val LightGray = Color(0xFFB4C6C1)

/**
 * Fondo más claro inspirado en la calma del cielo nocturno.
 */
val background = LightGray

/**
 * Color de superficie para componentes principales.
 */
val surface = MutedBlue

/**
 * Color de contenido que se mostrará sobre elementos primarios.
 */
val onPrimary = Color.White

/**
 * Color de contenido que se mostrará sobre elementos secundarios.
 */
val onSecondary = Color.White

/**
 * Color de contenido que se mostrará sobre el fondo.
 */
val onBackground = Color.Black

/**
 * Color de contenido que se mostrará sobre la superficie.
 */
val onSurface = Color.DarkGray

/**
 * Esquema de colores para tema oscuro.
 *
 * @property primary Color principal del tema.
 * @property secondary Color secundario del tema.
 * @property background Color de fondo.
 * @property surface Color de superficie.
 * @property onPrimary Color de contenido sobre el elemento primario.
 * @property onSecondary Color de contenido sobre el elemento secundario.
 * @property onBackground Color de contenido sobre el fondo.
 * @property onSurface Color de contenido sobre la superficie.
 */
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

/**
 * Esquema de colores para tema claro.
 *
 * @property primary Color principal del tema.
 * @property secondary Color secundario del tema.
 * @property background Color de fondo.
 * @property surface Color de superficie.
 * @property onPrimary Color de contenido sobre el elemento primario.
 * @property onSecondary Color de contenido sobre el elemento secundario.
 * @property onBackground Color de contenido sobre el fondo.
 * @property onSurface Color de contenido sobre la superficie.
 */
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

/**
 * Aplica el tema ArtCenter a la interfaz de usuario.
 *
 * @param darkTheme Indica si se debe usar el esquema de colores oscuro. Por defecto, se obtiene del sistema.
 * @param dynamicColor Indica si se deben usar colores dinámicos en Android 12 o superior.
 * @param content Contenido de la interfaz que usará el tema aplicado.
 */
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
