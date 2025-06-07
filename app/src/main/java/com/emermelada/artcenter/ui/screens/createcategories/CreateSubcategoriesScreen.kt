package com.emermelada.artcenter.ui.screens.createcategories

import androidx.compose.foundation.clickable
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
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.theme.DarkBlue
import com.emermelada.artcenter.ui.theme.LoraFontFamily

/**
 * Pantalla para crear o editar una subcategoría.
 *
 * - Si [idCategoria] e [idSubcategoria] son distintos de cero, carga la subcategoría existente y rellena los campos.
 * - Permite seleccionar categoría (solo al crear), introducir nombre, descripción, características, requerimientos y tutoriales.
 * - Muestra mensajes de éxito o error según el estado del [viewModel].
 * - Guarda o actualiza la subcategoría al pulsar el botón "Guardar".
 *
 * @param idCategoria Identificador de la categoría a la que pertenece la subcategoría.
 * @param idSubcategoria Identificador de la subcategoría (0 para crear nueva, distinto de 0 para editar existente).
 * @param onClickNav Lambda que recibe la ruta de navegación al volver al listado de categorías.
 * @param viewModel Instancia de [CreateSubcategoriesViewModel] que gestiona la lógica de creación y actualización.
 */
@Composable
fun CreateSubcategoriesScreen(
    idCategoria: Int,
    idSubcategoria: Int,
    onClickNav: (String) -> Unit,
    viewModel: CreateSubcategoriesViewModel = hiltViewModel()
) {
    val categoriesState by viewModel.categoriesState.collectAsState()
    val subcategoryState by viewModel.subcategoryState.collectAsState()
    val subcategoryLoadedState by viewModel.subcategoryLoadedState.collectAsState()

    val categorySelected = remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var caracteristicas by remember { mutableStateOf("") }
    var requerimientos by remember { mutableStateOf("") }
    var tutoriales by remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        viewModel.fetchCategories()
        if (idCategoria != 0 && idSubcategoria != 0) {
            viewModel.loadSubcategory(idCategoria, idSubcategoria)
        }
    }

    LaunchedEffect(subcategoryLoadedState) {
        when (val state = subcategoryLoadedState) {
            is UiState.Success<*> -> {
                val subcategory = state.data as? com.emermelada.artcenter.data.model.subcategories.Subcategory
                if (subcategory != null) {
                    nombre = subcategory.nombre
                    descripcion = subcategory.historia.orEmpty()
                    caracteristicas = subcategory.caracteristicas.orEmpty()
                    requerimientos = subcategory.requerimientos.orEmpty()
                    tutoriales = subcategory.tutoriales.orEmpty()
                    categorySelected.value = (categoriesState as? UiState.Success<List<CategorySimple>>)
                        ?.data
                        ?.find { it.id == subcategory.id_categoria }
                        ?.nombre
                } else {
                    nombre = ""
                    descripcion = ""
                    caracteristicas = ""
                    requerimientos = ""
                    tutoriales = ""
                    categorySelected.value = null
                }
            }
            else -> if (idSubcategoria == 0) {
                nombre = ""
                descripcion = ""
                caracteristicas = ""
                requerimientos = ""
                tutoriales = ""
                categorySelected.value = null
            }
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
                text = if (idSubcategoria == 0) "Crear nueva subcategoría" else "Editar subcategoría",
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

        when (categoriesState) {
            is UiState.Success<*> -> {
                if (idSubcategoria == 0) {
                    val expanded = remember { mutableStateOf(false) }
                    val categories = (categoriesState as UiState.Success<List<CategorySimple>>).data
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
                            modifier = Modifier.wrapContentSize(),
                            containerColor = DarkBlue
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
                } else {
                    Text(
                        text = "Categoría: ${categorySelected.value}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = TextStyle(color = Color.DarkGray)
                    )
                }
            }
            else -> { /* No-op */ }
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
                val categoria = categorySelected.value
                if (categoria == null) {
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
                        categoria
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
                        categoria
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

        when (val state = subcategoryState) {
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
                    text = if (idSubcategoria == 0)
                        "Subcategoría creada correctamente."
                    else
                        "Subcategoría actualizada correctamente.",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> { /* No-op */ }
        }
    }
}
