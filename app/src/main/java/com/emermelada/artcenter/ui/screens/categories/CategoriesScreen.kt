package com.emermelada.artcenter.ui.screens.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.LightBlue
import com.emermelada.artcenter.ui.theme.LoraFontFamily
import com.emermelada.artcenter.ui.theme.MutedBlue

@Composable
fun CategoriesScreen(
    onClickNav: (String) -> Unit,
    navController: NavController,
    viewModel: MainScaffoldViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRol.collectAsState()
    val categoriesState by categoriesViewModel.categoriesState.collectAsState()
    var selectedCategoryName by remember { mutableStateOf("Categorías") } // Guardamos el nombre de la categoría

    LaunchedEffect(Unit) {
        categoriesViewModel.fetchCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (categoriesState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(
                    text = (categoriesState as UiState.Error).message,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            is UiState.Success<*> -> {
                val categorias = (categoriesState as UiState.Success<List<CategorySimple>>).data

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(categorias) { index, category ->
                        var menuExpanded by remember { mutableStateOf(false) }

                        // Contenedor para el botón de categoría y el icono
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Si es administrador, alineamos a la izquierda, sino al centro
                                if (userRole == "admin") {
                                    // Administrador: Icono a la derecha y texto a la izquierda
                                    Button(
                                        onClick = {
                                            selectedCategoryName = category.nombre  // Actualiza el nombre de la categoría
                                            navController.navigate("${Destinations.SUBCATEGORIES}/${category.id}")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp),
                                        colors = ButtonDefaults.buttonColors(MutedBlue),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(2.dp, LightBlue)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = category.nombre.uppercase(),
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                fontFamily = LoraFontFamily,
                                                fontWeight = FontWeight.Bold
                                            )
                                            // Icono de ajustes para administradores
                                            IconButton(
                                                onClick = { menuExpanded = !menuExpanded },
                                                modifier = Modifier
                                                    .padding(start = 8.dp) // Asegura que el icono tenga margen
                                            ) {
                                                Icon(
                                                    Icons.Default.Settings,
                                                    contentDescription = "Ajustes",
                                                    tint = Color.Black
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Usuario normal: texto centrado
                                    Button(
                                        onClick = {
                                            selectedCategoryName = category.nombre  // Actualiza el nombre de la categoría
                                            navController.navigate("${Destinations.SUBCATEGORIES}/${category.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp),
                                        colors = ButtonDefaults.buttonColors(MutedBlue),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(2.dp, LightBlue)
                                    ) {
                                        Text(
                                            text = category.nombre.uppercase(),
                                            fontSize = 20.sp,
                                            color = Color.White,
                                            fontFamily = LoraFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth(), // Centra el texto
                                            textAlign = TextAlign.Center // Asegura que el texto esté centrado
                                        )
                                    }
                                }
                            }

                            // Desplegable del menú que aparece a la derecha, debajo del icono
                            if (menuExpanded) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd) // Alineación a la derecha
                                        .offset(x = (-100).dp, y = 65.dp)  // Ajusta el menú para que aparezca justo debajo
                                ) {
                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Cerrar sesión", color = Color.White) },
                                            onClick = {
                                                menuExpanded = false
                                                // Acción de cerrar sesión
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Eliminar", color = Color.White) },
                                            onClick = {
                                                menuExpanded = false
                                                // Acción de eliminar categoría
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Admin section for category management
        if (userRole == "admin") {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = { onClickNav(Destinations.CREATE_CATEGORIES) },
                    modifier = Modifier
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "addCategory", tint = Color.White)
                    Text(
                        text = " Categorías",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = LoraFontFamily
                    )
                }

                Button(
                    onClick = { onClickNav(Destinations.CREATE_SUBCATEGORIES) },
                    modifier = Modifier
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "addSubcategory", tint = Color.White)
                    Text(
                        text = " Subcategorías",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = LoraFontFamily
                    )
                }
            }
        }
    }
}