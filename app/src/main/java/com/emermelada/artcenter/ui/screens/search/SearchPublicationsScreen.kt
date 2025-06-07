package com.emermelada.artcenter.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.ui.components.publication.PublicationItem
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.DarkBlue
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable que muestra la pantalla de búsqueda de publicaciones.
 *
 * Incluye:
 * 1. Campo de búsqueda con icono.
 * 2. Grid escalonado que muestra las publicaciones filtradas.
 * 3. Paginación infinita al llegar al final del grid.
 *
 * @param onClickNav Lambda que recibe la ruta de navegación al interactuar con un elemento.
 * @param viewModel ViewModel de búsqueda que expone los resultados, el estado de carga
 *                  y las acciones de búsqueda y paginación.
 * @param mainScaffoldViewModel ViewModel principal que provee información de usuario (rol e ID).
 */
@Composable
fun SearchPublicationsScreen(
    onClickNav: (String) -> Unit,
    viewModel: SearchPublicationsViewModel = hiltViewModel(),
    mainScaffoldViewModel: MainScaffoldViewModel = hiltViewModel()
) {
    val userRole by mainScaffoldViewModel.userRol.collectAsState()
    val userId by mainScaffoldViewModel.userId.collectAsState()
    val userIdInt = userId.toIntOrNull() ?: -1

    val publications by viewModel.publications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastIndex ->
                if (lastIndex == publications.size - 1 && !isLoading) {
                    viewModel.loadMore()
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.search(it.text)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = DarkBlue
                )
            },
            placeholder = {
                CompositionLocalProvider(LocalContentColor provides DarkBlue) {
                    Text("Buscar publicaciones...")
                }
            },
            textStyle = TextStyle(color = DarkBlue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
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
                        onSave = { viewModel.toggleSave(publication) },
                        onLike = { viewModel.toggleLike(publication) },
                        onDelete = { viewModel.deletePublication(publication.id) }
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
        }
    }
}