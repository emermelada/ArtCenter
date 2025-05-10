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
    onClickNav: (String) -> Unit,
    viewModel: CreateSubcategoriesViewModel = hiltViewModel()
) {
    // Obtener el estado de las categorías
    val categoriesState = viewModel.categoriesState.collectAsState()
    val subcategoryState = viewModel.subcategoryState.collectAsState()

    // Variables para manejar los campos de texto
    val categorySelected = remember { mutableStateOf<String?>(null) }
    val nombre = remember { mutableStateOf("") }
    val descripcion = remember { mutableStateOf("") }
    val caracteristicas = remember { mutableStateOf("") }
    val requerimientos = remember { mutableStateOf("") }
    val tutoriales = remember { mutableStateOf("") }

    // Estado para mostrar errores
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Cargar las categorías
    LaunchedEffect(true) {
        viewModel.fetchCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado con botón de retroceso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crear nueva subcategoría",
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

        // Dropdown para seleccionar categoría
        val expanded = remember { mutableStateOf(false) }
        categoriesState.value.let { state ->
            when (state) {
                is UiState.Success<*> -> {
                    val categories = state.data as List<CategorySimple>
                    Text(
                        text = categorySelected.value ?: "Selecciona una categoría",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { expanded.value = !expanded.value }
                            .padding(16.dp),
                        style = TextStyle(color = Color.DarkGray)
                    )

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                {
                                    Text(text = category.nombre)
                                },
                                onClick = {
                                    categorySelected.value = category.nombre
                                    expanded.value = false
                                })
                        }
                    }
                }
                else -> {}
            }
        }

        // Campos de texto
        OutlinedTextField(
            value = nombre.value,
            onValueChange = { nombre.value = it },
            label = { Text("Nombre", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = descripcion.value,
            onValueChange = { descripcion.value = it },
            label = { Text("Descripción", color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = caracteristicas.value,
            onValueChange = { caracteristicas.value = it },
            label = { Text("Características", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = requerimientos.value,
            onValueChange = { requerimientos.value = it },
            label = { Text("Requerimientos", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        OutlinedTextField(
            value = tutoriales.value,
            onValueChange = { tutoriales.value = it },
            label = { Text("Tutoriales", color = Color.DarkGray) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.DarkGray)
        )

        // Mensajes de error o éxito
        errorMessage.value?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Botón para crear subcategoría
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val categoriaSeleccionada = categorySelected.value
                if (categoriaSeleccionada != null) {
                    viewModel.createSubcategory(
                        nombre.value,
                        descripcion.value,
                        caracteristicas.value,
                        requerimientos.value,
                        tutoriales.value,
                        categoriaSeleccionada
                    )
                } else {
                    errorMessage.value = "Debe seleccionar una categoría."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Crear Subcategoría", color = Color.White, fontFamily = LoraFontFamily)
        }

        // Manejo de estado de subcategoría
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
                    text = "Subcategoría creada correctamente.",
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> {}
        }
    }
}
