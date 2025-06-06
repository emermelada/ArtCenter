package com.emermelada.artcenter.ui.screens.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.ui.components.publication.PublicationItem
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.Yellow

@Composable
fun FeedScreen(
    onClickNav: (String) -> Unit,
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val userRole by mainScaffoldViewModel.userRol.collectAsState()
    val userId by mainScaffoldViewModel.userId.collectAsState()

    val userIdInt = userId.toIntOrNull() ?: -1

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
                PublicationItem(
                    publication = publication,
                    userRole = userRole,
                    isOwner = publication.id_usuario == userIdInt,
                    onClickNav = onClickNav,
                    onSave = { feedViewModel.toggleSave(publication) },
                    onLike = { feedViewModel.toggleLike(publication) },
                    onDelete = { feedViewModel.deletePublication(publication.id)}
                )
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