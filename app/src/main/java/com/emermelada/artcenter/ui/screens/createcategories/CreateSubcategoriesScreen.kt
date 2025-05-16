package com.emermelada.artcenter.ui.screens.createcategories

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.LoraFontFamily

@Composable
fun CreateSubcategoriesScreen(
    idCategoria: Int,
    idSubcategoria: Int,
    onClickNav: (String) -> Unit,
    viewModel: CreateSubcategoriesViewModel = hiltViewModel()
) {
    val categoriesState = viewModel.categoriesState.collectAsState()
    val subcategoryState = viewModel.subcategoryState.collectAsState()
    val subcategoryLoadedState = viewModel.subcategoryLoadedState.collectAsState()

    val categorySelected = remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var caracteristicas by remember { mutableStateOf("") }
    var requerimientos by remember { mutableStateOf("") }
    var tutoriales by remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val expanded = remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.fetchCategories()
        if (idCategoria != 0 && idSubcategoria != 0) {
            viewModel.loadSubcategory(idCategoria, idSubcategoria)
        }
    }

    LaunchedEffect(subcategoryLoadedState.value) {
        val state = subcategoryLoadedState.value
        if (state is UiState.Success<*>) {
            val subcategory = state.data as? com.emermelada.artcenter.data.model.subcategories.Subcategory
            if (subcategory != null) {
                nombre = subcategory.nombre ?: ""
                descripcion = subcategory.historia ?: ""
                caracteristicas = subcategory.caracteristicas ?: ""
                requerimientos = subcategory.requerimientos ?: ""
                tutoriales = subcategory.tutoriales ?: ""

                val catName = (categoriesState.value as? UiState.Success<List<CategorySimple>>)
                    ?.data?.find { it.id == subcategory.id_categoria }?.nombre
                categorySelected.value = catName
            } else {
                // Si quieres, limpia los campos aquí también
                nombre = ""
                descripcion = ""
                caracteristicas = ""
                requerimientos = ""
                tutoriales = ""
                categorySelected.value = null
            }
        } else if (idSubcategoria == 0) {
            nombre = ""
            descripcion = ""
            caracteristicas = ""
            requerimientos = ""
            tutoriales = ""
            categorySelected.value = null
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = if (idSubcategoria == 0) "Crear nueva subcategoría" else "Editar subcategoría",
                fontSize = 20.sp,
                color = Color.DarkGray
            )

            IconButton(
                onClick = { onClickNav(Destinations.CATEGORIES) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = categoriesState.value) {
            is UiState.Success<*> -> {
                // Sólo mostrar el dropdown de categorías si es creación (idSubcategoria == 0)
                if (idSubcategoria == 0) {
                    // Dropdown para seleccionar categoría
                    val expanded = remember { mutableStateOf(false) }
                    categoriesState.value.let { state ->
                        when (state) {
                            is UiState.Success<*> -> {
                                val categories = state.data as List<CategorySimple>
                                Box(
                                    modifier = Modifier
                                        .wrapContentSize(Alignment.TopStart)  // Ajusta tamaño al contenido y alinea arriba a la izquierda
                                ) {
                                    Text(
                                        text = categorySelected.value ?: "Selecciona una categoría",
                                        modifier = Modifier
                                            .clickable { expanded.value = !expanded.value }
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        style = TextStyle(color = Color.DarkGray)
                                    )

                                    DropdownMenu(
                                        expanded = expanded.value,
                                        onDismissRequest = { expanded.value = false },
                                        modifier = Modifier.wrapContentSize()  // No ocupa todo el ancho, sólo el necesario
                                    ) {
                                        categories.forEach { category ->
                                            DropdownMenuItem(
                                                text = { Text(text = category.nombre, color = Color.White) },
                                                onClick = {
                                                    categorySelected.value = category.nombre
                                                    expanded.value = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                } else {
                    // En edición, muestra la categoría seleccionada sin posibilidad de cambiar
                    Text(
                        text = "Categoría: ${categorySelected.value}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = TextStyle(color = Color.DarkGray)
                    )
                }
            }
            else -> {}
        }

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

        OutlinedTextField(
            value = caracteristicas,
            onValueChange = { caracteristicas = it },
            label = { Text("Características", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = requerimientos,
            onValueChange = { requerimientos = it },
            label = { Text("Requerimientos", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = tutoriales,
            onValueChange = { tutoriales = it },
            label = { Text("Tutoriales", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        errorMessage.value?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val categoriaSeleccionada = categorySelected.value
                if (categoriaSeleccionada == null) {
                    errorMessage.value = "Debe seleccionar una categoría."
                    return@Button
                }
                if (nombre.isBlank() || descripcion.isBlank()) {
                    errorMessage.value = "Completa todos los campos."
                    return@Button
                }
                errorMessage.value = null

                if (idSubcategoria == 0) {
                    viewModel.createSubcategory(
                        nombre,
                        descripcion,
                        caracteristicas,
                        requerimientos,
                        tutoriales,
                        categoriaSeleccionada
                    )
                } else {
                    viewModel.updateSubcategory(
                        idCategoria,
                        idSubcategoria,
                        nombre,
                        descripcion,
                        caracteristicas,
                        requerimientos,
                        tutoriales,
                        categoriaSeleccionada
                    )
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

        when (val state = subcategoryState.value) {
            is UiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            is UiState.Success<*> -> {
                Text(
                    text = if (idSubcategoria == 0) "Subcategoría creada correctamente." else "Subcategoría actualizada correctamente.",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> {}
        }
    }
}

