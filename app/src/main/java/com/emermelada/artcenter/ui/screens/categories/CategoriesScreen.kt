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

    var selectedCategoryName by remember { mutableStateOf("Categorías") } // Guardamos el nombre de la categoría seleccionada

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
                                fontWeight = FontWeight.Bold
                            )
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
        }
    }
}



