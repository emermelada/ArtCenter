package com.emermelada.artcenter.ui.screens.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emermelada.artcenter.data.model.publications.PublicationSimple
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LoraFontFamily
import java.util.Locale
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.emermelada.artcenter.ui.theme.MutedBlue
import com.emermelada.artcenter.ui.theme.Yellow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    onClickNav: (String) -> Unit,
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val userRole by mainScaffoldViewModel.userRol.collectAsState()
    val publications by feedViewModel.publications.collectAsState()
    val isLoading by feedViewModel.isLoading.collectAsState()

    val gridState = rememberLazyStaggeredGridState()
    val page = remember { mutableStateOf(0) }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex == publications.size - 1 && !isLoading) {
                    page.value++
                    feedViewModel.loadPublications(page.value)
                }
            }
    }

    LaunchedEffect(Unit) {
        feedViewModel.loadPublications(page.value)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(publications) { publication ->
                PublicationItem(publication = publication, onClick = { /*…*/ })
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        if (userRole != "admin") {
            FloatingActionButton(
                onClick = { onClickNav(Destinations.PUBLICATION) },
                shape = CircleShape,
                containerColor = Yellow,                     // fondo amarillo
                contentColor = DarkBlue,                     // icono en azul oscuro
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .border(
                        BorderStroke(2.dp, DarkBlue),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir"
                )
            }
        }
    }
}

@Composable
fun PublicationItem(
    publication: PublicationSimple,
    onClick: () -> Unit
) {
    // Etiqueta en mayúsculas (o cadena vacía)
    val label = publication.nombre_etiqueta
        .orEmpty()
        .uppercase(Locale.getDefault())

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Etiqueta fuera de la Card, si existe
        if (label.isNotBlank()) {
            // Borde igual que la Card (DarkBlue)
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = LoraFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        color = MutedBlue,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(                          // <-- borde DarkBlue
                        BorderStroke(1.dp, DarkBlue),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(2.dp, DarkBlue),
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                AsyncImage(
                    model = publication.urlContenido,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )

                // Overlay de tres puntitos
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .border(
                                    BorderStroke(1.dp, DarkBlue),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

