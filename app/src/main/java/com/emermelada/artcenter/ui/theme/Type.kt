package com.emermelada.artcenter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import com.emermelada.artcenter.R

/**
 * Conjunto de fuentes de la familia Lora, que incluye:
 * - Variante regular: recurso R.font.loraregular
 * - Variante en negrita: recurso R.font.lorabold con peso FontWeight.Bold
 */
val LoraFontFamily = FontFamily(
    Font(R.font.loraregular),
    Font(R.font.lorabold, FontWeight.Bold)
)

/**
 * Definición de la tipografía predeterminada de la aplicación usando la familia Lora.
 *
 * Incluye los siguientes estilos:
 * - bodyLarge: Texto de cuerpo con peso normal, tamaño 16sp, interlineado 24sp y espaciado entre letras 0.5sp.
 * - titleLarge: Título grande con peso normal, tamaño 22sp, interlineado 28sp y sin espaciado extra.
 * - labelSmall: Etiquetas pequeñas con peso medio, tamaño 11sp, interlineado 16sp y espaciado entre letras 0.5sp.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
