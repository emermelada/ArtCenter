package com.emermelada.artcenter.ui.screens.subcategories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations

/**
 * Pantalla de detalle de una subcategoría.
 *
 * Carga y muestra la subcategoría correspondiente, incluyendo secciones desplegables
 * para Historia, Características, Requerimientos y Tutoriales.
 * Permite volver al listado de subcategorías de la categoría padre.
 *
 * @param categoriaId Identificador de la categoría padre.
 * @param subcategoriaId Identificador de la subcategoría a mostrar.
 * @param onClickNav Lambda que recibe la ruta de navegación al pulsar el botón de retroceso.
 * @param subcategoryViewModel ViewModel que gestiona la carga de datos de subcategoría.
 */
@Composable
fun SubcategoryScreen(
    categoriaId: Int,
    subcategoriaId: Int,
    onClickNav: (String) -> Unit,
    subcategoryViewModel: SubcategoryViewModel = hiltViewModel()
) {
    val subcategoryState by subcategoryViewModel.subcategoryState.collectAsState()

    LaunchedEffect(Unit) {
        subcategoryViewModel.fetchSubcategory(categoriaId, subcategoriaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (subcategoryState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                Text(
                    text = (subcategoryState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
            is UiState.Success<*> -> {
                val subcategory = (subcategoryState as UiState.Success<Subcategory>).data

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subcategory.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        color = Color.DarkGray
                    )
                    IconButton(
                        onClick = { onClickNav("${Destinations.SUBCATEGORIES}/$categoriaId") },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.DarkGray
                        )
                    }
                }

                ExpandableSection(title = "Historia", content = subcategory.historia ?: "Sin historia disponible")
                ExpandableSection(title = "Características", content = subcategory.caracteristicas ?: "Sin características disponibles")
                ExpandableSection(title = "Requerimientos", content = subcategory.requerimientos ?: "Sin requerimientos disponibles")
                ExpandableSection(title = "Tutoriales", content = subcategory.tutoriales ?: "Sin tutoriales disponibles")
            }
            else -> { }
        }
    }
}

/**
 * Sección desplegable genérica con título y contenido.
 *
 * @param title Título de la sección.
 * @param content Texto que se muestra al expandir.
 */
@Composable
fun ExpandableSection(
    title: String,
    content: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.medium)
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "Contraer" else "Expandir",
                tint = Color.Black
            )
        }

        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                    .padding(10.dp)
            ) {
                Text(
                    text = content,
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}