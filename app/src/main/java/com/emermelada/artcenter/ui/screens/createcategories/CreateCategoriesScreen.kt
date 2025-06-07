package com.emermelada.artcenter.ui.screens.createcategories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.categories.Category
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.LoraFontFamily

/**
 * Composable que muestra la pantalla de creación o edición de una categoría.
 *
 * - Si [id] es distinto de 0, carga la categoría existente y rellena los campos.
 * - Permite introducir nombre y descripción.
 * - Muestra mensajes de éxito o error según el estado de [viewModel].
 * - Guarda o actualiza la categoría al pulsar el botón "Guardar".
 *
 * @param id Identificador de la categoría (0 para crear nueva, distinto de 0 para editar existente).
 * @param onClickNav Lambda que recibe la ruta de navegación al volver al listado de categorías.
 * @param viewModel Instancia de [CreateCategoryViewModel] que gestiona la lógica de creación/actualización.
 */
@Composable
fun CreateCategoriesScreen(
    id: Int,
    onClickNav: (String) -> Unit,
    viewModel: CreateCategoryViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val categoryState by viewModel.categoryState.collectAsState()

    LaunchedEffect(id) {
        if (id != 0) {
            viewModel.loadCategory(id)
        }
    }

    LaunchedEffect(categoryState) {
        if (categoryState is UiState.Success<*>) {
            val category = (categoryState as UiState.Success<Category>).data
            nombre = category.nombre
            descripcion = category.descripcion
        } else if (id == 0) {
            nombre = ""
            descripcion = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (id == 0) "Crear nueva categoría" else "Editar categoría",
                fontSize = 20.sp,
                color = Color.DarkGray
            )
            IconButton(
                onClick = { onClickNav(Destinations.CATEGORIES) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción", color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        when (uiState) {
            is UiState.Error -> {
                if (id != 0) {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            is UiState.Success<*> -> {
                Text(
                    text = if (id == 0)
                        "Categoría creada correctamente."
                    else
                        "Categoría actualizada correctamente.",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> { }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || descripcion.isBlank()) {
                    viewModel.setError("Completa todos los campos.")
                } else {
                    if (id == 0) {
                        viewModel.createCategory(nombre, descripcion)
                    } else {
                        viewModel.updateCategory(id, nombre, descripcion)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Guardar", color = Color.White, fontFamily = LoraFontFamily)
        }
    }
}
