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

@Composable
fun LogoArtCenter(){
    Image(
        painter = painterResource(R.drawable.logoartcenter),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 20.dp)
    )
}