package com.emermelada.artcenter.ui.screens.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.emermelada.artcenter.data.model.categories.CategorySimple
import com.emermelada.artcenter.ui.UiState
import com.emermelada.artcenter.ui.navigation.Destinations
import com.emermelada.artcenter.ui.screens.MainScaffoldViewModel
import com.emermelada.artcenter.ui.theme.LoraFontFamily

@Composable
fun CategoriesScreen(
    onClickNav: (String) -> Unit,
    navController: NavController,
    viewModel: MainScaffoldViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val userRole by viewModel.userRol.collectAsState()
    val categoriesState by categoriesViewModel.categoriesState.collectAsState()

    val pastelColors = listOf(
        Color(0xFFFFC1C1), // pastel rojo claro
        Color(0xFFFFB3B3), // rosa coral pastel
        Color(0xFFFFA8A0), // coral suave
        Color(0xFFFF9999), // rojo asalmonado claro
        Color(0xFFFF8C75), // melón claro
        Color(0xFFFFB47D), // naranja claro pastel
        Color(0xFFFFC285), // melocotón pastel
        Color(0xFFFFD299), // naranja muy claro
        Color(0xFFFFE0A3), // arena pastel
        Color(0xFFFFE9A8), // beige amarillento
        Color(0xFFFFF0AA), // crema pastel
        Color(0xFFFFF5A5), // amarillo claro pastel
        Color(0xFFFFF89C), // amarillo canario suave
        Color(0xFFFFFA8B), // amarillo intenso pastel
        Color(0xFFFFFB77), // amarillo limón pastel
        Color(0xFFFFFC5C)  // amarillo brillante pastel
    )


    // Esto lanza la carga solo una vez
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Categorias",
                fontSize = 20.sp,
                color = Color.DarkGray
            )
        }
        if (userRole == "admin") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onClickNav(Destinations.CREATE_CATEGORIES) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "+ Categorías",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = LoraFontFamily
                    )
                }

                Button(
                    onClick = { onClickNav(Destinations.CREATE_SUBCATEGORIES) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "+ Subcategorías",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontFamily = LoraFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
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
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // puedes ajustar este valor si quieres menos separación
                ) {
                    itemsIndexed(categorias) { index, category ->
                        val color = pastelColors[index % pastelColors.size]
                        Button(
                            onClick = { navController.navigate("${Destinations.SUBCATEGORIES}/${category.id}")},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, Color.DarkGray)
                        ) {
                            Text(
                                text = category.nombre.uppercase(),
                                fontSize = 20.sp,
                                color = Color.DarkGray,
                                fontFamily = LoraFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            }
        }
    }
}
