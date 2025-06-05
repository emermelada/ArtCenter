package com.emermelada.artcenter.ui.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emermelada.artcenter.R

/**
 * Composable que muestra el logotipo de ArtCenter.
 *
 * Este componente renderiza una imagen que ocupa todo el ancho disponible,
 * con una altura fija de 200 dp y un padding superior de 20 dp.
 *
 * La imagen se carga desde el recurso R.drawable.logoartcenter.
 */
@Composable
fun LogoArtCenter() {
    Image(
        painter = painterResource(R.drawable.logoartcenter),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 20.dp)
    )
}
