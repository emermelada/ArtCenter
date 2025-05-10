package com.emermelada.artcenter.ui.screens.subcategories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.subcategories.Subcategory
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations

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
                        onClick = { onClickNav("${Destinations.SUBCATEGORIES}/${categoriaId}") },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.DarkGray
                        )
                    }
                }

                // Creando las secciones desplegables
                ExpandableSection(title = "Historia", content = subcategory.historia ?: "Sin historia disponible")
                ExpandableSection(title = "Características", content = subcategory.caracteristicas ?: "Sin características disponibles")
                ExpandableSection(title = "Requerimientos", content = subcategory.requerimientos ?: "Sin requerimientos disponibles")
                ExpandableSection(title = "Tutoriales", content = subcategory.tutoriales ?: "Sin tutoriales disponibles")

            }
        }
    }
}

@Composable
fun ExpandableSection(title: String, content: String) {
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
            // Título de la sección con color negro
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color.Black,
                modifier = Modifier.padding(8.dp) // Ajuste del margen interno
            )

            // Icono de expandir/contraer
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "Contraer" else "Expandir",
                tint = Color.Black
            )
        }


        // Contenido que se muestra al expandir
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                    .padding(10.dp) // Padding adicional para el contenido
            ) {
                Text(
                    text = content,
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp) // Ajuste del margen interno
                )
            }
        }
    }
}

