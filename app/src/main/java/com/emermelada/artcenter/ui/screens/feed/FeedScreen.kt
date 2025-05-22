package com.emermelada.artcenter.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel

@Composable
fun FeedScreen(
    onClickNav: (String) -> Unit,
    viewModel: MainScaffoldViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRol.collectAsState()

    // Contenedor principal
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // El contenido del Feed (lista, publicaciones, etc.)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Tu código para mostrar el feed
        }

        // Mostrar el botón flotante solo si el usuario es "normal" y está en la pantalla de Feed
        if (userRole != "admin") {
            FloatingActionButton(
                onClick = {
                    // Acción para cuando el usuario hace clic en el botón (agregar una nueva publicación, por ejemplo)
                    onClickNav(Destinations.PUBLICATION) // Cambia esta ruta por la que desees
                },
                modifier = Modifier
                    .padding(16.dp) // Ajusta el padding según el espacio que quieras dejar alrededor
                    .align(Alignment.BottomEnd), // Ubicación flotante en la parte inferior derecha
                containerColor = Color(0xFF3E8A95) // Color del botón
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Icono de añadir
                    contentDescription = "Añadir",
                    tint = Color.White // Color del icono
                )
            }
        }
    }
}

