package com.emermelada.artcenter.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val userRole by mainScaffoldViewModel.userRol.collectAsState()
    val publications by feedViewModel.publications.collectAsState()
    val isLoading by feedViewModel.isLoading.collectAsState()

    // State for pagination
    val listState = rememberLazyListState()
    val page = remember { mutableStateOf(0) }

    // Detect when the user scrolls to the bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItem ->
                if (lastVisibleItem == publications.size - 1 && !isLoading) {
                    // Load next page of publications
                    page.value++
                    feedViewModel.loadPublications(page.value)
                }
            }
    }

    // Cargar las primeras 20 publicaciones cuando se entra en la pantalla
    LaunchedEffect(Unit) {
        feedViewModel.loadPublications(page.value)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(publications) { publication ->
                // Aquí pones la vista de cada publicación
                Text(text = publication.urlContenido) // Ejemplo de contenido
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center) // Cambié esto de CenterHorizontally a Center
                    )
                }
            }
        }

        // Botón flotante para agregar publicaciones, visible solo para usuarios no administradores
        if (userRole != "admin") {
            FloatingActionButton(
                onClick = {
                    onClickNav(Destinations.PUBLICATION)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFF3E8A95)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir",
                    tint = Color.White
                )
            }
        }
    }
}