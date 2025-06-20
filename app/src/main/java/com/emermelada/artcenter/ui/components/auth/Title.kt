package com.emermelada.artcenter.ui.components.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emermelada.artcenter.R

/**
 * Composable que muestra el título de la pantalla de autenticación.
 *
 * Renderiza un texto centrado en la parte superior, usando la fuente personalizada "Kuchek"
 * y los estilos definidos en el tema de Material.
 *
 * El texto que se muestra se obtiene de los recursos con la clave R.string.TitleAuth,
 * y su color es el recurso R.color.black.
 */
@Composable
fun Title() {
    val kuchekFont = FontFamily(Font(R.font.kuchek))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.TitleAuth),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = kuchekFont,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = colorResource(R.color.black)
            )
        )
    }
}
